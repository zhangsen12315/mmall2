package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int count = userMapper.findUserByUsername(username);
        if (count == 0){
           return ServerResponse.createByErrorMessage("用户不存在");
        }

        User user = userMapper.loginMethod(username, password);
        if (user == null){
             return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse register(User user) {
        ServerResponse validResponsse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponsse.isSuccess()){
            return validResponsse;
        }
        validResponsse = this.checkValid(user.getEmail(), Const.EMAIL);

        if (!validResponsse.isSuccess()){
            return validResponsse;
        }

        user.setRole(Const.Role.RoleGeneral);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int insert = userMapper.insert(user);
        if (insert == 0){
            return ServerResponse.createByErrorMessage("注册失败!");
        }
        return ServerResponse.createBySuccess("注册成功");
    }

    @Override
    public ServerResponse checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)){
            if (Const.USERNAME.equals(type)){
                int nameCount = userMapper.findUserByUsername(str);
                if (nameCount>0){
                    return ServerResponse.createByErrorMessage("此用户已经存在!");
                }
            }
            if (type.equals(Const.EMAIL)){
                int count = userMapper.findUserByEmail(str);
                if (count>0){
                    return ServerResponse.createByErrorMessage("此邮箱已被注册!");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse forGetgetQuestion(String username) {
        ServerResponse validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("没有此用户!");
        }
        String question = userMapper.selectQuestionByUsername(username);

        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题为空!");
    }

    @Override
    public ServerResponse checkAnswer(String username, String question, String answer) {
        int count = userMapper.checkAnswer(username, question, answer);
        if (count > 0){
            //说明问题答案是正确的
            String forToken = UUID.randomUUID().toString();
            TokenCache.setKey("token"+username,forToken);
            return ServerResponse.createBySuccess(forToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误!");
    }

    @Override
    public ServerResponse forgetRestPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServerResponse serverResponse = checkValid(username, Const.USERNAME);
        if (serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在啊");
        }
        String token = TokenCache.getKey("token"+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        if (StringUtils.equals(token,forgetToken)){

            int count = userMapper.updatePasswordByUsername(username, passwordNew);
            if (count > 0){
                return ServerResponse.createBySuccess("更新成功");
            }else {
                return ServerResponse.createByErrorMessage("用户名不匹配");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误,请重新获取");
        }
    }

    @Override
    public ServerResponse resetPassword(String passwordOld, String passwordNew, User user) {
        int id =  user.getId();
        int count = userMapper.checkPassword(passwordOld, id);
        if (count == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(passwordNew);
        user.setId(id);

        int updateCount = userMapper.updatePasswordByUsername(user.getUsername(),passwordNew);
        if (updateCount > 0){
            return ServerResponse.createBySuccess("密码更新成功!");
        }
        return ServerResponse.createByErrorMessage("密码更新失败!");
    }

    @Override
    public ServerResponse updateInformation(User user) {
        int count = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (count > 0){
            return ServerResponse.createByErrorMessage("email已经存在,请更换email重新尝试");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> findUserInfoById(Integer id) {
        User userInfo = userMapper.findUserInfoById(id);
        if (userInfo==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        userInfo.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(userInfo);
    }

    @Override
    public ServerResponse checkRoleIsAdmin(User user) {
        if (user.getRole().equals(Const.Role.RoleAdmin)){
             return ServerResponse.createBySuccess("是管理员");
        }
        return ServerResponse.createByErrorMessage("不是管理员");
    }

}
