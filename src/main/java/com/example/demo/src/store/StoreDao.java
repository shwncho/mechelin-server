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
    public List<GetStoreRes> getAllByDate(int userIdx, int page, int pageSize) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                        "from Store S, " +
                        "     ( " +
                        "        select min(reviewIdx) as reveiwIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                        "         from Review " +
                        "         group by reviewIdx " +
                        "     ) R, " +
                        "     ( " +
                        "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "        from ReviewImage " +
                        "        group by reviewIdx " +
                        "     ) as RI, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where S.userIdx = ? " +
                        "    and S.status = 'A' " +
                        "    and S.storeIdx = R.storeIdx " +
                        "    and R.reveiwIdx = RI.reviewIdx " +
                        "    and R.reveiwIdx = RT.reviewIdx " +
                        "    and RT.tagIdx = T.tagIdx " +
                        "group by S.storeIdx, S.createdAt " +
                        "order by S.createdAt DESC " +
                        "limit ?,? ";
        Object[] params = new Object[]{userIdx, (page-1)*pageSize, pageSize};
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
    public List<GetStoreRes> getAllByDateAndDelivery(int userIdx, String deliveryService, int page, int pageSize) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                        "from Store S, " +
                        "     ( " +
                        "        select min(reviewIdx) as reveiwIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                        "         from Review " +
                        "         group by reviewIdx " +
                        "     ) R, " +
                        "     ( " +
                        "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "        from ReviewImage " +
                        "        group by reviewIdx " +
                        "     ) as RI, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where S.userIdx = ? " +
                        "    and S.deliveryService = ? " +
                        "    and S.status = 'A' " +
                        "    and S.storeIdx = R.storeIdx " +
                        "    and R.reveiwIdx = RI.reviewIdx " +
                        "    and R.reveiwIdx = RT.reviewIdx " +
                        "    and RT.tagIdx = T.tagIdx " +
                        "group by S.storeIdx, S.createdAt " +
                        "order by S.createdAt DESC " +
                        "limit ?,? ";
        Object[] params = new Object[]{userIdx, deliveryService, (page-1)*pageSize, pageSize};
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
    public List<GetStoreRes> getCategoryByDate(int userIdx, int categoryIdx, int page, int pageSize) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                        "from Store S, " +
                        "     ( " +
                        "        select min(reviewIdx) as reveiwIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                        "         from Review " +
                        "         group by reviewIdx " +
                        "     ) R, " +
                        "     ( " +
                        "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "        from ReviewImage " +
                        "        group by reviewIdx " +
                        "     ) as RI, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where S.userIdx = ? " +
                        "    and S.categoryIdx = ? " +
                        "    and S.status = 'A' " +
                        "    and S.storeIdx = R.storeIdx " +
                        "    and R.reveiwIdx = RI.reviewIdx " +
                        "    and R.reveiwIdx = RT.reviewIdx " +
                        "    and RT.tagIdx = T.tagIdx " +
                        "group by S.storeIdx, S.createdAt " +
                        "order by S.createdAt DESC " +
                        "limit ?,? ";
        Object[] params = new Object[]{userIdx, categoryIdx, (page-1)*pageSize, pageSize};
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
    public List<GetStoreRes> getCategoryByDateAndDelivery(int userIdx, int categoryIdx, String deliveryService, int page, int pageSize) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                        "from Store S, " +
                        "     ( " +
                        "        select min(reviewIdx) as reveiwIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                        "         from Review " +
                        "         group by reviewIdx " +
                        "     ) R, " +
                        "     ( " +
                        "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "        from ReviewImage " +
                        "        group by reviewIdx " +
                        "     ) as RI, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where S.userIdx = ? " +
                        "    and S.categoryIdx = ? " +
                        "    and S.deliveryService = ? " +
                        "    and S.status = 'A' " +
                        "    and S.storeIdx = R.storeIdx " +
                        "    and R.reveiwIdx = RI.reviewIdx " +
                        "    and R.reveiwIdx = RT.reviewIdx " +
                        "    and RT.tagIdx = T.tagIdx " +
                        "group by S.storeIdx, S.createdAt " +
                        "order by S.createdAt DESC " +
                        "limit ?,? ";
        Object[] params = new Object[]{userIdx, categoryIdx, deliveryService, (page-1)*pageSize, pageSize};
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
    public List<GetStoreRes> getAllByStarRate(int userIdx, int page, int pageSize) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                        "from Store S, " +
                        "     ( " +
                        "        select min(reviewIdx) as reveiwIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                        "         from Review " +
                        "         group by storeIdx " +
                        "     ) R, " +
                        "     ( " +
                        "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "        from ReviewImage " +
                        "        group by reviewIdx " +
                        "     ) as RI, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where S.userIdx = ? " +
                        "    and S.status = 'A' " +
                        "    and S.storeIdx = R.storeIdx " +
                        "    and R.reveiwIdx = RI.reviewIdx " +
                        "    and R.reveiwIdx = RT.reviewIdx " +
                        "    and RT.tagIdx = T.tagIdx " +
                        "group by S.storeIdx, R.starRate, S.storeName " +
                        "order by R.starRate DESC, S.storeName " +
                        "limit ?,? ";
        Object[] params = new Object[]{userIdx, (page-1)*pageSize, pageSize};
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
    public List<GetStoreRes> getAllByStarRateAndDelivery(int userIdx, String deliveryService, int page, int pageSize) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                        "from Store S, " +
                        "     ( " +
                        "        select min(reviewIdx) as reveiwIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                        "         from Review " +
                        "         group by storeIdx " +
                        "     ) R, " +
                        "     ( " +
                        "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "        from ReviewImage " +
                        "        group by reviewIdx " +
                        "     ) as RI, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where S.userIdx = ? " +
                        "    and S.deliveryService = ? " +
                        "    and S.status = 'A' " +
                        "    and S.storeIdx = R.storeIdx " +
                        "    and R.reveiwIdx = RI.reviewIdx " +
                        "    and R.reveiwIdx = RT.reviewIdx " +
                        "    and RT.tagIdx = T.tagIdx " +
                        "group by S.storeIdx, R.starRate, S.storeName " +
                        "order by R.starRate DESC, S.storeName " +
                        "limit ?,? ";
        Object[] params = new Object[]{userIdx, deliveryService, (page-1)*pageSize, pageSize};
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
    public List<GetStoreRes> getCategoryByStarRate(int userIdx, int categoryIdx, int page, int pageSize) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                        "from Store S, " +
                        "     ( " +
                        "        select min(reviewIdx) as reveiwIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                        "         from Review " +
                        "         group by storeIdx " +
                        "     ) R, " +
                        "     ( " +
                        "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "        from ReviewImage " +
                        "        group by reviewIdx " +
                        "     ) as RI, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where S.userIdx = ? " +
                        "    and S.categoryIdx = ? " +
                        "    and S.status = 'A' " +
                        "    and S.storeIdx = R.storeIdx " +
                        "    and R.reveiwIdx = RI.reviewIdx " +
                        "    and R.reveiwIdx = RT.reviewIdx " +
                        "    and RT.tagIdx = T.tagIdx " +
                        "group by S.storeIdx, R.starRate, S.storeName " +
                        "order by R.starRate DESC, S.storeName " +
                        "limit ?,? ";
        Object[] params = new Object[]{userIdx, categoryIdx, (page-1)*pageSize, pageSize};
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
    public List<GetStoreRes> getCategoryByStarRateAndDelivery(int userIdx, int categoryIdx, String deliveryService, int page, int pageSize) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                "from Store S, " +
                "     ( " +
                "        select min(reviewIdx) as reveiwIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                "         from Review " +
                "         group by storeIdx " +
                "     ) R, " +
                "     ( " +
                "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                "        from ReviewImage " +
                "        group by reviewIdx " +
                "     ) as RI, " +
                "     ReviewTag RT, " +
                "     Tag T " +
                "where S.userIdx = ? " +
                "    and S.categoryIdx = ? " +
                "    and S.deliveryService = ? " +
                "    and S.status = 'A' " +
                "    and S.storeIdx = R.storeIdx " +
                "    and R.reveiwIdx = RI.reviewIdx " +
                "    and R.reveiwIdx = RT.reviewIdx " +
                "    and RT.tagIdx = T.tagIdx " +
                "group by S.storeIdx, R.starRate, S.storeName " +
                "order by R.starRate DESC, S.storeName " +
                "limit ?,? ";
        Object[] params = new Object[]{userIdx, categoryIdx, deliveryService, (page-1)*pageSize, pageSize};
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


    //Store가 이미 존재하는지 확인
    public int checkStore(int userIdx, String storeName,String address){
        String Query="select exists(select storeIdx\n" +
                "    from Store\n" +
                "    where userIdx=? AND Store.storeName=? AND Store.address=? AND Store.status='A')";
        int Params1 = userIdx;
        String Params2 = storeName;
        String Params3 = address;
        return this.jdbcTemplate.queryForObject(Query,int.class,Params1,Params2,Params3);
    }


    //Store, Review테이블에 삽입
    public int createStore(PostStoreReq postStoreReq){
        String start="START TRANSACTION";
        String Query1= "INSERT INTO Store (userIdx,categoryIdx,storeName,address,x,y,tel,deliveryService) VALUES(?,?,?,?,?,?,?,?)";
        String Query2= "INSERT INTO Review (userIdx,storeIdx,starRate,contents) VALUES(?,last_insert_id(),?,?)";
        String end="COMMIT";


        Object[] Params1 = new Object[]{postStoreReq.getUserIdx(),postStoreReq.getCategoryIdx(),postStoreReq.getStoreName(),postStoreReq.getAddress(),postStoreReq.getX(),postStoreReq.getY(),
                                        postStoreReq.getTel(),postStoreReq.getDeliveryService()};
        Object[] Params2 = new Object[]{postStoreReq.getUserIdx(),postStoreReq.getStarRate(),postStoreReq.getContents()};

        this.jdbcTemplate.update(start);
        this.jdbcTemplate.update(Query1,Params1);
        this.jdbcTemplate.update(Query2,Params2);
        this.jdbcTemplate.update(end);

        String reviewIdx= "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(reviewIdx, int.class);

    }

    //storeIdx를 조회
    public int searchStoreIdx(int userIdx, String storeName, String address){
        String Query="select storeIdx\n" +
                "    from Store\n" +
                "    where userIdx=? AND Store.storeName=? AND Store.address=? AND Store.status='A'";
        int Params1 = userIdx;
        String Params2 = storeName;
        String Params3 = address;
        return this.jdbcTemplate.queryForObject(Query,int.class,Params1,Params2,Params3);
    }


    //ReviewImage 삽입
    public void createImage(String imgURL,int reviewIdx){
        String Query="INSERT INTO ReviewImage (reviewIdx,imageUrl) VALUES(?,?)";
        Object[] Params = new Object[]{reviewIdx,imgURL};

        this.jdbcTemplate.update(Query, Params);
    }


    // DB에 tagName 여부 체크이후, tagIdx 반환
    public int checkTagName(String tagName){
        String Query1="select exists(select tagIdx from Tag where tagName=?)";
        String Query2="select tagIdx from Tag where tagName=?";
        if(this.jdbcTemplate.queryForObject(Query1, int.class,tagName)==1){
            return this.jdbcTemplate.queryForObject(Query2,int.class,tagName);
        }
        else{
            return 0;
        }
    }



    // tagName이 DB에 없을경우
    public void createTag(int reviewIdx, String tag){
        String start="START TRANSACTION";
        String Query1="INSERT INTO Tag (tagName) VALUES(?)";
        String Query2="INSERT INTO ReviewTag (reviewIdx,tagIdx) VALUES(?,last_insert_id())";
        String end="COMMIT";

        Object[] Params1 = new Object[]{tag};
        Object[] Params2 = new Object[]{reviewIdx};

        this.jdbcTemplate.update(start);
        this.jdbcTemplate.update(Query1,Params1);
        this.jdbcTemplate.update(Query2,Params2);
        this.jdbcTemplate.update(end);
    }

    // tagName이 DB에 있을경우
    public void createIsTag(int reviewIdx, int tagIdx){
        String Query="INSERT INTO ReviewTag (reviewIdx, tagIdx) VALUES(?,?)";
        Object[] Param = new Object[]{reviewIdx, tagIdx};

        this.jdbcTemplate.update(Query,Param);


    }

<<<<<<< HEAD
=======
    public List<Integer> getReviewIdx(int userIdx, int storeIdx){
        String Query="select reviewIdx\n" +
                "    from Review\n" +
                "        inner join Store\n" +
                "            on Store.storeIdx = Review.storeIdx\n" +
                "    where Store.userIdx=? AND Review.status='A' AND Store.storeIdx=?";

        return this.jdbcTemplate.query(Query,
                (rs,rowNum) -> (rs.getInt("reviewIdx")),userIdx,storeIdx);

    }

    public void deleteStore(int storeIdx){
        String Query="UPDATE Store set status='D' where storeIdx=?";

        this.jdbcTemplate.update(Query,storeIdx);
    }








>>>>>>> upstream/develop
}

