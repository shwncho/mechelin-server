package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.model.GetStoreRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service

public class StoreProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;

    @Autowired
    public StoreProvider(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    // ************************************************************************************

    // 최신순 정렬
    public List<GetStoreRes> getCategoryByDate(int userIdx, int categoryIdx, String deliveryService, int pageNo) throws BaseException {
        try {
            List<GetStoreRes> getCategoryRes;

            // category => 전체보기
            if (storeDao.checkCategoryIdx(categoryIdx) == 1) {
                // 전체보기 - 최신순 정렬 + 배달 가능 설정 안 함
                if (deliveryService.equals("N")) {
                    getCategoryRes = storeDao.getAllByDate(userIdx, pageNo);
                }
                // 전체보기 - 최신순 정렬 + 배달 가능
                else {
                    getCategoryRes = storeDao.getAllByDateAndDelivery(userIdx, deliveryService, pageNo);
                }
            }
            // category => 한식 ..
            else {
                // 카테고리 - 최신순 정렬 + 배달 가능 설정 안 함
                if (deliveryService.equals("N")) {
                    getCategoryRes = storeDao.getCategoryByDate(userIdx, categoryIdx, pageNo);
                }
                // 카테고리 - 최신순 정렬 + 배달 가능
                else {
                    getCategoryRes = storeDao.getCategoryByDateAndDelivery(userIdx, categoryIdx, deliveryService, pageNo);
                }
            }
            return getCategoryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 별점순 정렬
    public List<GetStoreRes> getCategoryByStarRate(int userIdx, int categoryIdx, String deliveryService, int pageNo) throws BaseException {
        try {
            List<GetStoreRes> getCategoryRes;

            // category => 전체보기
            if (storeDao.checkCategoryIdx(categoryIdx) == 1) {
                // 전체보기 - 별점순 정렬 + 배달 가능 설정 안 함
                if (deliveryService.equals("N")) {
                    getCategoryRes = storeDao.getAllByStarRate(userIdx, pageNo);
                }
                // 전체보기 - 별점순 정렬 + 배달 가능
                else {
                    getCategoryRes = storeDao.getAllByStarRateAndDelivery(userIdx, deliveryService, pageNo);
                }
            }
            // category => 한식 ..
            else {
                // 카테고리 - 최신순 정렬 + 배달 가능 설정 안 함
                if (deliveryService.equals("N")) {
                    getCategoryRes = storeDao.getCategoryByStarRate(userIdx, categoryIdx, pageNo);
                }
                // 카테고리 - 최신순 정렬 + 배달 가능
                else {
                    getCategoryRes = storeDao.getCategoryByStarRateAndDelivery(userIdx, categoryIdx, deliveryService, pageNo);
                }
            }
            return getCategoryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
