package com.duytoan.imajicoffee.imaji_coffee_be.repository.address;

import com.duytoan.imajicoffee.imaji_coffee_be.entities.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Address repository
 * @author duytoan
 * @since 10/2025
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
    /**
     * Find address by userId
     * @param userId
     * @return address list
     */
    List<Address> findByUser_UserId(Long userId);

    /**
     * Count address of a user by user id
     * @param userId
     * @return count
     */
    @Query("select count(a) from Address a where a.user.userId = :userId")
    long countByUser_UserId(@Param("userId") Long userId);

    /**
     * Clear default address
     * @param userId
     */
    @Modifying
    @Query("update Address a set a.isDefault = false where a.user.userId = :userId")
    void clearDefaultForUser(@Param("userId") Long userId);

    /**
     * Find address by multiple fields
     * @param userId
     * @param country
     * @param city
     * @param street
     * @param postalCode
     * @param apartment
     * @param phoneNumber
     * @return optional address object
     */
    @Query("""
                    select a from Address a 
                    where a.user.userId = :userId and
                    a.country = :country and 
                    a.city = :city and
                    a.street = :street and
                    a.postalCode = :postalCode and
                    a.apartment = :apartment and
                    a.phoneNumber = :phoneNumber
            """)
    Optional<Address> findIdenticalAddress(
            @Param("userId") Long userId,
            @Param("country") String country,
            @Param("city") String city,
            @Param("street") String street,
            @Param("postalCode") String postalCode,
            @Param("apartment") String apartment,
            @Param("phoneNumber") String phoneNumber
    );
}
