package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.entity.Address;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.exception.UserAlreadyException;
import com.sajotuna.account.exception.UserNotFoundException;
import com.sajotuna.account.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;


    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmailAndStatusNot(email, User.Status.DELETED).orElseThrow(()-> new UserNotFoundException(email));
        return objectMapper.convertValue(user, UserDto.class);
    }

    public void updateLastLogin(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException(email));
        user.setCurrentLoginAt(LocalDateTime.now());
    }

    public UserDto createUser(UserDto userDto) {
        User user = new User(userDto, passwordEncoder);
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyException(userDto.getEmail());
        }
        User saveduser = userRepository.save(user);
        return objectMapper.convertValue(saveduser, UserDto.class);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        return objectMapper.convertValue(user, UserDto.class);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        user.setStatus(User.Status.DELETED);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailAndStatusNot(username, User.Status.DELETED).orElseThrow(()-> new UserNotFoundException(username));
    }
}
