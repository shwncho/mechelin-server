package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.AuthRequest;
import com.example.demo.src.auth.model.SmsResponse;
import com.example.demo.src.auth.model.VerifyRequest;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
public class SmsController {

    private final SmsService smsService;
    private final JwtService jwtService;

    @Autowired
    public SmsController(SmsService smsService, JwtService jwtService) {
        this.smsService = smsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/{userIdx}")
    public ResponseEntity<?> auth(@RequestBody AuthRequest authRequest, @PathVariable int userIdx) {
        try {
            if (userIdx != jwtService.getUserIdx()) {
                return ResponseEntity.ok().body(new BaseResponse<>(INVALID_USER_JWT));
            }
            String certificationNumber = generateCertNumber();
            String contents = "[Mechelin] 인증번호:" + certificationNumber + "\n인증번호를 입력해 주세요.";
            SmsResponse data = smsService.sendSms(authRequest.getRecipientPhoneNumber(), contents, certificationNumber);
            return ResponseEntity.ok().body(data);
        } catch (BaseException baseException) {
            return ResponseEntity.badRequest().body(new BaseResponse<>(baseException.getStatus()));
        } catch (Exception exception) {
            System.out.println(exception);
            return ResponseEntity.badRequest().build();
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
        return String.valueOf(result);
    }

    @PostMapping("auth/verification/{userIdx}")
    public BaseResponse<?> verify(@RequestBody VerifyRequest verifyRequest, @PathVariable int userIdx) {
        try {
            if (userIdx != jwtService.getUserIdx()) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            return new BaseResponse<>(smsService.verify(verifyRequest));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
