package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.entity.Address;
import com.sajotuna.account.exception.AddressNotFoundException;
import com.sajotuna.account.exception.UserNotFoundException;
import com.sajotuna.account.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {
    private final AddressRepository addressRepository;
    private final ObjectMapper objectMapper;

    public AddressDto save(AddressDto addressDto, Long userId) {
        Address address = new Address(userId, addressDto);
        Address savedAddress = addressRepository.save(address);
        return objectMapper.convertValue(savedAddress, AddressDto.class);
    }

    public List<AddressDto> getAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId).get();
        List<AddressDto> addressDtos = new ArrayList<>();
        for (Address address : addresses) {
            addressDtos.add(objectMapper.convertValue(address, AddressDto.class));
        }
        return addressDtos;
    }

    public void deleteAddress(Long userId, Long addressId) {
        addressRepository.findByIdAndUserId(addressId, userId).orElseThrow(()-> new AddressNotFoundException(userId));
        addressRepository.deleteById(addressId);
    }

}
