package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dooray.DoorayMessage;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.entity.Address;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.exception.UserAlreadyException;
import com.sajotuna.account.exception.UserNotFoundException;
import com.sajotuna.account.feign.InActiveUserFeignClient;
import com.sajotuna.account.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final InActiveUserFeignClient inActiveUserFeignClient;


    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmailAndStatusNot(email, User.Status.DELETED).orElseThrow(()-> new UserNotFoundException(email));
        if (user.getStatus() == User.Status.INACTIVE) {
            DoorayMessage doorayMessage = new DoorayMessage(
                    "team04.shop 봇.",
                    String.format("휴면 유저 %s",
                    user.getEmail()),
                    new DoorayMessage.Attachment[]{new DoorayMessage.Attachment(
                            "인증되었습니다. ",
                            "깨어났습니다 용사님",
                            "http://naver.com",
                    "https://static.dooray.com/static_images/dooray-bot.png",
                            "red")});
            inActiveUserFeignClient.sendMessage("application/json",doorayMessage);
            user.setStatus(User.Status.ACTIVE);
        }
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
