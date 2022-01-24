package com.example.demo.src.store;

import com.example.demo.src.store.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Repository
public class StoreDao {

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
    public List<GetStoreRes> getAllByDate(int userIdx, int pageNo) {
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
                "and S.status = 'A' " +
                "group by R.reviewIdx, R.createdAt " +
                "order by R.createdAt DESC " +
                "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{userIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 최신순으로 정렬 - 전체 식당 조회 - 배달 가능한 식당만 조회
    public List<GetStoreRes> getAllByDateAndDelivery(int userIdx, String deliveryService, int pageNo) {
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
                "select storeIdx, storeName, address, status " +
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
                "and S.status = 'A' " +
                "group by R.reviewIdx, R.createdAt " +
                "order by R.createdAt DESC " +
                "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{deliveryService, userIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 최신순으로 정렬 - 카테고리별 식당 조회
    public List<GetStoreRes> getCategoryByDate(int userIdx, int categoryIdx, int pageNo) {
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
                "and S.status = 'A' " +
                "group by R.reviewIdx, R.createdAt " +
                "order by R.createdAt DESC " +
                "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{userIdx, categoryIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 최신순으로 정렬 - 카테고리별 식당 조회 - 배달 가능한 식당만 조회
    public List<GetStoreRes> getCategoryByDateAndDelivery(int userIdx, int categoryIdx, String deliveryService, int pageNo) {
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
                "select storeIdx, storeName, address, status " +
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
                "and S.status = 'A' " +
                "group by R.reviewIdx, R.createdAt " +
                "order by R.createdAt DESC " +
                "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{deliveryService, userIdx, categoryIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreRes(
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
    public List<GetStoreRes> getAllByStarRate(int userIdx, int pageNo) {
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
                "and S.status = 'A' " +
                "group by R.reviewIdx, R.starRate " +
                "order by R.starRate DESC, S.storeName " +
                "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{userIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 별점순 정렬 - 전체 식당 조회 - 배달 가능한 식당만 조회
    public List<GetStoreRes> getAllByStarRateAndDelivery(int userIdx, String deliveryService, int pageNo) {
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
                "select storeIdx, storeName, address, status " +
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
                "and S.status = 'A' " +
                "group by R.reviewIdx, R.starRate " +
                "order by R.starRate DESC, S.storeName " +
                "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{deliveryService, userIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 별점순 정렬 - 카테고리별 식당 조회
    public List<GetStoreRes> getCategoryByStarRate(int userIdx, int categoryIdx, int pageNo) {
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
                "and S.status = 'A' " +
                "group by R.reviewIdx, R.starRate " +
                "order by R.starRate DESC, S.storeName " +
                "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{userIdx, categoryIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    // 별점순 정렬 - 카테고리별 식당 조회 - 배달 가능한 식당만 조회
    public List<GetStoreRes> getCategoryByStarRateAndDelivery(int userIdx, int categoryIdx, String deliveryService, int pageNo) {
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
                "select storeIdx, storeName, address, status " +
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
                "and S.status = 'A' " +
                "group by R.reviewIdx, R.starRate " +
                "order by R.starRate DESC, S.storeName " +
                "limit ?, 12 ";  // 12개씩 보이기
        Object[] params = new Object[]{deliveryService, userIdx, categoryIdx, (pageNo-1)*12};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeIdx"),
                        rs.getString("imageUrl"),
                        rs.getString("storeName"),
                        rs.getFloat("starRate"),
                        Arrays.asList(rs.getString("tag")),
                        rs.getString("address")),
                params);
    }

    public List<GetStoresRes> getStores(int userIdx){
        String getStoresquery= "SELECT distinct s.storeIdx, x,y\n" +
                "    FROM Store as s\n" +
                "        INNER JOIN Review as r\n" +
                "            ON r.storeIdx = s.storeIdx\n" +
                "        INNER JOIN User as u\n" +
                "            ON u.userIdx = r.userIdx\n" +
                "    WHERE u.userIdx=? AND s.status='A'";
        int getStoresParams = userIdx;
        return this.jdbcTemplate.query(getStoresquery,
                (rs, rowNum) -> new GetStoresRes(
                        rs.getInt("storeIdx"),
                        rs.getDouble("x"),
                        rs.getDouble("y")),
                getStoresParams);
    }
}
