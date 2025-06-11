package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.entity.Address;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.exception.UserAlreadyException;
import com.sajotuna.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;


    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException(email));
        return objectMapper.convertValue(user, UserDto.class);
    }

    public UserDto createUser(UserDto userDto) {
        User user = new User(userDto, passwordEncoder);
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyException(userDto.getEmail());
        }
        User saveduser = userRepository.save(user);
        return objectMapper.convertValue(saveduser, UserDto.class);
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException(username));
    }
}
