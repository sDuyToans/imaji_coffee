package com.duytoan.imajicoffee.imaji_coffee_be.account;

import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressCreateRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.address.AddressUpdateRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.auth.PasswordRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.user.UpdateProfileRequestDto;
import com.duytoan.imajicoffee.imaji_coffee_be.dto.user.UserDto;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Address;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.exceptions.ResourceNotFoundException;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.address.AddressRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.services.address.impl.AddressServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.auth.impl.AuthServiceImpl;
import com.duytoan.imajicoffee.imaji_coffee_be.services.user.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private AuthServiceImpl authService;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(10L);
        user.setUsername("tester");
        user.setEmail("tester@example.com");
        user.setPhone("+1234567890");
        user.setPassword("encoded-old-password");

        lenient().when(userRepository.findById(10L)).thenReturn(Optional.of(user));
    }

    // --- Profile update ---

    @Test
    void updateProfile_shouldUpdateAuthenticatedUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequestDto request = new UpdateProfileRequestDto("New Name", "new@example.com", "+10987654321");
        UserDto result = userService.updateProfile(10L, request);

        assertThat(result.getUsername()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getPhone()).isEqualTo("+10987654321");
    }

    @Test
    void updateProfile_shouldRejectExistingEmail() {
        User otherUser = new User();
        otherUser.setUserId(99L);
        otherUser.setEmail("taken@example.com");
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(otherUser));

        UpdateProfileRequestDto request = new UpdateProfileRequestDto("Tester", "taken@example.com", "+1234567890");

        assertThatThrownBy(() -> userService.updateProfile(10L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is already in use");
    }

    @Test
    void updateProfileDto_shouldHaveValidationConstraints() {
        jakarta.validation.Validator validator = jakarta.validation.Validation
                .buildDefaultValidatorFactory().getValidator();

        UpdateProfileRequestDto invalid = new UpdateProfileRequestDto("A", "not-an-email", "123");

        var violations = validator.validate(invalid);

        assertThat(violations).isNotEmpty();
    }

    // --- Change password ---

    @Test
    void changePassword_shouldSucceed_whenCurrentPasswordMatches() {
        user.setTokenVersion(3);
        when(passwordEncoder.matches("oldPass", "encoded-old-password")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encoded-new-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordRequestDto request = new PasswordRequestDto("oldPass", "newPass", "newPass");
        var result = authService.changePassword(10L, request);

        assertThat(result.name()).isEqualTo("SUCCESS");
        assertThat(user.getPassword()).isEqualTo("encoded-new-password");
        assertThat(user.getTokenVersion()).isEqualTo(4);
    }

    @Test
    void changePassword_shouldRejectIncorrectCurrentPassword() {
        user.setTokenVersion(2);
        when(passwordEncoder.matches("wrongPass", "encoded-old-password")).thenReturn(false);

        PasswordRequestDto request = new PasswordRequestDto("wrongPass", "newPass", "newPass");

        assertThatThrownBy(() -> authService.changePassword(10L, request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Current password is incorrect");
        assertThat(user.getTokenVersion()).isEqualTo(2);
        assertThat(user.getPassword()).isEqualTo("encoded-old-password");
    }

    @Test
    void changePassword_shouldRejectOAuthOnlyAccount() {
        user.setCreatedBy("GOOGLE");
        user.setPassword("550e8400-e29b-41d4-a716-446655440000");

        PasswordRequestDto request = new PasswordRequestDto("anyPass", "newPass", "newPass");

        assertThatThrownBy(() -> authService.changePassword(10L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OAuth accounts cannot change a local password");
    }

    @Test
    void changePasswordDto_shouldHaveValidationConstraints() {
        jakarta.validation.Validator validator = jakarta.validation.Validation
                .buildDefaultValidatorFactory().getValidator();

        PasswordRequestDto weak = new PasswordRequestDto("oldPass", "12345", "12345");

        var violations = validator.validate(weak);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("newPassword"));
    }

    @Test
    void changePassword_shouldRejectWhenNewPasswordEqualsCurrentPassword() {
        user.setTokenVersion(5);
        PasswordRequestDto request = new PasswordRequestDto("oldPass", "oldPass", "oldPass");

        assertThatThrownBy(() -> authService.changePassword(10L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New password must differ from current password");
        assertThat(user.getTokenVersion()).isEqualTo(5);
    }

    @Test
    void changePassword_shouldRejectMismatchedConfirmationWithoutChangingVersion() {
        user.setTokenVersion(7);
        PasswordRequestDto request = new PasswordRequestDto("oldPass", "newPass", "differentPass");

        assertThatThrownBy(() -> authService.changePassword(10L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New password and confirmation do not match");
        assertThat(user.getTokenVersion()).isEqualTo(7);
    }

    // --- Address CRUD ---

    private AddressCreateRequestDto createRequest(String name, boolean isDefault) {
        return new AddressCreateRequestDto(
                name, "US", "CA", "SF", "Market St", "94105", "Apt 1", "+14155550123", isDefault
        );
    }

    private Address addressFor(User owner, Long id, boolean isDefault) {
        Address address = new Address();
        address.setAddressId(id);
        address.setUser(owner);
        address.setName("Home");
        address.setStreet("Market St");
        address.setCity("SF");
        address.setProvince("CA");
        address.setPostalCode("94105");
        address.setCountry("US");
        address.setApartment("Apt 1");
        address.setPhoneNumber("+14155550123");
        address.setDefault(isDefault);
        return address;
    }

    @Test
    void createAddress_shouldCreateFirstAddressAsDefault() {
        when(addressRepository.countByUser_UserId(10L)).thenReturn(0L);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address a = invocation.getArgument(0);
            a.setAddressId(1L);
            return a;
        });

        AddressDto result = addressService.createAddress(10L, createRequest("Home", false));

        assertThat(result.addressId()).isEqualTo(1L);
        assertThat(result.isDefault()).isTrue();
    }

    @Test
    void createAddress_shouldSetDefaultAndClearExistingDefault() {
        when(addressRepository.countByUser_UserId(10L)).thenReturn(1L);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address a = invocation.getArgument(0);
            a.setAddressId(3L);
            return a;
        });

        AddressDto result = addressService.createAddress(10L, createRequest("Office", true));

        verify(addressRepository).clearDefaultForUser(10L);
        assertThat(result.isDefault()).isTrue();
    }

    @Test
    void updateAddress_shouldUpdate_whenUserOwnsAddress() {
        Address existing = addressFor(user, 1L, false);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressUpdateRequestDto request = new AddressUpdateRequestDto(
                "Office", "US", "CA", "SF", "Market St", "94105", "Apt 2", "+14155550123", true
        );
        AddressDto result = addressService.updateAddress(10L, 1L, request);

        assertThat(result.name()).isEqualTo("Office");
        assertThat(result.apartment()).isEqualTo("Apt 2");
        assertThat(result.isDefault()).isTrue();
        verify(addressRepository).clearDefaultForUser(10L);
    }

    @Test
    void updateAddress_shouldReject_whenUserDoesNotOwnAddress() {
        User other = new User();
        other.setUserId(99L);
        Address existing = addressFor(other, 1L, false);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(existing));

        AddressUpdateRequestDto request = new AddressUpdateRequestDto(
                "Office", "US", "CA", "SF", "Market St", "94105", "Apt 2", "+14155550123", false
        );

        assertThatThrownBy(() -> addressService.updateAddress(10L, 1L, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Not authorized to update this address");
    }

    @Test
    void deleteAddress_shouldDelete_whenUserOwnsAddress() {
        Address existing = addressFor(user, 1L, false);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(existing));

        addressService.deleteAddress(10L, 1L);

        verify(addressRepository).delete(existing);
    }

    @Test
    void deleteAddress_shouldReject_whenUserDoesNotOwnAddress() {
        User other = new User();
        other.setUserId(99L);
        Address existing = addressFor(other, 1L, false);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> addressService.deleteAddress(10L, 1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Not authorized to delete this address");
    }

    @Test
    void deleteAddress_shouldPromoteAnotherDefault_whenDeletingDefaultAddress() {
        Address toDelete = addressFor(user, 1L, true);
        Address remaining = addressFor(user, 2L, false);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(toDelete));
        when(addressRepository.findByUser_UserId(10L)).thenReturn(List.of(remaining));

        addressService.deleteAddress(10L, 1L);

        assertThat(remaining.isDefault()).isTrue();
        verify(addressRepository).save(remaining);
    }

    @Test
    void setDefaultAddress_shouldEnforceOnlyOneDefault() {
        Address newDefault = addressFor(user, 2L, false);
        when(addressRepository.findById(2L)).thenReturn(Optional.of(newDefault));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressDto result = addressService.setDefaultAddress(10L, 2L);

        verify(addressRepository).clearDefaultForUser(10L);
        assertThat(result.isDefault()).isTrue();
    }

    @Test
    void getAddresses_shouldReturnOnlyCurrentUserAddresses() {
        Address a1 = addressFor(user, 1L, true);
        when(addressRepository.findByUser_UserId(10L)).thenReturn(List.of(a1));

        List<AddressDto> result = addressService.getAddressesForUser(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(10L);
    }

    @Test
    void createAddress_shouldNotUseIdFromClient() {
        when(addressRepository.countByUser_UserId(10L)).thenReturn(0L);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address a = invocation.getArgument(0);
            a.setAddressId(5L);
            return a;
        });

        AddressCreateRequestDto request = createRequest("Home", false);
        AddressDto result = addressService.createAddress(10L, request);

        assertThat(result.userId()).isEqualTo(10L);
        assertThat(result.addressId()).isEqualTo(5L);
    }

    @Test
    void updateProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UpdateProfileRequestDto request = new UpdateProfileRequestDto("Name", "a@b.com", "+1234567890");

        assertThatThrownBy(() -> userService.updateProfile(999L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
