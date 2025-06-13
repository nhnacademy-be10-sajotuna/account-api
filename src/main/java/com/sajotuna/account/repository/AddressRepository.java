package com.sajotuna.account.repository;

import com.sajotuna.account.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
