package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dooray.DoorayMessage;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.dto.UserGradePolicyDto;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.domain.request.PointEarnRequest;
import com.sajotuna.account.domain.request.WelcomeCouponRequest;
import com.sajotuna.account.domain.response.LoginResponse;
import com.sajotuna.account.domain.response.ResponseUserGradePolicy;
import com.sajotuna.account.exception.UserAlreadyException;
import com.sajotuna.account.exception.UserNotFoundException;
import com.sajotuna.account.feign.InActiveUserFeignClient;
import com.sajotuna.account.feign.OrderFeignClient;
import com.sajotuna.account.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService{

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final InActiveUserFeignClient inActiveUserFeignClient;
    private final AddressService addressService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PointMessageProducer pointMessageProducer;
    private final OrderFeignClient orderFeignClient;
    private final TokenService tokenService;

    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmailAndStatusNot(email, User.Status.DELETED).orElseThrow(()-> new UserNotFoundException(email));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserNotFoundException(email);
        }
        if (user.getStatus() == User.Status.INACTIVE) {
            AwakeInactiveUser(email, user);
        }

        user.setCurrentLoginAt(LocalDateTime.now());

        return makeTokenAfterLogin(user);
    }

    public LoginResponse oauth2Login(String outId, String email, String name) {
        User user = userRepository.findByOutId(outId).orElse(null);
        if (user == null) {
            if (email != null && name != null) {
                User savedUser = userRepository.findByEmailAndName(email, name).orElse(null);
                if (savedUser != null) {
                    if (savedUser.getStatus().equals(User.Status.DELETED)) {
                        throw new UserNotFoundException(email);
                    }
                    savedUser.setOutId(outId);
                    savedUser.setAuthType(User.AuthType.PAYCO);
                }
                else {
                    User newUser = User.ofPayco(outId, name, email, User.AuthType.PAYCO);
                    userRepository.save(newUser);
                    user = newUser;
                    pointMessageProducer.sendPointEarnRequest(new PointEarnRequest(user.getId(), PointEarnRequest.PointPolicyType.REGISTER));
                }
            }
            else {
                String newName = UUID.randomUUID().toString();
                email = newName +"@sajotuna.com";
                User newUser = User.ofPayco(outId, newName, email, User.AuthType.PAYCO);
                user = userRepository.save(newUser);
                pointMessageProducer.sendPointEarnRequest(new PointEarnRequest(user.getId(), PointEarnRequest.PointPolicyType.REGISTER));
            }
        }
        else {
            if (user.getStatus().equals(User.Status.DELETED)) {
                throw new UserNotFoundException(email);
            }
        }

        user.setCurrentLoginAt(LocalDateTime.now());
        if (user.getStatus() == User.Status.INACTIVE) {
            AwakeInactiveUser(email, user);
        }

        return makeTokenAfterLogin(user);
    }

    private void AwakeInactiveUser(String email, User user) {
        DoorayMessage doorayMessage = new DoorayMessage("inactive", email);
        inActiveUserFeignClient.sendMessage("application/json",doorayMessage);
        user.setStatus(User.Status.ACTIVE);
    }

    private LoginResponse makeTokenAfterLogin(User user) {
        Claims claims = Jwts.claims();

        String accessToken = tokenService.getAccessToken(claims, user);
        String refreshToken = tokenService.getRefreshToken(claims, user);

        tokenService.saveRefreshToken(user.getId(), refreshToken);
        return new LoginResponse(accessToken, refreshToken, user.getEmail(), user.getName());
    }

    public UserDto createUser(UserDto userDto, String address) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyException(userDto.getEmail());
        }
        User user = userDto.toUser(passwordEncoder);

        User saveduser = userRepository.save(user);
        pointMessageProducer.sendPointEarnRequest(new PointEarnRequest(user.getId(), PointEarnRequest.PointPolicyType.REGISTER));
        orderFeignClient.issueWelcomeCoupon(new WelcomeCouponRequest(user.getId()));

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
        user.update(userDto.getName(), userDto.getPhoneNumber(), userDto.getBirthDate());
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.updatePassword(passwordEncoder.encode(userDto.getPassword()));
        }
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        UserDto userDto = objectMapper.convertValue(user, UserDto.class);
        return userDto;
    }

    public UserDto getUserDetailById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        UserDto userDto = objectMapper.convertValue(user, UserDto.class);
        ResponseUserGradePolicy responseUserGradePolicy = orderFeignClient.getUserGradePolicy(id);
        userDto.setUserGradePolicyDto(objectMapper.convertValue(responseUserGradePolicy, UserGradePolicyDto.class));
        return userDto;
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        user.setStatus(User.Status.DELETED);

        redisTemplate.delete("refresh_token:"+ user.getId());
    }

    public void logout(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id.toString()));
        redisTemplate.delete("refresh_token:"+ user.getId());
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
}
