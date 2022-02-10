package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/users")

public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if (postUserReq.getPassword()==null){
            return new BaseResponse<>(NULL_ERROR);
        }
        if (!isRegexPassWord(postUserReq.getPassword())){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        if(postUserReq.getNickName()==null){
            return new BaseResponse<>(NULL_ERROR);
        }
        if (!isRegexNickName(postUserReq.getNickName())){
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }
        if(postUserReq.getPhoneNumber()==null){
            return new BaseResponse<>(NULL_ERROR);
        }
        if (!isRegexPhoneNumber(postUserReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }

        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/sign-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getEmail() == null) {
            return new BaseResponse<>(NULL_ERROR);
        }
        if (!isRegexEmail(postLoginReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        if(postLoginReq.getPassword().isEmpty() && postLoginReq.getPassword() ==null){
            return new BaseResponse<>(NULL_ERROR);
        }
        try {
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetProfileRes> getProfile(@PathVariable int userIdx){
        try{
            if(userIdx<=0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx!=userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetProfileRes getProfileRes = userProvider.getProfile(userIdx);
            return new BaseResponse<>(getProfileRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @PatchMapping("/{userIdx}/status")
    public BaseResponse<PatchUserStatusRes> deleteAccount(@RequestBody PatchUserStatusReq patchUserStatusReq, @PathVariable("userIdx") int userIdx) {
        try {
            if (userIdx != jwtService.getUserIdx()) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if (patchUserStatusReq.getPassword() == null || patchUserStatusReq.getPassword().equals("")) {
                return new BaseResponse<>(PATCH_USERS_EMPTY_PASSWORD);
            }
            PatchUserStatusRes patchUserStatusRes = userService.deleteAccount(userIdx, patchUserStatusReq.getPassword());
            return new BaseResponse<>(patchUserStatusRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    
}







