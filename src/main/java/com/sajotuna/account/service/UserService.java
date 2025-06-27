package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dooray.DoorayMessage;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.dto.UserGradePolicyDto;
import com.sajotuna.account.domain.entity.Address;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.domain.entity.UserGradePolicy;
import com.sajotuna.account.exception.UserAlreadyException;
import com.sajotuna.account.exception.UserNotFoundException;
import com.sajotuna.account.feign.InActiveUserFeignClient;
import com.sajotuna.account.repository.UserGradePolicyRepository;
import com.sajotuna.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final InActiveUserFeignClient inActiveUserFeignClient;
    private final AddressService addressService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserGradePolicyRepository userGradePolicyRepository;


    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmailAndStatusNot(email, User.Status.DELETED).orElseThrow(()-> new UserNotFoundException(email));
        if (user.getStatus() == User.Status.INACTIVE) {
            DoorayMessage doorayMessage = new DoorayMessage("inactive", email);
            inActiveUserFeignClient.sendMessage("application/json",doorayMessage);
            user.setStatus(User.Status.ACTIVE);
        }
        return objectMapper.convertValue(user, UserDto.class);
    }

    public void updateLastLogin(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException(email));
        user.setCurrentLoginAt(LocalDateTime.now());
    }

    public UserDto createUser(UserDto userDto, String address) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyException(userDto.getEmail());
        }
        User user = new User(userDto, passwordEncoder);
        UserGradePolicy defaultUserGradePolicy = userGradePolicyRepository.findById(1L).orElse(null);
        user.setUserGradePolicy(defaultUserGradePolicy);

        User saveduser = userRepository.save(user);
        if (address != null && !address.isBlank()) {
            AddressDto addressDto = new AddressDto();
            addressDto.setStreetAddress(address);
            addressDto.setNickName(address);
            addressService.save(addressDto, saveduser.getId());
        }

        return objectMapper.convertValue(saveduser, UserDto.class);
    }

    public void updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getPhoneNumber() != null && !userDto.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getBirthDate() != null) {
            user.setBirthDate(userDto.getBirthDate());
        }
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        UserDto userDto = objectMapper.convertValue(user, UserDto.class);
        userDto.setUserGradePolicyDto(new UserGradePolicyDto(user.getUserGradePolicy()));
        return userDto;
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        user.setStatus(User.Status.DELETED);

        redisTemplate.delete("refresh_token:"+ user.getEmail());
    }

    public void logout(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        redisTemplate.delete("refresh_token:"+ user.getEmail());
    }

    public List<UserDto> getUserByBirth() {
        List<User> users = userRepository.findUsersWithBirthdayInThisMonth();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(objectMapper.convertValue(user, UserDto.class));
        }
        return userDtos;
    }

    public void sleepUser() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<User> users = userRepository.findByStatusAndCurrentLoginAtLessThan(User.Status.ACTIVE, threeMonthsAgo).get();
        for (User user : users) {
            user.setStatus(User.Status.INACTIVE);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailAndStatusNot(username, User.Status.DELETED).orElseThrow(()-> new UserNotFoundException(username));
    }
}
