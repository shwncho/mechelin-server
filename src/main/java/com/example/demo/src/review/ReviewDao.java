package com.example.demo.src.review;

import com.example.demo.src.review.model.*;
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

    public List<Integer> getReviewTagIdx(int reviewIdx){
        String Query ="select reviewTagIdx\n" +
                "    from Review\n" +
                "        inner join ReviewTag\n" +
                "            on ReviewTag.reviewIdx = Review.reviewIdx\n" +
                "    where Review.status='A' AND Review.reviewIdx=?";
        int Param = reviewIdx;

        return this.jdbcTemplate.query(Query,
                (rs, rowNum) -> (rs.getInt("reviewTagIdx")),Param);
    }

    public void deleteReviewTag(int reviewTagIdx){
        String Query="UPDATE ReviewTag set status='D' where reviewTagIdx=?";

        this.jdbcTemplate.update(Query,reviewTagIdx);
    }

    public List<PatchReviewImageRes> getReviewImageIdx(int reviewIdx){
        String Query="select reviewImageIdx,imageUrl\n" +
                "    from Review\n" +
                "        inner join ReviewImage\n" +
                "            on ReviewImage.reviewIdx = Review.reviewIdx\n" +
                "    where Review.status='A' AND Review.reviewIdx=?";
        int Param = reviewIdx;
        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> new PatchReviewImageRes(
                        rs.getInt("reviewImageIdx"),
                        rs.getString("imageUrl")
                ),Param);
    }

    public void deleteReviewImage(int reviewImageIdx){
        String Query="UPDATE ReviewImage set status='D' where reviewImageIdx=?";

        this.jdbcTemplate.update(Query,reviewImageIdx);
    }

    public void deleteReview(int reviewIdx){
        String Query="UPDATE Review set status='D' where reviewIdx=?";

        this.jdbcTemplate.update(Query,reviewIdx);
    }

}
