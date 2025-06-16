package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.request.RequestAddress;
import com.sajotuna.account.domain.response.ResponseAddress;
import com.sajotuna.account.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressController {
    private final AddressService addressService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ResponseAddress> saveAddress(@Valid @RequestBody RequestAddress address, @RequestHeader("X-User-Id") Long userId) {
        if (address.getNickName() == null) {
            address.setNickName(address.getStreetAddress());
        }
        AddressDto addressDto = objectMapper.convertValue(address, AddressDto.class);
        AddressDto savedAddressDto = addressService.save(addressDto, userId);
        ResponseAddress responseAddress = objectMapper.convertValue(savedAddressDto, ResponseAddress.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseAddress);
    }

    @GetMapping
    public ResponseEntity<List<ResponseAddress>> getAddress(@RequestHeader("X-User-Id") Long userId) {
        List<ResponseAddress> responseAddresses = new ArrayList<>();
        List<AddressDto> addressDtos = addressService.getAddresses(userId);
        for (AddressDto addressDto : addressDtos) {
            ResponseAddress responseAddress = objectMapper.convertValue(addressDto, ResponseAddress.class);
            responseAddresses.add(responseAddress);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseAddresses);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@RequestHeader("X-User-Id") Long userId, @PathVariable long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
