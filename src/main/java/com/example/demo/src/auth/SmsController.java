package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.PostAuthRequest;
import com.example.demo.src.auth.model.SmsResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {

    private final SmsService smsService;
    private final JwtService jwtService;

    @Autowired
    public SmsController(SmsService smsService, JwtService jwtService) {
        this.smsService = smsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/phonenumber")
    public BaseResponse<?> PostAuth(@RequestBody PostAuthRequest postAuthRequest) {
        try {
            String phoneNumber = postAuthRequest.getRecipientPhoneNumber();
            String certificationNumber = generateCertNumber();
            String contents = "[Mechelin] 인증번호:" + certificationNumber + "\n인증번호를 입력해 주세요.";
            SmsResponse data = smsService.sendSms(phoneNumber, contents, certificationNumber);
            return new BaseResponse<>(data);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 인증번호 생성
    public String generateCertNumber() {
        Random random = new Random(System.currentTimeMillis());

        int range = (int)Math.pow(10, 6);
        int trim  = (int)Math.pow(10, 5);
        int result = random.nextInt(range)+trim;

        if (result > range) {
            result = result - trim;
        }

        return Integer.toString(result);
    }

    @GetMapping("/auth")
    public BaseResponse<?> GetAuth(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("certNumber") String certNumber) {
        try {
            return new BaseResponse<>(smsService.getAuth(phoneNumber, certNumber));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
