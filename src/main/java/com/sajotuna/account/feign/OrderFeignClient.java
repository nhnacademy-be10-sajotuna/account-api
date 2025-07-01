package com.sajotuna.account.feign;

import com.sajotuna.account.domain.response.ResponseUserGradePolicy;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-api")
public interface OrderFeignClient {
    @GetMapping("/api/grade/{userId}")
    public ResponseUserGradePolicy getUserGradePolicy(@PathVariable Long userId);
}
