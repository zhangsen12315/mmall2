package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse register(User user);

    ServerResponse checkValid(String str, String type);

    ServerResponse forGetgetQuestion(String username);

    ServerResponse checkAnswer(String username, String question, String answer);

    ServerResponse forgetRestPassword(String username, String passwordNew, String forgetToken);

    ServerResponse resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse updateInformation(User user);

    ServerResponse<User> findUserInfoById(Integer id);

    ServerResponse checkRoleIsAdmin(User user);
}
