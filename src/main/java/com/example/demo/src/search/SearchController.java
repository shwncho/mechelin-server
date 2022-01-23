package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.search.model.*;
import com.example.demo.src.store.model.GetStoreRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/stores")

public class SearchController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchProvider searchProvider;
    private final SearchService searchService;
    private final JwtService jwtService;

    @Autowired
    public SearchController(SearchProvider searchProvider, SearchService searchService, JwtService jwtService) {
        this.searchProvider = searchProvider;
        this.searchService = searchService;
        this.jwtService = jwtService;
    }

    // ************************************************************************************

    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<GetSearchRes> getSearch(@RequestParam("keyword") String keyword) {
        try {
            int userIdx = jwtService.getUserIdx();

            GetSearchRes getSearchRes = searchProvider.getSearch(userIdx, keyword);

            return new BaseResponse<>(getSearchRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //페이징 처리
    @ResponseBody
    @GetMapping("/search/hashtag/{tagIdx}")
    public BaseResponse<List<GetStoreRes>> getStoresByHashTag(@PathVariable("tagIdx") int tagIdx, int pageNo) {
        try {
            int userIdx = jwtService.getUserIdx();

            List<GetStoreRes> getStoreRes = searchProvider.getStoresByHashtag(userIdx, tagIdx, pageNo);

            return new BaseResponse<>(getStoreRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}