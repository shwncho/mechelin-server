package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.AwsS3Service;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/stores")

public class StoreController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreProvider storeProvider;
    private final StoreService storeService;
    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;

    @Autowired
    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService,AwsS3Service awsS3Service) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
        this.awsS3Service = awsS3Service;
    }

    // ************************************************************************************

    /**
     * 카테고리별 가게 조회
     * [GET] /stores/category
     */

    @ResponseBody
    @GetMapping("/category")

    public BaseResponse<List<GetStoreRes>> getCategory (@RequestParam("categoryIdx") int categoryIdx, @RequestParam(value="starRating", required = false, defaultValue = "N") String starRating, @RequestParam(value = "deliveryService", required = false, defaultValue = "N") String deliveryService, @RequestParam(defaultValue = "1") int pageNo) {
        try {
            int userIdx = jwtService.getUserIdx();

            List<GetStoreRes> getStoreRes;

            // starRating 값이 'N' 이면, 최신순으로 정렬한다.
            if (starRating.equals("N")) {
                getStoreRes = storeProvider.getCategoryByDate(userIdx, categoryIdx, deliveryService, pageNo);
            }
            // starRating 값이 'Y' 이면, 별점순으로 정렬한다.
            else {
                getStoreRes = storeProvider.getCategoryByStarRate(userIdx, categoryIdx, deliveryService, pageNo);
            }

            return new BaseResponse<>(getStoreRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     *지도의 전체가게 조회
     *[GET] /stores
     */
    @ResponseBody
    @GetMapping("")

    public BaseResponse<List<GetStoresRes>> getStores(){
        try{
            int userIdx = jwtService.getUserIdx();
            List<GetStoresRes> getStoresRes = storeProvider.getStores(userIdx);
            return new BaseResponse<>(getStoresRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //식당 저장 및 리뷰 작성
    @ResponseBody
    @PostMapping(value="",consumes = {"multipart/form-data"})
    public BaseResponse<PostStoreRes> createStore(  @RequestPart PostStoreReq postStoreReq,
                                              @RequestPart(required = false) List<MultipartFile> imageFile) {
        try{
            if(postStoreReq.getUserIdx()==0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            int userIdxByJwt = jwtService.getUserIdx();
            if(postStoreReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(postStoreReq.getCategoryIdx()==0){
                return new BaseResponse<>(POST_STORE_EMPTY_CATEGORY);
            }

            if(postStoreReq.getStoreName().isEmpty()){
                return new BaseResponse<>(POST_STORE_EMPTY_RESTAURANT);
            }
            if(postStoreReq.getStarRate()==0){
                return new BaseResponse<>(POST_STORE_EMPTY_STAR);
            }
            if(postStoreReq.getContents().isEmpty()){
                return new BaseResponse<>(POST_STORE_EMPTY_CONTENTS);
            }
            if(storeProvider.checkStore(postStoreReq.getUserIdx(), postStoreReq.getStoreName(), postStoreReq.getAddress())==1){
                return new BaseResponse<>(POST_STORE_EXISTS_RESTAURANT);
            }
            List<String> fileNameList = new ArrayList<>();
            if(imageFile!=null) fileNameList = awsS3Service.uploadFile(imageFile);


            PostStoreRes postStoreRes = storeService.createStore(postStoreReq, fileNameList);

            return new BaseResponse<>(postStoreRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/{userIdx}/{storeIdx}/status")
    public BaseResponse<String> deleteStore(@PathVariable int userIdx, @PathVariable int storeIdx){
        try{
            if(userIdx==0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx!=userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(storeIdx==0){
                return new BaseResponse<>(STORES_EMPTY_STORE_ID);
            }

            storeService.deleteStore(userIdx, storeIdx);
            String result="식당이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
