package com.offcn.user.service;

import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;

import java.util.List;

public interface UserService {

//传入一个用户对象  用户进行注册
    public void registerUser(TMember member);

    /*用户登陆的功能
    * */
    public TMember login(String username,String password);

    //根据用户id获取用户信息8
    public TMember findTmemberById(Integer id);

    //获取用户收获地址
    List<TMemberAddress> addressList(Integer menberId);
}
