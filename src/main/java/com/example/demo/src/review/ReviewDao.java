package com.example.demo.src.review;

import com.example.demo.src.review.model.GetMainScreenReviewRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetMainScreenReviewRes> getMainScreenReview(int userIdx) {
        String getMainScreenReviewQuery = "SELECT userIdx, Store.storeIdx, storeName, starRate, SUBSTR(contents,1,100) as contents, DATE_FORMAT(Review.createdAt,'%Y.%c.%d') as createdAt " +
                "FROM Review INNER JOIN Store ON Review.storeIdx = Store.storeIdx " +
                "WHERE userIdx = ? AND Review.status = 'a' ORDER BY Review.createdAt DESC LIMIT 5";

        int getMainScreenReviewParams = userIdx;

        return this.jdbcTemplate.query(getMainScreenReviewQuery,
                (rs, cnt) -> new GetMainScreenReviewRes(
                        rs.getInt("userIdx"),
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getDouble("starRate"),
                        rs.getString("contents"),
                        rs.getString("createdAt")
                ),
                getMainScreenReviewParams
                );
    }

}
