package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.src.search.model.*;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service

public class SearchProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchDao searchDao;
    private final UserProvider userProvider;

    @Autowired
    public SearchProvider(SearchDao searchDao, UserProvider userProvider) {
        this.searchDao = searchDao;
        this.userProvider = userProvider;
    }

    // ************************************************************************************


    // 카테고리 + 식당 검색 결과 확인
    @Transactional(readOnly = true)
    public GetSearchRes getSearch(int userIdx, String keyword) throws BaseException {

        if (userProvider.checkUser(userIdx) == 0) {
            throw new BaseException(EMPTY_USER);
        }

        // 해시태그 검색
        List<GetSearchByHashtag> getCountStoreByHashtag = null;

        // 식당 이름 검색
        List<GetSearchByStoreName> getStoresByStoreName = null;

        // 해시태그 검색 결과가 존재한다면
        if (searchDao.checkHashtag(keyword) == 1) {
            getCountStoreByHashtag = searchDao.countByHashTag(userIdx, keyword);
        }

        // 식당 이름 검색 결과가 존재한다면
        if (searchDao.checkStoreName(keyword) == 1) {
            getStoresByStoreName = searchDao.getStoresByStoreName(userIdx, keyword);
        }

        // 해시태그 & 식당 이름 검색 결과가 존재하지 않는다면
        if (getCountStoreByHashtag == null && getStoresByStoreName == null) {
            throw new BaseException(NO_RESULT_FOUND);
        }

        try {
            GetSearchRes getSearchRes = new GetSearchRes(getCountStoreByHashtag, getStoresByStoreName);
            return getSearchRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 해시태그 검색 결과 확인
    @Transactional(readOnly = true)
    public List<GetStoreRes> getStoresByHashtag(int userIdx, int tagIdx, int page, int pageSize) throws BaseException {

        if (userProvider.checkUser(userIdx) == 0) {
            throw new BaseException(EMPTY_USER);
        }

        if (searchDao.checkTag(userIdx, tagIdx) == 0) {
            throw new BaseException(EMPTY_TAG);
        }

        try {
            List<GetStoreRes> getStoresByHashtag = searchDao.getStoresByHashtag(userIdx, tagIdx, page, pageSize);
            return getStoresByHashtag;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
