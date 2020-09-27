package com.offcn.user.service.impl;

import com.offcn.user.enums.UserExceptionEnum;
import com.offcn.user.exception.UserException;
import com.offcn.user.mapper.TMemberAddressMapper;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;
import com.offcn.user.pojo.TMemberAddressExample;
import com.offcn.user.pojo.TMemberExample;
import com.offcn.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TMemberMapper memberMapper;
//实现用户注册
    @Override
    public void registerUser(TMember member) {
        //检查系统中此手机好是否存在
        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(member.getLoginacct());

        long l = memberMapper.countByExample(example);

        //第一种 手机号已经被注册
        if (l>0){
                throw new UserException(UserExceptionEnum.EMAIL_EXIST);
        }
        //手机号 未被注册  设置相关参数
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(member.getUserpswd());

        //设置密码
        member.setUserpswd(encode);
        member.setUsername(member.getLoginacct());
        member.setEmail(member.getEmail());
        //实名认证状态 0 - 未实名认证， 1 - 实名认证申请中， 2 - 已实名认证
        member.setAuthstatus("0");
        //用户类型: 0 - 个人， 1 - 企业
        member.setUsertype("0");
        //账户类型: 0 - 企业， 1 - 个体， 2 - 个人， 3 - 政府
        member.setAccttype("2");
        System.out.println("插入数据:"+member.getLoginacct());
        memberMapper.insertSelective(member);
    }

    @Override
    public TMember login(String username, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(username);

        List<TMember> list = memberMapper.selectByExample(example);
        if (list!=null&&list.size()==1){
            TMember tMember = list.get(0);
            //比对传入的值和  数据库中的密码是否一致  因为后者是加密后的
            boolean matches = encoder.matches(password, tMember.getUserpswd());

            return matches?tMember: null;
        }

        return null;


    }

    @Override
    public TMember findTmemberById(Integer id) {
            return memberMapper.selectByPrimaryKey(id);
    }
        @Autowired
        private TMemberAddressMapper memberAddressMapper;
    @Override
    public List<TMemberAddress> addressList(Integer menberId) {
        TMemberAddressExample example = new TMemberAddressExample();
        TMemberAddressExample.Criteria criteria = example.createCriteria();
        criteria.andMemberidEqualTo(menberId);
        return memberAddressMapper.selectByExample(example);

    }


}
