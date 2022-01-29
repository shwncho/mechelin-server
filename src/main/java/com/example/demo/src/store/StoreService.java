package com.example.demo.src.store;


import com.example.demo.config.BaseException;
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

    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
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

    // ******************************************************************************


}
