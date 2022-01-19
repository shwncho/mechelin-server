package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.GetCategoryRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@Service

public class CategoryProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryProvider(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    // ************************************************************************************

    // 최신순 정렬
    public List<GetCategoryRes> getCategoryByDate(int userIdx, int categoryIdx, String deliveryService, int pageNo) throws BaseException {
        try {
            List<GetCategoryRes> getCategoryRes;

            // category => 전체보기
            if (categoryDao.checkCategoryIdx(categoryIdx) == 1) {
                // 전체보기 - 최신순 정렬 + 배달 가능 설정 안 함
                if (deliveryService.equals("N")) {
                    getCategoryRes = categoryDao.getAllByDate(userIdx, pageNo);
                }
                // 전체보기 - 최신순 정렬 + 배달 가능
                else {
                    getCategoryRes = categoryDao.getAllByDateAndDelivery(userIdx, deliveryService, pageNo);
                }
            }
            // category => 한식 ..
            else {
                // 카테고리 - 최신순 정렬 + 배달 가능 설정 안 함
                if (deliveryService.equals("N")) {
                    getCategoryRes = categoryDao.getCategoryByDate(userIdx, categoryIdx, pageNo);
                }
                // 카테고리 - 최신순 정렬 + 배달 가능
                else {
                    getCategoryRes = categoryDao.getCategoryByDateAndDelivery(userIdx, categoryIdx, deliveryService, pageNo);
                }
            }
            return getCategoryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 별점순 정렬
    public List<GetCategoryRes> getCategoryByStarRate(int userIdx, int categoryIdx, String deliveryService, int pageNo) throws BaseException {
        try {
            List<GetCategoryRes> getCategoryRes;

            // category => 전체보기
            if (categoryDao.checkCategoryIdx(categoryIdx) == 1) {
                // 전체보기 - 별점순 정렬 + 배달 가능 설정 안 함
                if (deliveryService.equals("N")) {
                    getCategoryRes = categoryDao.getAllByStarRate(userIdx, pageNo);
                }
                // 전체보기 - 별점순 정렬 + 배달 가능
                else {
                    getCategoryRes = categoryDao.getAllByStarRateAndDelivery(userIdx, deliveryService, pageNo);
                }
            }
            // category => 한식 ..
            else {
                // 카테고리 - 최신순 정렬 + 배달 가능 설정 안 함
                if (deliveryService.equals("N")) {
                    getCategoryRes = categoryDao.getCategoryByStarRate(userIdx, categoryIdx, pageNo);
                }
                // 카테고리 - 최신순 정렬 + 배달 가능
                else {
                    getCategoryRes = categoryDao.getCategoryByStarRateAndDelivery(userIdx, categoryIdx, deliveryService, pageNo);
                }
            }
            return getCategoryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
