package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.request.LoginRequestUser;
import com.sajotuna.account.domain.request.RequestAddress;
import com.sajotuna.account.service.AddressService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
@ActiveProfiles("test")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressService addressService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("주소 저장")
    void saveAddress() throws Exception {
        RequestAddress address = new RequestAddress();
        address.setNickName("test");
        address.setStreetAddress("test");
        AddressDto addressDto = objectMapper.convertValue(address, AddressDto.class);

        Mockito.when(addressService.save(addressDto, 1L)).thenReturn(null);


        mockMvc.perform(post("/api/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(address))
                        .header("X-User-Id", 1L))
                .andExpect(status().isCreated());

        Mockito.verify(addressService).save(any(), any());
    }

    @Test
    @DisplayName("주소 저장 별칭 없이")
    void saveAddressWithoutNickName() throws Exception {
        RequestAddress address = new RequestAddress();
        address.setStreetAddress("test");
        AddressDto addressDto = objectMapper.convertValue(address, AddressDto.class);

        Mockito.when(addressService.save(addressDto, 1L)).thenReturn(null);


        mockMvc.perform(post("/api/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(address))
                        .header("X-User-Id", 1L))
                .andExpect(status().isCreated());

        addressDto.setNickName("test");
        Mockito.verify(addressService).save(any(), any());
    }

    @Test
    @DisplayName("주소 조회")
    void getAddress() throws Exception {
        Mockito.when(addressService.getAddresses(1L)).thenReturn(List.of(new AddressDto()));


        mockMvc.perform(get("/api/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk());

        Mockito.verify(addressService).getAddresses(1L);
    }

    @Test
    @DisplayName("주소 삭제")
    void deleteAddress() throws Exception {
        Mockito.when(addressService.getAddresses(1L)).thenReturn(List.of(new AddressDto()));


        mockMvc.perform(delete("/api/address/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(addressService).deleteAddress(1L,1L);
    }


}