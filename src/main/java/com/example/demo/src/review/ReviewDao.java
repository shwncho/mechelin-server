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

    public List<Integer> getReviewTagIdx(int userIdx, int reviewIdx){
        String Query ="select reviewTagIdx\n" +
                "    from Review\n" +
                "        inner join ReviewTag\n" +
                "            on ReviewTag.reviewIdx = Review.reviewIdx\n" +
                "    where Review.userIdx=? AND Review.status='A' AND Review.reviewIdx=?";
        int Param1 = userIdx;
        int Param2 = reviewIdx;

        return this.jdbcTemplate.query(Query,
                (rs, rowNum) -> (rs.getInt("reviewTagIdx")),Param1,Param2);
    }

    public void deleteReviewTag(int reviewTagIdx){
        String Query="UPDATE ReviewTag set status='D' where reviewTagIdx=?";

        this.jdbcTemplate.update(Query,reviewTagIdx);
    }

    public List<Integer> getReviewImageIdx(int userIdx,int reviewIdx){
        String Query="select reviewImageIdx\n" +
                "    from Review\n" +
                "        inner join ReviewImage\n" +
                "            on ReviewImage.reviewIdx = Review.reviewIdx\n" +
                "    where Review.userIdx=? AND Review.status='A' AND Review.reviewIdx=?";
        int Param1 = userIdx;
        int Param2 = reviewIdx;
        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> (rs.getInt("reviewImageIdx")),Param1,Param2);
    }

    public void deleteReviewImage(int reviewImageIdx){
        String Query="UPDATE ReviewImage set status='D' where reviewImageIdx=?";

        this.jdbcTemplate.update(Query,reviewImageIdx);
    }

    public void deleteReview(int reviewIdx){
        String Query="UPDATE Review set status='D' where reviewIdx=?";

        this.jdbcTemplate.update(Query,reviewIdx);
    }

    public int createReview(PostReviewReq postReviewReq){
        String Query="INSERT INTO Review (userIdx,storeIdx,starRate,contents) VALUES(?,?,?,?)";
        Object[] Param = new Object[]{postReviewReq.getUserIdx(),postReviewReq.getStoreIdx(),postReviewReq.getStarRate(),postReviewReq.getContents()};

        this.jdbcTemplate.update(Query,Param);
        String reviewIdx= "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(reviewIdx, int.class);
    }


    // 리뷰 수정
    public int editReview(PatchReviewReq patchReviewReq, int reviewIdx) {
        String query = "update Review set starRate = ?, contents = ? where reviewIdx = ?";
        Object[] params = new Object[]{patchReviewReq.getStarRate(), patchReviewReq.getContents(), reviewIdx};
        return this.jdbcTemplate.update(query, params);
    }

}
