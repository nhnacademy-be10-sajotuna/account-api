package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<List<Address>> findByUserId(Long userId);

    Optional<Address> findByIdAndUserId(Long addressId, Long userId);
}
