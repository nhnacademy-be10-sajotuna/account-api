package com.sajotuna.account.feign;

import com.sajotuna.account.domain.request.WelcomeCouponRequest;
import com.sajotuna.account.domain.response.ResponseUserGradePolicy;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-api")
public interface OrderFeignClient {
    @GetMapping("/api/grade/{userId}")
    public ResponseUserGradePolicy getUserGradePolicy(@PathVariable Long userId);

    @PostMapping("/api/coupons/users/issue-welcome")
    void issueWelcomeCoupon(@RequestBody @Valid WelcomeCouponRequest welcomeCouponRequest);
}
