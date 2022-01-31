package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }
    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        if (userProvider.checkNickName(postUserReq.getNickName()) == 1){
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);

        }
        if (userProvider.checkPhoneNumber(postUserReq.getPhoneNumber()) == 1){
            throw new BaseException(POST_USERS_EXISTS_PHONENUMBER);
        }

        String pwd;
        try {
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            int userIdx = userDao.createUser(postUserReq);

            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(userIdx,jwt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원탈퇴
    @Transactional
    public PatchUserStatusRes deleteAccount(int userIdx, String password) throws BaseException{
        AES128 aes128 = new AES128(Secret.USER_INFO_PASSWORD_KEY);
        String storedPassword = userDao.getPassword(userIdx);
        String decryptedPassword;
        try {
            decryptedPassword = aes128.decrypt(storedPassword);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        try {
            if (password.equals(decryptedPassword)) {
                return new PatchUserStatusRes(userDao.deleteAccount(userIdx));
            } else {
                throw new BaseException(PATCH_USERS_STATUS_INVALID_PASSWORD);
            }
        } catch (BaseException baseException) {
            throw new BaseException(baseException.getStatus());
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
