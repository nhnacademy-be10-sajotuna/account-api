package com.sajotuna.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.request.LoginRequestUser;
import com.sajotuna.account.domain.request.RequestEditUser;
import com.sajotuna.account.domain.request.RequestOauth2;
import com.sajotuna.account.domain.request.RequestUser;
import com.sajotuna.account.exception.UserAlreadyException;
import com.sajotuna.account.exception.UserNotFoundException;
import com.sajotuna.account.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        LoginRequestUser loginRequestUser = new LoginRequestUser();
        loginRequestUser.setEmail("test@test.com");
        loginRequestUser.setPassword("password");

        Mockito.when(userService.login("test@test.com", "password")).thenReturn(null);


        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestUser)))
                .andExpect(status().isOk());

        Mockito.verify(userService).login("test@test.com", "password");
    }

    @Test
    @DisplayName("유저를 찾지 못함")
    void loginError1() throws Exception {
        LoginRequestUser loginRequestUser = new LoginRequestUser();
        loginRequestUser.setEmail("test@test.com");
        loginRequestUser.setPassword("password");

        Mockito.when(userService.login("test@test.com", "password")).thenThrow(new UserNotFoundException("test@test.com"));


        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("LoginRequestUser 의 이메일이 이메일같지 않음")
    void loginError2() throws Exception {
        LoginRequestUser loginRequestUser = new LoginRequestUser();
        loginRequestUser.setEmail("test");
        loginRequestUser.setPassword("password");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("LoginRequestUser 의 비밀번호가 null")
    void loginError3() throws Exception {
        LoginRequestUser loginRequestUser = new LoginRequestUser();
        loginRequestUser.setEmail("test@test.com");
        loginRequestUser.setPassword(null);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("oauth2 로그인")
    void oauth2() throws Exception {
        RequestOauth2 requestOauth2 = new RequestOauth2();

        Mockito.when(userService.oauth2Login("123", null, null)).thenReturn(null);


        mockMvc.perform(post("/api/users/oauth2/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestOauth2)))
                .andExpect(status().isOk());

        Mockito.verify(userService).oauth2Login("123", null, null);
    }

    @Test
    @DisplayName("유저 생성")
    void createUser() throws Exception {
        RequestUser requestUser = new RequestUser();
        requestUser.setEmail("test@test.com");
        requestUser.setPassword("password");
        requestUser.setName("test");
        requestUser.setAddress("address");
        requestUser.setBirthDate(LocalDate.now());
        requestUser.setPhoneNumber("010-1111-2222");

        UserDto userDto = UserDto.fromRequestUserLocal(requestUser);

        Mockito.when(userService.createUser(userDto, requestUser.getAddress())).thenReturn(userDto);


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("유저 이미 있음")
    void createUserError() throws Exception {
        RequestUser requestUser = new RequestUser();
        requestUser.setEmail("test@test.com");
        requestUser.setPassword("password");
        requestUser.setName("test");
        requestUser.setAddress("address");
        requestUser.setBirthDate(LocalDate.now());
        requestUser.setPhoneNumber("010-1111-2222");

        UserDto userDto = UserDto.fromRequestUserLocal(requestUser);

        Mockito.when(userService.createUser(any(UserDto.class), anyString())).thenThrow(new UserAlreadyException(userDto.getEmail()));


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("유저 조회")
    void getUser() throws Exception {

        Mockito.when(userService.getUserById(1L)).thenReturn(UserDto.fromRequestUserLocal(new RequestUser()));


        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 자세히 조회")
    void getDetailUser() throws Exception {

        Mockito.when(userService.getUserDetailById(1L)).thenReturn(UserDto.fromRequestUserLocal(new RequestUser()));


        mockMvc.perform(get("/api/users/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 삭제")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("유저 수정")
    void editUser() throws Exception {
        RequestEditUser editUser = new RequestEditUser();
        editUser.setPassword("password");
        editUser.setName("test");
        editUser.setBirthDate(LocalDate.now());
        editUser.setPhoneNumber("010-1111-2222");

        UserDto userDto = objectMapper.convertValue(editUser, UserDto.class);

        mockMvc.perform(put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1)
                        .content(objectMapper.writeValueAsString(editUser)))
                .andExpect(status().isNoContent());
        Mockito.verify(userService).updateUser(1L, userDto);
    }

    @Test
    @DisplayName("로그 아웃")
    void logout() throws Exception {
        mockMvc.perform(post("/api/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1))
                .andExpect(status().isNoContent());
        Mockito.verify(userService).logout(1L);
    }

    @Test
    @DisplayName("생일 유저")
    void getAllBirthUsers() throws Exception {

        Mockito.when(userService.getUserByBirth()).thenReturn(List.of(UserDto.fromRequestUserLocal(new RequestUser())));

        mockMvc.perform(get("/api/users/birth")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userService).getUserByBirth();
    }

}