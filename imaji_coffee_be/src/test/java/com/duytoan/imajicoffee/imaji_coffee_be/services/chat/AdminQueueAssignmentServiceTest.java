package com.duytoan.imajicoffee.imaji_coffee_be.services.chat;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.chat.ChatConversation;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Role;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.User;
import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.UserRole;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.ConversationStatus;
import com.duytoan.imajicoffee.imaji_coffee_be.enums.RoleName;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.auth.UserRoleRepository;
import com.duytoan.imajicoffee.imaji_coffee_be.repository.chat.ChatConversationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminQueueAssignmentServiceTest {

    @Mock
    private ChatConversationRepository chatConversationRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private AdminQueueAssignmentService adminQueueAssignmentService;

    @Test
    void assignConversationToLeastBusyAdmin_assignsLeastBusyAdmin() {
        User admin1 = new User();
        admin1.setUserId(10L);
        User admin2 = new User();
        admin2.setUserId(20L);

        UserRole adminRole1 = new UserRole();
        adminRole1.setUser(admin1);
        Role role1 = new Role();
        role1.setName(RoleName.ROLE_ADMIN);
        adminRole1.setRole(role1);

        UserRole adminRole2 = new UserRole();
        adminRole2.setUser(admin2);
        Role role2 = new Role();
        role2.setName(RoleName.ROLE_ADMIN);
        adminRole2.setRole(role2);

        when(userRoleRepository.findAllByRole_Name(RoleName.ROLE_ADMIN)).thenReturn(List.of(adminRole1, adminRole2));
        when(chatConversationRepository.countByAssignedAdminIdAndStatusIn(10L, List.of(ConversationStatus.OPEN, ConversationStatus.PENDING)))
                .thenReturn(5L);
        when(chatConversationRepository.countByAssignedAdminIdAndStatusIn(20L, List.of(ConversationStatus.OPEN, ConversationStatus.PENDING)))
                .thenReturn(1L);
        ChatConversation conversation = ChatConversation.builder()
                .id(1L)
                .customerId(100L)
                .status(ConversationStatus.OPEN)
                .build();

        ChatConversation assigned = adminQueueAssignmentService.assignConversationToLeastBusyAdmin(conversation);

        assertThat(assigned.getAssignedAdminId()).isEqualTo(20L);
        assertThat(assigned.getStatus()).isEqualTo(ConversationStatus.PENDING);
    }

    @Test
    void assignConversationToLeastBusyAdmin_leavesConversationUnassignedWhenNoAdminsExist() {
        when(userRoleRepository.findAllByRole_Name(RoleName.ROLE_ADMIN)).thenReturn(List.of());

        ChatConversation conversation = ChatConversation.builder()
                .id(1L)
                .customerId(100L)
                .status(ConversationStatus.OPEN)
                .build();

        ChatConversation assigned = adminQueueAssignmentService.assignConversationToLeastBusyAdmin(conversation);

        assertThat(assigned.getAssignedAdminId()).isNull();
        assertThat(assigned.getStatus()).isEqualTo(ConversationStatus.WAITING);
        verify(chatConversationRepository, never()).save(org.mockito.ArgumentMatchers.any(ChatConversation.class));
    }

    @Test
    void getWaitingQueuePosition_countsAheadInQueue() {
        when(chatConversationRepository.countByStatusAndIdLessThan(ConversationStatus.WAITING, 5L)).thenReturn(2L);

        long position = adminQueueAssignmentService.getWaitingQueuePosition(5L);

        assertThat(position).isEqualTo(3L);
    }

    @Test
    void closeConversation_clearsAdminAndClosesConversation() {
        ChatConversation conversation = ChatConversation.builder()
                .id(1L)
                .customerId(100L)
                .assignedAdminId(20L)
                .status(ConversationStatus.OPEN)
                .build();
        when(chatConversationRepository.save(org.mockito.ArgumentMatchers.any(ChatConversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatConversation closed = adminQueueAssignmentService.closeConversation(conversation);

        assertThat(closed.getStatus()).isEqualTo(ConversationStatus.CLOSED);
        assertThat(closed.getAssignedAdminId()).isNull();
        verify(chatConversationRepository).save(conversation);
    }
}
