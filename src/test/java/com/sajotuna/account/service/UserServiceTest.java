package com.sajotuna.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sajotuna.account.domain.dto.AddressDto;
import com.sajotuna.account.domain.dto.UserDto;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.domain.request.PointEarnRequest;
import com.sajotuna.account.exception.UserAlreadyException;
import com.sajotuna.account.exception.UserNotFoundException;
import com.sajotuna.account.feign.InActiveUserFeignClient;
import com.sajotuna.account.feign.OrderFeignClient;
import com.sajotuna.account.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private InActiveUserFeignClient inActiveUserFeignClient;
    @Mock
    private AddressService addressService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PointMessageProducer pointMessageProducer;
    @Mock
    private OrderFeignClient orderFeignClient;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("로그인 성공")
    void login() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);
        when(userRepository.findByEmailAndStatusNot("test", User.Status.DELETED)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test", "test")).thenReturn(true);
        when(tokenService.getAccessToken(any(Claims.class), any(User.class))).thenReturn("test");
        when(tokenService.getRefreshToken(any(Claims.class), any(User.class))).thenReturn("test");

        userService.login("test", "test");

        verify(userRepository, Mockito.times(1)).findByEmailAndStatusNot("test", User.Status.DELETED);
        verify(tokenService, Mockito.times(1)).getAccessToken(any(Claims.class), any(User.class));
        verify(tokenService, Mockito.times(1)).getRefreshToken(any(Claims.class), any(User.class));
        verify(inActiveUserFeignClient, Mockito.times(0)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("로그인 성공 - 휴면 계정")
    void login2() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.INACTIVE);
        when(userRepository.findByEmailAndStatusNot("test", User.Status.DELETED)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test", "test")).thenReturn(true);
        when(tokenService.getAccessToken(any(Claims.class), any(User.class))).thenReturn("test");
        when(tokenService.getRefreshToken(any(Claims.class), any(User.class))).thenReturn("test");

        userService.login("test", "test");

        verify(userRepository, Mockito.times(1)).findByEmailAndStatusNot("test", User.Status.DELETED);
        verify(tokenService, Mockito.times(1)).getAccessToken(any(Claims.class), any(User.class));
        verify(tokenService, Mockito.times(1)).getRefreshToken(any(Claims.class), any(User.class));
        verify(inActiveUserFeignClient, Mockito.times(1)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("로그인 실패 - 없는 사용자")
    void loginError() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.INACTIVE);
        when(userRepository.findByEmailAndStatusNot("test", User.Status.DELETED)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test", "test")).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class,()-> {
            userService.login("test", "test");
        });
    }

    @Test
    @DisplayName("외부 로그인 성공")
    void oauth2Login() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);
        when(userRepository.findByOutId("123")).thenReturn(Optional.of(user));
        when(tokenService.getAccessToken(any(Claims.class), any(User.class))).thenReturn("test");
        when(tokenService.getRefreshToken(any(Claims.class), any(User.class))).thenReturn("test");

        userService.oauth2Login("123", "test", "test");

        verify(userRepository, Mockito.times(1)).findByOutId("123");
        verify(tokenService, Mockito.times(1)).getAccessToken(any(Claims.class), any(User.class));
        verify(tokenService, Mockito.times(1)).getRefreshToken(any(Claims.class), any(User.class));
        verify(inActiveUserFeignClient, Mockito.times(0)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("외부 로그인 성공 - 휴면 계정")
    void oauth2Login2() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.INACTIVE);
        when(userRepository.findByOutId("123")).thenReturn(Optional.of(user));
        when(tokenService.getAccessToken(any(Claims.class), any(User.class))).thenReturn("test");
        when(tokenService.getRefreshToken(any(Claims.class), any(User.class))).thenReturn("test");

        userService.oauth2Login("123", "test", "test");

        verify(userRepository, Mockito.times(1)).findByOutId("123");
        verify(tokenService, Mockito.times(1)).getAccessToken(any(Claims.class), any(User.class));
        verify(tokenService, Mockito.times(1)).getRefreshToken(any(Claims.class), any(User.class));
        verify(inActiveUserFeignClient, Mockito.times(1)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("외부 로그인 성공 - 삭제된 계정")
    void oauth2Login3() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.DELETED);
        when(userRepository.findByOutId("123")).thenReturn(Optional.of(user));

        Assertions.assertThrows(UserNotFoundException.class,()-> {
            userService.oauth2Login("123", "test", "test");
        });
    }

    @Test
    @DisplayName("외부 로그인 실패 - 동기화시 삭제된 계정")
    void oauth2Login4() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.DELETED);
        when(userRepository.findByOutId("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmailAndName("test", "test")).thenReturn(Optional.of(user));

        Assertions.assertThrows(UserNotFoundException.class,()-> {
            userService.oauth2Login("123", "test", "test");
        });
    }

    @Test
    @DisplayName("외부 로그인 실패 - 동기화")
    void oauth2Login5() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);
        when(userRepository.findByOutId("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmailAndName("test", "test")).thenReturn(Optional.of(user));

        userService.oauth2Login("123", "test", "test");

        verify(userRepository, Mockito.times(1)).findByOutId("123");
        verify(userRepository, Mockito.times(1)).findByEmailAndName("test", "test");
        verify(tokenService, Mockito.times(1)).getAccessToken(any(Claims.class), any(User.class));
        verify(tokenService, Mockito.times(1)).getRefreshToken(any(Claims.class), any(User.class));
        verify(inActiveUserFeignClient, Mockito.times(0)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("외부 로그인 실패 - 계정 생성")
    void oauth2Login6() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);
        when(userRepository.findByOutId("123")).thenReturn(Optional.empty());
        when(userRepository.findByEmailAndName("test", "test")).thenReturn(Optional.empty());

        userService.oauth2Login("123", "test", "test");

        verify(userRepository, Mockito.times(1)).findByOutId("123");
        verify(userRepository, Mockito.times(1)).findByEmailAndName("test", "test");
        verify(userRepository, Mockito.times(1)).save(any(User.class));
        verify(pointMessageProducer, Mockito.times(1)).sendPointEarnRequest(any());
        verify(orderFeignClient, Mockito.times(1)).issueWelcomeCoupon(any());
        verify(tokenService, Mockito.times(1)).getAccessToken(any(Claims.class), any(User.class));
        verify(tokenService, Mockito.times(1)).getRefreshToken(any(Claims.class), any(User.class));
        verify(inActiveUserFeignClient, Mockito.times(0)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("외부 로그인 실패 - 정보 X")
    void oauth2Login7() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);
        when(userRepository.findByOutId("123")).thenReturn(Optional.empty());

        when(userRepository.save(any())).thenReturn(user);

        userService.oauth2Login("123", "test", null);

        verify(userRepository, Mockito.times(1)).findByOutId("123");
        verify(userRepository, Mockito.times(1)).save(any(User.class));
        verify(pointMessageProducer, Mockito.times(1)).sendPointEarnRequest(any());
        verify(orderFeignClient, Mockito.times(1)).issueWelcomeCoupon(any());
        verify(tokenService, Mockito.times(1)).getAccessToken(any(Claims.class), any(User.class));
        verify(tokenService, Mockito.times(1)).getRefreshToken(any(Claims.class), any(User.class));
        verify(inActiveUserFeignClient, Mockito.times(0)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("회원 가입 - 주소 있음")
    void createUser() {
        UserDto userDto = Mockito.mock(UserDto.class);
        User user = Mockito.mock(User.class);
        AddressDto addressDto = new AddressDto();
        addressDto.setStreetAddress("123");
        addressDto.setNickName("123");

        when(userDto.getEmail()).thenReturn("test");
        when(userDto.toUser(passwordEncoder)).thenReturn(user);
        when(userRepository.findByEmail("test")).thenReturn(Optional.empty());
        when(user.getId()).thenReturn(1L);
        when(userRepository.save(user)).thenReturn(user);


        userService.createUser(userDto, "123");

        verify(userRepository, Mockito.times(1)).findByEmail("test");
        verify(addressService, Mockito.times(1)).save(addressDto, 1L);
    }

    @Test
    @DisplayName("회원 가입 - 주소 없음")
    void createUser2() {
        UserDto userDto = Mockito.mock(UserDto.class);
        User user = Mockito.mock(User.class);

        when(userDto.getEmail()).thenReturn("test");
        when(userDto.toUser(passwordEncoder)).thenReturn(user);
        when(userRepository.findByEmail("test")).thenReturn(Optional.empty());
        when(user.getId()).thenReturn(1L);
        when(userRepository.save(user)).thenReturn(user);


        userService.createUser(userDto, null);

        verify(userRepository, Mockito.times(1)).findByEmail("test");
        verify(addressService, Mockito.times(0)).save(any(), any());

    }

    @Test
    @DisplayName("회원 가입 실패")
    void createUserError() {
        UserDto userDto = Mockito.mock(UserDto.class);
        User user = Mockito.mock(User.class);

        when(userDto.getEmail()).thenReturn("test");
        when(userRepository.findByEmail("test")).thenReturn(Optional.of(user));

        Assertions.assertThrows(UserAlreadyException.class,()-> {
            userService.createUser(userDto, null);
        });


        verify(userRepository, Mockito.times(1)).findByEmail("test");
    }

    @Test
    @DisplayName("회원 정보 수정")
    void updateUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test");
        userDto.setName("test");
        userDto.setPhoneNumber("123");
        userDto.setBirthDate(LocalDate.now());
        userDto.setPassword("123");
        userDto.setRole(User.Role.USER);
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));


        userService.updateUser(1L, userDto);


        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(passwordEncoder, Mockito.times(1)).encode("123");
    }

    @Test
    @DisplayName("회원 정보 수정 - 비밀번호 x")
    void updateUser2() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test");
        userDto.setName("test");
        userDto.setPhoneNumber("123");
        userDto.setBirthDate(LocalDate.now());
        userDto.setRole(User.Role.USER);
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));


        userService.updateUser(1L, userDto);


        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(passwordEncoder, Mockito.times(0)).encode("123");
    }

    @Test
    @DisplayName("회원 정보 수정 - 실패")
    void updateUser3() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test");
        userDto.setName("test");
        userDto.setPhoneNumber("123");
        userDto.setBirthDate(LocalDate.now());
        userDto.setRole(User.Role.USER);
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,()-> {
            userService.updateUser(1L, userDto);
        });

        verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 조회")
    void getUserById() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.getUserById(1L);

        verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 조회 - 실패")
    void getUserById2() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,()-> {
            userService.getUserById(1L);
        });

        verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 상세 조회")
    void getUserDetailById() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);
        UserDto userDto = new UserDto();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(objectMapper.convertValue(user, UserDto.class)).thenReturn(userDto);

        userService.getUserDetailById(1L);


        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(orderFeignClient, Mockito.times(1)).getUserGradePolicy(1L);
    }

    @Test
    @DisplayName("회원 상세 조회 - 실패")
    void getUserDetailById2() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,()-> {
            userService.getUserDetailById(1L);
        });

        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(orderFeignClient, Mockito.times(0)).getUserGradePolicy(1L);
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteUser() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);


        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(redisTemplate, Mockito.times(1)).delete("refresh_token:0");
    }

    @Test
    @DisplayName("회원 삭제 - 실패")
    void deleteUser2() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,()-> {
            userService.deleteUser(1L);
        });

        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(redisTemplate, Mockito.times(0)).delete("refresh_token:0");
    }

    @Test
    @DisplayName("회원 삭제")
    void logout() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.logout(1L);


        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(redisTemplate, Mockito.times(1)).delete("refresh_token:0");
    }

    @Test
    @DisplayName("회원 삭제 - 실패")
    void logout2() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,()-> {
            userService.logout(1L);
        });

        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(redisTemplate, Mockito.times(0)).delete("refresh_token:0");
    }

    @Test
    @DisplayName("이번달 생일 유저")
    void getUserByBirth() {
        User user = new User("test","test","test","test", LocalDate.now(), User.Role.USER, User.AuthType.PAYCO, LocalDateTime.now(), User.Status.ACTIVE);
        when(userRepository.findUsersWithBirthdayInThisMonth()).thenReturn(List.of(user));

        userService.getUserByBirth();

        verify(userRepository, Mockito.times(1)).findUsersWithBirthdayInThisMonth();
    }

    @Test
    @DisplayName("3달간 접속 안한 유저 휴면으로")
    void sleepUser() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsAgo = now.minusMonths(3).minusDays(1);

        User user1 = new User();
        user1.setStatus(User.Status.ACTIVE);
        user1.setCurrentLoginAt(threeMonthsAgo);

        User user2 = new User();
        user2.setStatus(User.Status.ACTIVE);
        user2.setCurrentLoginAt(threeMonthsAgo.minusDays(1));

        List<User> inactiveUsers = List.of(user1, user2);

        when(userRepository.findByStatusAndCurrentLoginAtLessThan(eq(User.Status.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.of(inactiveUsers));

        // when
        userService.sleepUser();

        // then
        assertEquals(User.Status.INACTIVE, user1.getStatus());
        assertEquals(User.Status.INACTIVE, user2.getStatus());

        verify(userRepository).findByStatusAndCurrentLoginAtLessThan(eq(User.Status.ACTIVE), any(LocalDateTime.class));
    }


}