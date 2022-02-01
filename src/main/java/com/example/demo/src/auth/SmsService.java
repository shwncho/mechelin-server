package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.src.auth.model.Message;
import com.example.demo.src.auth.model.SmsRequest;
import com.example.demo.src.auth.model.SmsResponse;
import com.example.demo.src.auth.model.VerifyRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.config.secret.Secret;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional
public class SmsService {

    private SmsDao smsDao;

    @Autowired
    public SmsService(SmsDao smsDao) {
        this.smsDao = smsDao;
    }

    public SmsResponse sendSms(String recipientPhoneNumber, String content, String certificationNumber) throws JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {

        Long time = System.currentTimeMillis();
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(recipientPhoneNumber, content));

        SmsRequest smsRequest = new SmsRequest("SMS", "COMM", "82", "01025912343","test", messages);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(smsRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", Secret.accessKey);
        String sig = makeSignature(time); //암호화
        headers.set("x-ncp-apigw-signature-v2", sig);

        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        SmsResponse smsResponse = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + Secret.serviceId + "/messages"), body, SmsResponse.class);
        // 휴대폰번호 + 인증번호 저장
        smsDao.createSmsCertification(recipientPhoneNumber, certificationNumber);
        return smsResponse;

    }
    public String makeSignature(Long time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+Secret.serviceId+"/messages";
        String timestamp = time.toString();
        String accessKey = Secret.accessKey;
        String secretKey = Secret.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    public String verify(VerifyRequest verifyRequest) throws BaseException {
        String phoneNumber = verifyRequest.getPhoneNumber();
        String certificationNumber = verifyRequest.getCertificationNumber();

        if (smsDao.hasKey(phoneNumber)) {
            String storedCertificationNumber = smsDao.getSmsCertification(verifyRequest.getPhoneNumber());
            if (certificationNumber.equals(storedCertificationNumber)) {
                smsDao.removeSmsCertification(phoneNumber);
                return "성공";
            } else {
                System.out.println(storedCertificationNumber);
                throw new BaseException(POST_AUTH_INVALID_CERTIFICATION_NUMBER);
            }
        } else {
            throw new BaseException(POST_AUTH_VERIFICATION_EXPIRE_CERTIFICATION_NUMBER);
        }
    }
}