package com.sajotuna.account.feign;

import com.sajotuna.account.domain.dooray.DoorayMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "inActiveUserFeignClient", url = "https://nhnacademy.dooray.com")
public interface InActiveUserFeignClient {
    @PostMapping(
            value = "/services/3204376758577275363/4071284119244117501/RibHlHtpSlCOQ1Kesn_0KQ",
            consumes = "application/json",
            produces = "application/json"
    )
    String sendMessage(@RequestHeader("Content-Type") String contentType, @RequestBody DoorayMessage message);
}