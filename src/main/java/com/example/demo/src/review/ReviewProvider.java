package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import javax.sound.midi.Patch;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewProvider {
    ReviewDao reviewDao;

    @Autowired
    public ReviewProvider(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    @Transactional(readOnly = true)
    public List<GetMainScreenReviewRes> getMainScreenReview (int userIdx) throws BaseException {
        try {
            return reviewDao.getMainScreenReview(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public GetStoreInformationRes getStoreInformation (int userIdx, int storeIdx) throws BaseException {
        try {
            return reviewDao.getStoreInformation(userIdx, storeIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<Integer> getReviewTagIdx(int userIdx,int reviewIdx) throws BaseException{
        try{
            return reviewDao.getReviewTagIdx(userIdx,reviewIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<GetReviewRes> getReview (int userIdx, int storeIdx, int page, int pageSize) throws BaseException {
        try {
            return reviewDao.getReview(userIdx, storeIdx, page, pageSize);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Integer> getReviewImageIdx(int userIdx, int reviewIdx) throws BaseException{
        try{
            return reviewDao.getReviewImageIdx(userIdx, reviewIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
