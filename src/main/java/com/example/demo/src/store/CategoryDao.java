package com.example.demo.src.store;

import com.example.demo.src.store.model.GetCategoryRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Repository
public class CategoryDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // ************************************************************************************

    // 입력받은 categoryIdx가 전체 보기인지 아닌지 확인
    public int checkCategoryIdx(int categoryIdx) {
        String checkCategoryIdxQuery = "select ? = categoryIdx " +
                "from Category " +
                "where categoryName = '전체보기' ";
        int checkCategoryIdxParam = categoryIdx;
        return this.jdbcTemplate.queryForObject(checkCategoryIdxQuery, int.class, checkCategoryIdxParam);
    }

    /**
     * 최신순으로 식당 정렬
     */
    // 최신순으로 정렬 - 전체 식당 조회
    public List<GetCategoryRes> getAllByDate(int userIdx, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, substring_index(group_concat(T.tagName separator ' '), ' ', 2) as tag, substring_index(S.address, ' ', 2) as address " +
                       "from (" +
                       "select min(reviewIdx) as reviewIdx, userIdx, storeIdx, categoryIdx, ROUND(AVG(starRate), 1) as starRate, createdAt " +
                       "from Review " +
                       "group by storeIdx" +
                       ") as R," +
                       "( " +
                       "select min(reviewImageIdx), reviewIdx, imageUrl " +
                       "from ReviewImage " +
                       "group by reviewIdx " +
                       ") as RI, " +
                       "Store S, " +
                       "ReviewTag RT, " +
                       "Tag T " +
                       "where R.reviewIdx = RI.reviewIdx " +
                       "and R.storeIdx = S.storeIdx " +
                       "and R.reviewIdx = RT.reviewIdx " +
                       "and RT.tagIdx = T.tagIdx " +
                       "and R.userIdx = ? " +
                       "group by R.reviewIdx, R.createdAt " +
                       "order by R.createdAt DESC " +
                       "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{userIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 최신순으로 정렬 - 전체 식당 조회 - 배달 가능한 식당만 조회
    public List<GetCategoryRes> getAllByDateAndDelivery(int userIdx, String deliveryService, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, substring_index(group_concat(T.tagName separator ' '), ' ', 2) as tag, substring_index(S.address, ' ', 2) as address " +
                        "from (" +
                        "select min(reviewIdx) as reviewIdx, userIdx, storeIdx, categoryIdx, ROUND(AVG(starRate), 1) as starRate, createdAt " +
                        "from Review " +
                        "group by storeIdx" +
                        ") as R," +
                        "( " +
                        "select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "from ReviewImage " +
                        "group by reviewIdx " +
                        ") as RI, " +
                        "( " +
                        "select storeIdx, storeName, address " +
                        "from Store " +
                        "where deliveryService = ? " +
                        ") as S, " +
                        "ReviewTag RT, " +
                        "Tag T " +
                        "where R.reviewIdx = RI.reviewIdx " +
                        "and R.storeIdx = S.storeIdx " +
                        "and R.reviewIdx = RT.reviewIdx " +
                        "and RT.tagIdx = T.tagIdx " +
                        "and R.userIdx = ? " +
                        "group by R.reviewIdx, R.createdAt " +
                        "order by R.createdAt DESC " +
                        "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{deliveryService, userIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 최신순으로 정렬 - 카테고리별 식당 조회
    public List<GetCategoryRes> getCategoryByDate(int userIdx, int categoryIdx, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, substring_index(group_concat(T.tagName separator ' '), ' ', 2) as tag, substring_index(S.address, ' ', 2) as address " +
                        "from (" +
                        "select min(reviewIdx) as reviewIdx, userIdx, storeIdx, categoryIdx, ROUND(AVG(starRate), 1) as starRate, createdAt " +
                        "from Review " +
                        "group by storeIdx" +
                        ") as R," +
                        "( " +
                        "select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "from ReviewImage " +
                        "group by reviewIdx " +
                        ") as RI, " +
                        "Store S, " +
                        "ReviewTag RT, " +
                        "Tag T " +
                        "where R.reviewIdx = RI.reviewIdx " +
                        "and R.storeIdx = S.storeIdx " +
                        "and R.reviewIdx = RT.reviewIdx " +
                        "and RT.tagIdx = T.tagIdx " +
                        "and R.userIdx = ? " +
                        "and R.categoryIdx = ? " +
                        "group by R.reviewIdx, R.createdAt " +
                        "order by R.createdAt DESC " +
                        "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{userIdx, categoryIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 최신순으로 정렬 - 카테고리별 식당 조회 - 배달 가능한 식당만 조회
    public List<GetCategoryRes> getCategoryByDateAndDelivery(int userIdx, int categoryIdx, String deliveryService, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, substring_index(group_concat(T.tagName separator ' '), ' ', 2) as tag, substring_index(S.address, ' ', 2) as address " +
                        "from (" +
                        "select min(reviewIdx) as reviewIdx, userIdx, storeIdx, categoryIdx, ROUND(AVG(starRate), 1) as starRate, createdAt " +
                        "from Review " +
                        "group by storeIdx" +
                        ") as R," +
                        "( " +
                        "select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "from ReviewImage " +
                        "group by reviewIdx " +
                        ") as RI, " +
                        "( " +
                        "select storeIdx, storeName, address " +
                        "from Store " +
                        "where deliveryService = ? " +
                        ") as S, " +
                        "ReviewTag RT, " +
                        "Tag T " +
                        "where R.reviewIdx = RI.reviewIdx " +
                        "and R.storeIdx = S.storeIdx " +
                        "and R.reviewIdx = RT.reviewIdx " +
                        "and RT.tagIdx = T.tagIdx " +
                        "and R.userIdx = ? " +
                        "and R.categoryIdx = ? " +
                        "group by R.reviewIdx, R.createdAt " +
                        "order by R.createdAt DESC " +
                        "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{deliveryService, userIdx, categoryIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    /**
     * 별점순으로 식당 정렬
     */
    // 별점순 정렬 - 전체 식당 조회
    public List<GetCategoryRes> getAllByStarRate(int userIdx, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, substring_index(group_concat(T.tagName separator ' '), ' ', 2) as tag, substring_index(S.address, ' ', 2) as address " +
                        "from (" +
                        "select min(reviewIdx) as reviewIdx, userIdx, storeIdx, categoryIdx, ROUND(AVG(starRate), 1) as starRate, createdAt " +
                        "from Review " +
                        "group by storeIdx" +
                        ") as R," +
                        "( " +
                        "select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "from ReviewImage " +
                        "group by reviewIdx " +
                        ") as RI, " +
                        "Store S, " +
                        "ReviewTag RT, " +
                        "Tag T " +
                        "where R.reviewIdx = RI.reviewIdx " +
                        "and R.storeIdx = S.storeIdx " +
                        "and R.reviewIdx = RT.reviewIdx " +
                        "and RT.tagIdx = T.tagIdx " +
                        "and R.userIdx = ? " +
                        "group by R.reviewIdx, R.starRate " +
                        "order by R.starRate DESC " +
                        "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{userIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 별점순 정렬 - 전체 식당 조회 - 배달 가능한 식당만 조회
    public List<GetCategoryRes> getAllByStarRateAndDelivery(int userIdx, String deliveryService, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, substring_index(group_concat(T.tagName separator ' '), ' ', 2) as tag, substring_index(S.address, ' ', 2) as address " +
                        "from (" +
                        "select min(reviewIdx) as reviewIdx, userIdx, storeIdx, categoryIdx, ROUND(AVG(starRate), 1) as starRate, createdAt " +
                        "from Review " +
                        "group by storeIdx" +
                        ") as R," +
                        "( " +
                        "select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "from ReviewImage " +
                        "group by reviewIdx " +
                        ") as RI, " +
                        "( " +
                        "select storeIdx, storeName, address " +
                        "from Store " +
                        "where deliveryService = ? " +
                        ") as S, " +
                        "ReviewTag RT, " +
                        "Tag T " +
                        "where R.reviewIdx = RI.reviewIdx " +
                        "and R.storeIdx = S.storeIdx " +
                        "and R.reviewIdx = RT.reviewIdx " +
                        "and RT.tagIdx = T.tagIdx " +
                        "and R.userIdx = ? " +
                        "group by R.reviewIdx, R.starRate " +
                        "order by R.starRate DESC " +
                        "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{deliveryService, userIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 별점순 정렬 - 카테고리별 식당 조회
    public List<GetCategoryRes> getCategoryByStarRate(int userIdx, int categoryIdx, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, substring_index(group_concat(T.tagName separator ' '), ' ', 2) as tag, substring_index(S.address, ' ', 2) as address " +
                        "from (" +
                        "select min(reviewIdx) as reviewIdx, userIdx, storeIdx, categoryIdx, ROUND(AVG(starRate), 1) as starRate, createdAt " +
                        "from Review " +
                        "group by storeIdx" +
                        ") as R," +
                        "( " +
                        "select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "from ReviewImage " +
                        "group by reviewIdx " +
                        ") as RI, " +
                        "Store S, " +
                        "ReviewTag RT, " +
                        "Tag T " +
                        "where R.reviewIdx = RI.reviewIdx " +
                        "and R.storeIdx = S.storeIdx " +
                        "and R.reviewIdx = RT.reviewIdx " +
                        "and RT.tagIdx = T.tagIdx " +
                        "and R.userIdx = ? " +
                        "and R.categoryIdx = ? " +
                        "group by R.reviewIdx, R.starRate " +
                        "order by R.starRate DESC " +
                        "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{userIdx, categoryIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 별점순 정렬 - 카테고리별 식당 조회 - 배달 가능한 식당만 조회
    public List<GetCategoryRes> getCategoryByStarRateAndDelivery(int userIdx, int categoryIdx, String deliveryService, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, substring_index(group_concat(T.tagName separator ' '), ' ', 2) as tag, substring_index(S.address, ' ', 2) as address " +
                        "from (" +
                        "select min(reviewIdx) as reviewIdx, userIdx, storeIdx, categoryIdx, ROUND(AVG(starRate), 1) as starRate, createdAt " +
                        "from Review " +
                        "group by storeIdx" +
                        ") as R," +
                        "( " +
                        "select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "from ReviewImage " +
                        "group by reviewIdx " +
                        ") as RI, " +
                        "( " +
                        "select storeIdx, storeName, address " +
                        "from Store " +
                        "where deliveryService = ? " +
                        ") as S, " +
                        "ReviewTag RT, " +
                        "Tag T " +
                        "where R.reviewIdx = RI.reviewIdx " +
                        "and R.storeIdx = S.storeIdx " +
                        "and R.reviewIdx = RT.reviewIdx " +
                        "and RT.tagIdx = T.tagIdx " +
                        "and R.userIdx = ? " +
                        "group by R.reviewIdx, R.starRate " +
                        "order by R.starRate DESC " +
                        "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{deliveryService, userIdx, categoryIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }
}
