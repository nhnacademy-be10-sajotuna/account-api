package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.entity.Address;
import com.sajotuna.account.exception.Address10OverException;
import com.sajotuna.account.exception.AddressNotFoundException;
import com.sajotuna.account.repository.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AddressService addressService;

    @Test
    @DisplayName("주소 저장")
    void saveAddress() {
        AddressDto addressDto = new AddressDto();
        addressDto.setStreetAddress("test");
        addressDto.setNickName("test");
        Mockito.when(addressRepository.findByUserId(1L)).thenReturn(Optional.of(List.of(new Address())));

        addressService.save(addressDto, 1L);

        Mockito.verify(addressRepository, Mockito.times(1)).save(Mockito.any(Address.class));
        Mockito.verify(addressRepository, Mockito.times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("주소 저장 - 주소 10개로 실패")
    void saveAddressError() {
        AddressDto addressDto = new AddressDto();
        addressDto.setStreetAddress("test");
        addressDto.setNickName("test");
        List<Address> addressList = new ArrayList<>();
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        addressList.add(new Address());
        Mockito.when(addressRepository.findByUserId(1L)).thenReturn(Optional.of(addressList));

        Assertions.assertThrows(Address10OverException.class, () -> addressService.save(addressDto, 1L));

        Mockito.verify(addressRepository, Mockito.times(0)).save(Mockito.any(Address.class));
        Mockito.verify(addressRepository, Mockito.times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("주소 조회")
    void getAddresses() {
        Mockito.when(addressRepository.findByUserId(1L)).thenReturn(Optional.of(List.of(new Address())));

        addressService.getAddresses(1L);

        Mockito.verify(addressRepository, Mockito.times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("주소 삭제")
    void deleteAddress() {
        Mockito.when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(new Address()));

        addressService.deleteAddress(1L, 1L);

        Mockito.verify(addressRepository, Mockito.times(1)).findByIdAndUserId(1L, 1L);
        Mockito.verify(addressRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("주소 삭제 - 실패")
    void deleteAddress2() {
        Mockito.when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(AddressNotFoundException.class, () -> addressService.deleteAddress(1L, 1L));



        Mockito.verify(addressRepository, Mockito.times(1)).findByIdAndUserId(1L, 1L);
        Mockito.verify(addressRepository, Mockito.times(0)).deleteById(1L);
    }


}