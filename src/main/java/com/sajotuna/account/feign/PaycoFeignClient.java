package com.sajotuna.account.feign;


import com.sajotuna.account.domain.payco.PaycoUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "paycoFeignClient", url = "https://apis-payco.krp.toastoven.net/payco/friends/find_member_v2.json")
public interface PaycoFeignClient {
    @PostMapping
    public PaycoUserInfoResponse getUserInfo(@RequestHeader("client_id") String clientId, @RequestHeader("access_token") String accessToken);

}
