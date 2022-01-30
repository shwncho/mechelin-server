package com.example.demo.src.store;


import com.example.demo.config.BaseException;
import com.example.demo.src.review.ReviewDao;
import com.example.demo.src.review.ReviewProvider;
import com.example.demo.src.review.model.PatchReviewImageRes;
import com.example.demo.src.store.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.*;


import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service

public class StoreService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final StoreProvider storeProvider;
    private final ReviewProvider reviewProvider;
    private final ReviewDao reviewDao;

    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider, ReviewProvider reviewProvider, ReviewDao reviewDao) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
        this.reviewProvider = reviewProvider;
        this.reviewDao = reviewDao;
    }
    @Transactional(rollbackFor = BaseException.class)
    public PostStoreRes createStore(PostStoreReq postStoreReq, List<String> fileNameList) throws BaseException{
        try{


            int reviewIdx=storeDao.createStore(postStoreReq);
            int storeIdx=storeDao.searchStoreIdx(postStoreReq.getUserIdx(),postStoreReq.getStoreName(),postStoreReq.getAddress());

            if(!(fileNameList.isEmpty())) {
                for (String imgURL : fileNameList) {
                    storeDao.createImage(imgURL, reviewIdx);
                }
            }
            if(!(postStoreReq.getTagName().isEmpty())) {
                for (String tag : postStoreReq.getTagName()) {
                    int tagIdx = storeDao.checkTagName(tag);

                    if (tagIdx == 0) {
                        storeDao.createTag(reviewIdx, tag);
                    } else {
                        storeDao.createIsTag(reviewIdx, tagIdx);
                    }
                }
            }
            return new PostStoreRes(postStoreReq.getUserIdx(),storeIdx);

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = BaseException.class)
    public void deleteStore(int userIdx, int storeIdx) throws BaseException{
        try{
             List<Integer> reviewIdx=storeDao.getReviewIdx(userIdx,storeIdx);

             for(int rIdx : reviewIdx){
                 List<Integer> reviewTagIdx = reviewProvider.getReviewTagIdx(userIdx,rIdx);
                 for(int idx : reviewTagIdx){
                     reviewDao.deleteReviewTag(idx);
                 }

                 List<Integer> imageIdx = reviewProvider.getReviewImageIdx(userIdx, rIdx);
                 for(int t : imageIdx){
                     reviewDao.deleteReviewImage(t);
                 }

                 reviewDao.deleteReview(rIdx);

             }

            storeDao.deleteStore(storeIdx);

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
