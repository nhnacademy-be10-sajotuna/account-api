package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.request.RequestAddress;
import com.sajotuna.account.service.TokenService;
import com.sajotuna.account.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(TokenController.class)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("토큰 유효")
    void validateToken() throws Exception {


        Mockito.when(tokenService.validate("1234")).thenReturn(true);
        Mockito.when(tokenService.getIdFromToken("1234")).thenReturn("1");
        mockMvc.perform(get("/api/token/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer 1234"))
                .andExpect(status().isOk());

        Mockito.verify(tokenService).validate("1234");
        Mockito.verify(tokenService).getIdFromToken("1234");
    }

    @Test
    @DisplayName("토큰 유효 X")
    void invalidateToken() throws Exception {
        Mockito.when(tokenService.validate("1234")).thenReturn(false);
        mockMvc.perform(get("/api/token/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer 1234"))
                .andExpect(status().isOk());

        Mockito.verify(tokenService).validate("1234");
    }

    @Test
    @DisplayName("리프레쉬 토큰 유효")
    void refresh() throws Exception {
        Mockito.when(tokenService.validateRefreshToken("1234")).thenReturn(true);
        Mockito.when(tokenService.getAccessTokenFromRefreshToken("1234")).thenReturn("1234");
        Mockito.when(tokenService.getIdFromToken("1234")).thenReturn("1");
        mockMvc.perform(post("/api/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AuthorizationRefresh", "Bearer 1234"))
                .andExpect(status().isCreated());

        Mockito.verify(tokenService).validateRefreshToken("1234");
        Mockito.verify(tokenService).getAccessTokenFromRefreshToken("1234");
        Mockito.verify(tokenService).getIdFromToken("1234");
    }

    @Test
    @DisplayName("리프레쉬 토큰 무효")
    void invalidateRefresh() throws Exception {
        Mockito.when(tokenService.validateRefreshToken("1234")).thenReturn(false);
        mockMvc.perform(post("/api/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AuthorizationRefresh", "Bearer 1234"))
                .andExpect(status().isOk());

        Mockito.verify(tokenService).validateRefreshToken("1234");
    }
}