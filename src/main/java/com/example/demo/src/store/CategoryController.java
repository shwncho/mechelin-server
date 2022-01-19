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

public class CategoryController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CategoryProvider categoryProvider;
    private final CategoryService categoryService;
    private final JwtService jwtService;

    @Autowired
    public CategoryController(CategoryProvider categoryProvider, CategoryService categoryService, JwtService jwtService) {
        this.categoryProvider = categoryProvider;
        this.categoryService = categoryService;
        this.jwtService = jwtService;
    }

    // ************************************************************************************

    /**
     * 카테고리별 가게 조회
     * [GET] /stores/category
     */

    @ResponseBody
    @GetMapping("/category")

    public BaseResponse<List<GetCategoryRes>> getCategory(@RequestParam("userIdx") int userIdx, @RequestParam("categoryIdx") int categoryIdx, @RequestParam(value="starRating", required = false, defaultValue = "N") String starRating, @RequestParam(value = "deliveryService", required = false, defaultValue = "N") String deliveryService, int pageNo) {
        try {
            // jwt에서 idx 추출
            int userIdxByJwt = jwtService.getUserIdx();

            // userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // 같다면 카테고리별 가게 조회
            List<GetCategoryRes> getCategoryRes;

            // starRating 값이 'N' 이면, 최신순으로 정렬한다.
            if (starRating.equals("N")) {
                getCategoryRes = categoryProvider.getCategoryByDate(userIdx, categoryIdx, deliveryService, pageNo);
            }
            // starRating 값이 'Y' 이면, 별점순으로 정렬한다.
            else {
                getCategoryRes = categoryProvider.getCategoryByStarRate(userIdx, categoryIdx, deliveryService, pageNo);
            }

            return new BaseResponse<>(getCategoryRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
