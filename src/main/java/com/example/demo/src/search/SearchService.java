package com.example.demo.src.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class SearchService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchDao searchDao;
    private final SearchProvider searchProvider;

    @Autowired
    public SearchService(SearchDao searchDao, SearchProvider searchProvider) {
        this.searchDao = searchDao;
        this.searchProvider = searchProvider;
    }

    // ************************************************************************************

}
