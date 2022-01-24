package com.example.demo.src.search;

import com.example.demo.src.search.model.*;
import com.example.demo.src.store.model.GetStoreRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Repository

public class SearchDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // ************************************************************************************

    // 입력 받은 문자열이 Store 테이블에 존재하는지 확인
    public int checkStoreName(String keyword) {
        String checkSearchQuery = "select exists(select storeName from Store where storeName like ?)";
        String checkSearchParam = "%" + keyword + "%";
        return this.jdbcTemplate.queryForObject(checkSearchQuery, int.class, checkSearchParam); // 해당 문자열이 식당 이름에 존재한다면 1을 반환
    }

    // 입력 받은 문자열이 가게 이름인 경우 해당 가게 정보 조회
    public List<GetSearchByStoreName> getStoresByStoreName(int userIdx, String keyword) {
        String query = "select S.storeIdx, S.storeName, S.address " +
                        "from Store S, " +
                        "     Review R " +
                        "where R.storeIdx = S.storeIdx " +
                        "    and R.userIdx = ?" +
                        "    and S.status = 'A'" +
                        "    and S.storeName like ?" +
                        "group by S.storeIdx";
        Object[] params = new Object[]{userIdx, "%" + keyword + "%"};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetSearchByStoreName(
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getString("address")),
                params);
    }

    // 입력 받은 문자열이 Tag에 테이블에 존재하는지 확인
    public int checkHashtag(String keyword) {
        String checkHashtagQuery = "select exists(select tagName from Tag where tagName like ?)";
        String checkHashtagParam = "%" + keyword + "%";
        return this.jdbcTemplate.queryForObject(checkHashtagQuery, int.class, checkHashtagParam);   // 해당 문자열이 해시태그에 존재한다면 1을 반환
    }


    // 입력 받은 문자열이 해시태그인 경우 해당 해시태그에 대한 식당들의 개수 반환
    public List<GetSearchByHashtag> countByHashTag(int userIdx, String keyword) {
        String query = "select T.tagIdx, T.tagName, count(S.storeIdx) as count " +
                        "from Store S, " +
                        "     Review R, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where S.storeIdx = R.storeIdx " +
                        "  and R.reviewIdx = RT.reviewIdx " +
                        "  and RT.tagIdx = T.tagIdx " +
                        "    and R.userIdx = ?" +
                        "  and S.status = 'A' " +
                        "  and T.tagName like ? " +
                        "group by T.tagIdx";
        Object[] params = new Object[]{userIdx, "%" + keyword + "%"};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetSearchByHashtag(
                        rs.getInt("tagIdx"),
                        rs.getString("tagName"),
                        rs.getInt("count")),
                params);

    }

    // 검색한 Tag를 클릭하면 해당 해시태그에 대한 식당 정보 조회
    public List<GetStoreRes> getStoresByHashtag(int userIdx, int tagIdx, int pageNo) {
        String query = "select S.storeIdx, RI.imageUrl as imageUrl, S.storeName, R.starRate, T.tagName as tag, substring_index(S.address, ' ', 2) as address " +
                        "from ( " +
                        "        select min(reviewIdx) as reviewIdx, userIdx, storeIdx, ROUND(AVG(starRate), 1) as starRate " +
                        "        from Review " +
                        "         group by storeIdx, userIdx, reviewIdx " +
                        "     ) as R, " +
                        "     ( " +
                        "        select min(reviewImageIdx), reviewIdx, imageUrl " +
                        "        from ReviewImage " +
                        "        group by reviewIdx " +
                        "     ) as RI, " +
                        "     Store S, " +
                        "     ReviewTag RT, " +
                        "     Tag T " +
                        "where R.reviewIdx = RI.reviewIdx " +
                        "    and R.storeIdx = S.storeIdx " +
                        "    and R.reviewIdx = RT.reviewIdx " +
                        "    and RT.tagIdx = T.tagIdx " +
                        "    and R.userIdx = ? " +
                        "    and S.status = 'A' " +
                        "    and T.tagIdx = ? " +
                        "limit ?, 12";  // 식당 정보 12개씩 보이기
        Object[] params = new Object[]{userIdx, tagIdx, (pageNo-1)*12};
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

}
