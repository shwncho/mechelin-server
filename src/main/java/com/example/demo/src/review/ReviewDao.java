package com.example.demo.src.review;

import com.example.demo.src.review.model.*;
import com.example.demo.src.review.model.GetMainScreenReviewRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

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

    public GetStoreInformationRes getStoreInformation(int userIdx, int storeIdx) {
        String getStoreInformationQuery = "SELECT storeName, address, tel, Round(avg(starRate), 1) AS starRate, group_concat(tagName) AS tagName " +
                "FROM Store AS S " +
                "INNER JOIN Review AS R ON R.storeIdx = S.storeIdx " +
                "INNER JOIN ReviewTag AS RT ON R.reviewIdx = RT.reviewIdx " +
                "INNER JOIN Tag AS T on RT.tagIdx = T.tagIdx " +
                "WHERE userIdx = ? AND S.storeIdx = ? AND R.status = 'a' " +
                "LIMIT 10";

      Object[] getStoreInformationParams = new Object[] {userIdx, storeIdx};

        return this.jdbcTemplate.queryForObject(getStoreInformationQuery,
                (rs, cnt) -> new GetStoreInformationRes(
                        rs.getDouble("starRate"),
                        rs.getString("storeName"),
                        rs.getString("address"),
                        rs.getString("tel"),
                        Arrays.asList(rs.getString("tagName").split(","))
                ),
                getStoreInformationParams
                );
    }

    public List<GetReviewRes> getReview(int userIdx, int storeIdx, Integer lastIdx) {
        String getReviewQuery;

        if (lastIdx == null) {
            getReviewQuery = "SELECT R.reviewIdx, group_concat(imageUrl) AS imageUrl, contents, DATE_FORMAT(R.createdAt,'%Y.%c.%d') as createdAt " +
                    "FROM Review as R INNER JOIN ReviewImage AS RI ON R.reviewIdx = RI.reviewIdx " +
                    "WHERE R.userIdx = ? AND R.storeIdx = ? AND R.status = 'a' " +
                    "GROUP BY R.reviewIdx, R.createdAt " +
                    "ORDER BY R.createdAt DESC " +
                    "LIMIT 5";
            return this.jdbcTemplate.query(getReviewQuery,
                    (rs, cnt) -> new GetReviewRes(
                            rs.getInt("reviewIdx"),
                            Arrays.asList(rs.getString("imageUrl").split(",")),
                            rs.getString("contents"),
                            rs.getString("createdAt")
                    ),
                    userIdx, storeIdx
            );

        } else {
            getReviewQuery = "SELECT R.reviewIdx, group_concat(imageUrl) AS imageUrl, contents, DATE_FORMAT(R.createdAt,'%Y.%c.%d') as createdAt " +
            "FROM Review AS R INNER JOIN ReviewImage AS RI ON R.reviewIdx = RI.reviewIdx " +
            "WHERE R.userIdx = ? AND R.storeIdx = ? AND R.status = 'a' AND R.reviewIdx < ? "+
            "GROUP BY R.reviewIdx, R.createdAt " +
            "ORDER BY R.createdAt DESC " +
            "LIMIT 5";

            return this.jdbcTemplate.query(getReviewQuery,
                    (rs, cnt) -> new GetReviewRes(
                            rs.getInt("reviewIdx"),
                            Arrays.asList(rs.getString("imageUrl").split(",")),
                            rs.getString("contents"),
                            rs.getString("createdAt")
                    ),
                    userIdx, storeIdx, lastIdx
            );
        }

    }

}
