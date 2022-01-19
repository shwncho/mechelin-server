package com.example.demo.src.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class CategoryService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CategoryDao categoryDao;
    private final CategoryProvider categoryProvider;

    @Autowired
    public CategoryService(CategoryDao categoryDao, CategoryProvider categoryProvider) {
        this.categoryDao = categoryDao;
        this.categoryProvider = categoryProvider;
    }

    // ******************************************************************************

}
