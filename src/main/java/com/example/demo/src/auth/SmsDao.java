package com.example.demo.src.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class SmsDao {

    private final String PREFIX = "sms:";
    // 인증 제한시간
    private final int LIMIT_TIME = 3 * 60;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public SmsDao(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    // 캐시에 전화번호, 인증번호 저장
    public void createSmsCertification(String phoneNumber, String certNumber) {
        stringRedisTemplate.opsForValue()
                .set(PREFIX + phoneNumber, certNumber, Duration.ofSeconds(LIMIT_TIME));
    }
    // 인증번호 불러오기
    public String getSmsCertification(String phoneNumber) {
        return stringRedisTemplate.opsForValue().get(PREFIX + phoneNumber);
    }
    // 인증번호 제거
    public void removeSmsCertification(String phoneNumber) {
        stringRedisTemplate.delete(PREFIX + phoneNumber);
    }
    // 인증번호 존재하는지 확인
    public boolean hasKey(String phone) {
        return stringRedisTemplate.hasKey(PREFIX + phone);
    }

}
