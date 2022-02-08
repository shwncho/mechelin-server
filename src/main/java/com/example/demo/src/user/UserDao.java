package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository


public class UserDao {


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (email, password, nickName,phoneNumber) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(), postUserReq.getPassword(), postUserReq.getNickName(),postUserReq.getPhoneNumber()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int checkUser(int userIdx){
        String Query="select exists(select userIdx from User where userIdx=?)";
        return this.jdbcTemplate.queryForObject(Query,int.class,userIdx);
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    public int checkNickName(String nickname) {
        String checkNickNameQuery = "select exists(select nickName from User where nickName = ?)";
        String checkNickNameParams = nickname;
        return this.jdbcTemplate.queryForObject(checkNickNameQuery,
                int.class,
                checkNickNameParams);
    }

    public int checkPhoneNumber(String phonenumber) {
        String checkPhoneNumberQuery = "select exists(select phoneNumber from User where phoneNumber = ?)";
        String checkPhoneNumberParams = phonenumber;
        return this.jdbcTemplate.queryForObject(checkPhoneNumberQuery,
                int.class,
                checkPhoneNumberParams);
    }
    public String checkStatus(String email){
        String checkStatusQuery = "SELECT status FROM User WHERE email=?";
        String checkStatusParams = email;
        return this.jdbcTemplate.queryForObject(checkStatusQuery,String.class,checkStatusParams);
    }

    // 로그인: 해당 email에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userIdx, password,email from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("email"),
                        rs.getString("password")
                ),
                getPwdParams
        );
    }

    // 프로필: 해당 User의 프로필 페이지를 조회한다.
    public GetProfileRes getProfile(int userIdx){
        String getProfileQuery= "SELECT * " +
                "FROM(" +
                "SELECT u.userIdx, u.email, u.nickName " +
                ",(SELECT COUNT(s.storeIdx) FROM Store s WHERE u.userIdx = s.userIdx and s.status = 'A') as storeCnt " +
                ",(SELECT COUNT(r.reviewIdx) from Review r where u.userIdx = r.userIdx and r.status = 'A') as reviewCnt " +
                "from User u " +
                ") t " +
                "WHERE t.userIdx = ?";
        int getProfileParams = userIdx;

        return this.jdbcTemplate.queryForObject(getProfileQuery,
                (rs, rowNum) -> new GetProfileRes(
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getInt("storeCnt"),
                        rs.getInt("reviewCnt")),
                getProfileParams);
    }

    public String getPassword(int userIdx) {
        String getPasswordQuery = "SELECT password FROM User WHERE userIdx = ?";
        return this.jdbcTemplate.queryForObject(getPasswordQuery, String.class , userIdx);
    }

    // 회원탈퇴
    public int deleteAccount (int userIdx) {
        String deleteAccountQuery = "UPDATE User SET status = 'D' WHERE userIdx = ?";
        this.jdbcTemplate.update(deleteAccountQuery, userIdx);
        return userIdx;
    }

}
