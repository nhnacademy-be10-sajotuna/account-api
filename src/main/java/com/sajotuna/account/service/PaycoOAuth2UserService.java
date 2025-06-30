package com.sajotuna.account.service;

import com.sajotuna.account.domain.dooray.DoorayMessage;
import com.sajotuna.account.domain.entity.User;
import com.sajotuna.account.feign.InActiveUserFeignClient;
import com.sajotuna.account.feign.PaycoFeignClient;
import com.sajotuna.account.domain.payco.PaycoUserInfoResponse;
import com.sajotuna.account.repository.UserRepository;
import com.sajotuna.account.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PaycoOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PaycoFeignClient paycoFeignClient;
    private final InActiveUserFeignClient inActiveUserFeignClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String userInfoEndpointUri = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();

        String clientId = userRequest.getClientRegistration().getClientId();
        String accessToken = userRequest.getAccessToken().getTokenValue();

        PaycoUserInfoResponse userInfoResponse = paycoFeignClient.getUserInfo(clientId, accessToken);
        PaycoUserInfoResponse.Member m = userInfoResponse.getData().getMember();
        String idNo = m.getIdNo();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("idNo", idNo);

        String email = m.getEmail();
        String name = m.getName();
        User user = userRepository.findByPaycoId(idNo).orElse(null);
        if (user == null) {
            if (email != null && name != null) {
                User savedUser = userRepository.findByEmailAndName(email, name).orElse(null);
                if (savedUser != null) {
                    if (savedUser.getStatus().equals(User.Status.DELETED)) {
                        throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info_response", "사용자 정보 조회 실패", null));
                    }
                    savedUser.setPaycoId(idNo);
                    savedUser.setAuthType(User.AuthType.PAYCO);
                }
                else {
                    User newUser = new User();
                    newUser.setPaycoId(idNo);
                    newUser.setAuthType(User.AuthType.PAYCO);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    userRepository.save(newUser);
                }
            }
            else {
                User newUser = new User();
                newUser.setPaycoId(idNo);
                String newName = UUID.randomUUID().toString();
                newUser.setName(newName);
                email = newName +"@sajotuna.com";
                name = newName;
                userRepository.save(newUser);
            }
        }
        else {
            if (user.getStatus().equals(User.Status.DELETED)) {
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info_response", "사용자 정보 조회 실패", null));
            }
            email = user.getEmail();
            name = user.getName();
        }
        attributes.put("email", email);
        attributes.put("name", name);

        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new DefaultOAuth2User(authorities, attributes, "idNo");
    }
}
