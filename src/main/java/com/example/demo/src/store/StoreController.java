package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;


@RestController
@RequestMapping("/stores")

public class StoreController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreProvider storeProvider;
    private final StoreService storeService;
    private final JwtService jwtService;

    @Autowired
    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    // ************************************************************************************

    /**
     * 카테고리별 가게 조회
     * [GET] /stores/category
     */

    @ResponseBody
    @GetMapping("/{userIdx}/{categoryIdx}")

    public BaseResponse<List<GetStoreRes>> getCategory (@PathVariable("userIdx") int userIdx, @PathVariable("categoryIdx") int categoryIdx, @RequestParam(value="starRating", required = false, defaultValue = "N") String starRating, @RequestParam(value = "deliveryService", required = false, defaultValue = "N") String deliveryService, @RequestParam(defaultValue = "1") int page, @RequestParam int pageSize) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();

            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetStoreRes> getStoreRes;

            // starRating 값이 'N' 이면, 최신순으로 정렬한다.
            if (starRating.equals("N")) {
                getStoreRes = storeProvider.getCategoryByDate(userIdx, categoryIdx, deliveryService, page, pageSize);
            }
            // starRating 값이 'Y' 이면, 별점순으로 정렬한다.
            else {
                getStoreRes = storeProvider.getCategoryByStarRate(userIdx, categoryIdx, deliveryService, page, pageSize);
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

}