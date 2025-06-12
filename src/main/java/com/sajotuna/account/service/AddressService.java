package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.entity.Address;
import com.sajotuna.account.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final ObjectMapper objectMapper;

    public AddressDto save(AddressDto addressDto, Long userId) {
        Address address = new Address(userId, addressDto);
        Address savedAddress = addressRepository.save(address);
        return objectMapper.convertValue(savedAddress, AddressDto.class);
    }

}
