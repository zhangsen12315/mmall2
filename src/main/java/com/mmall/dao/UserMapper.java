package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User loginMethod(@Param("username")String username, @Param("password") String password);

    int findUserByEmail(String email);

    int findUserByUsername(String username);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username,
                    @Param("question") String question,
                    @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username,
                                 @Param("passwordNew") String passwordNew);

    int checkPassword(@Param("passwordOld")String passwordOld,@Param("id") int id);

    int checkEmailByUserId(@Param("email")String email,
                           @Param("id")int id);

    User findUserInfoById(Integer id);
}