package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetMainScreenReviewRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
}
