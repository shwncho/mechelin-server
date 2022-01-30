package com.example.demo.src.review;

import com.example.demo.src.review.model.*;
import com.example.demo.src.review.model.GetMainScreenReviewRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 메인화면 최근리뷰 조회
    public List<GetMainScreenReviewRes> getMainScreenReview(int userIdx) {
        String getMainScreenReviewQuery = "SELECT R.userIdx, S.storeIdx, storeName, starRate, SUBSTR(contents,1,100) as contents, DATE_FORMAT(R.createdAt,'%Y.%c.%d') as createdAt " +
                "FROM Review as R INNER JOIN Store as S ON R.storeIdx = S.storeIdx " +
                "WHERE R.userIdx = ? AND R.status = 'a' ORDER BY R.createdAt DESC LIMIT 5";

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

    // 식당정보 조회
    public GetStoreInformationRes getStoreInformation(int userIdx, int storeIdx) {
        String getStoreInformationQuery = "SELECT storeName, address, tel, Round(avg(starRate), 1) AS starRate " +
                "FROM Store AS S " +
                "INNER JOIN Review AS R ON R.storeIdx = S.storeIdx " +
                "INNER JOIN ReviewTag AS RT ON R.reviewIdx = RT.reviewIdx " +
                "WHERE R.userIdx = ? AND S.storeIdx = ? AND R.status = 'a' ";

      Object[] getStoreInformationParams = new Object[] {userIdx, storeIdx};

        return this.jdbcTemplate.queryForObject(getStoreInformationQuery,
                (rs, cnt) -> new GetStoreInformationRes(
                        rs.getDouble("starRate"),
                        rs.getString("storeName"),
                        rs.getString("address"),
                        rs.getString("tel")
                ),
                getStoreInformationParams
                );
    }

    // 리뷰 조회
    public List<GetReviewRes> getReview(int userIdx, int storeIdx, int page, int pageSize) {
        // 페이지가 1보다 작을 경우 1로 설정
        if (page <= 1) {
            page = 1;
        }
        int start = (page - 1) * pageSize;

        System.out.println(page);

        String getReviewQuery = "SELECT R.reviewIdx, group_concat(imageUrl) AS imageUrl, contents, " +
        "CASE WEEKDAY(R.createdAt) " +
        "WHEN '0' THEN CONCAT(DATE_FORMAT(R.createdAt,'%Y.%c.%d'), ' 월요일') " +
        "WHEN '1' THEN CONCAT(DATE_FORMAT(R.createdAt,'%Y.%c.%d'), ' 화요일') " +
        "WHEN '2' THEN CONCAT(DATE_FORMAT(R.createdAt,'%Y.%c.%d'), ' 수요일') " +
        "WHEN '3' THEN CONCAT(DATE_FORMAT(R.createdAt,'%Y.%c.%d'), ' 목요일') " +
        "WHEN '4' THEN CONCAT(DATE_FORMAT(R.createdAt,'%Y.%c.%d'), ' 금요일') " +
        "WHEN '5' THEN CONCAT(DATE_FORMAT(R.createdAt,'%Y.%c.%d'), ' 토요일') " +
        "WHEN '6' THEN CONCAT(DATE_FORMAT(R.createdAt,'%Y.%c.%d'), ' 일요일') " +
        "END AS createdAt, " +
        "starRate " +
        "FROM Review as R " +
        "INNER JOIN ReviewImage AS RI ON R.reviewIdx = RI.reviewIdx " +
        "WHERE R.userIdx = ? AND R.storeIdx = ? AND R.status = 'a' " +
        "GROUP BY R.reviewIdx, R.createdAt " +
        "ORDER BY R.createdAt DESC " +
        "LIMIT ?,?";

        String getTagQuery = "SELECT R.reviewIdx, GROUP_CONCAT(tagName) as tagName " +
                "FROM Review as R " +
                "INNER JOIN ReviewTag RT on R.reviewIdx = RT.reviewIdx " +
                "INNER JOIN Tag T on RT.tagIdx = T.tagIdx " +
                "WHERE R.userIdx = ? AND R.storeIdx = ? AND R.status = 'a' " +
                "GROUP BY R.reviewIdx, R.createdAt " +
                "ORDER BY R.createdAt DESC " +
                "LIMIT ?,?";

        // 리뷰인덱스에 해당하는 태그 저장
        Map<Integer, List<String>> tagByReviewIdx = this.jdbcTemplate.query(getTagQuery, new ResultSetExtractor<Map>() {
            @Override
            public Map extractData(ResultSet rs) throws SQLException, DataAccessException {
                HashMap<Integer, List<String>> map = new HashMap<>();
                while(rs.next()) {
                    map.put(rs.getInt("reviewIdx"), Arrays.asList(rs.getString("tagName").split(",")));
                }
                return map;
            }
        }, userIdx, storeIdx, start, pageSize);

        return this.jdbcTemplate.query(getReviewQuery,
                (rs, cnt) -> new GetReviewRes(
                        rs.getInt("reviewIdx"),
                        Arrays.asList(rs.getString("imageUrl").split(",")),
                        rs.getString("contents"),
                        rs.getString("createdAt"),
                        tagByReviewIdx.get(rs.getInt("reviewIdx")),
                        rs.getDouble("starRate")

                ),
                userIdx, storeIdx, start, pageSize
        );

    }
}
