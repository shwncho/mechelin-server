package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        if(checkEmail(postLoginReq.getEmail())==0){
            throw new BaseException(POST_USERS_EMPTY_EMAIL);
        }

        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if (postLoginReq.getPassword().equals(password)) {
            if(checkStatus(postLoginReq.getEmail())== "D") {
                throw new BaseException(POST_USERS_INACTIVE_ACCOUNT);
            }

            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);

        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }
    @Transactional(readOnly = true)
    public int checkEmail(String email) throws BaseException {
        try {
            return userDao.checkEmail(email);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(readOnly = true)
    public int checkNickName(String nickname) throws BaseException{
        try{
            return userDao.checkNickName(nickname);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(readOnly = true)
    public int checkPhoneNumber(String phonenumber) throws BaseException{
        try{
            return userDao.checkPhoneNumber(phonenumber);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(readOnly = true)
    public String checkStatus(String email) throws BaseException{
        try{
            return userDao.checkStatus(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public GetProfileRes getProfile(int userIdx) throws BaseException{
        try {
            GetProfileRes getProfileRes = userDao.getProfile(userIdx);
            return getProfileRes;
        } catch (Exception exception){
        throw new BaseException(DATABASE_ERROR);
        }
    }



}
