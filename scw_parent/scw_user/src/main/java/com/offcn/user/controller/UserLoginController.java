package com.offcn.user.controller;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.druid.util.StringUtils;
import com.offcn.response.AppResponse;
import com.offcn.user.component.SmsTemplate;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberExample;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.req.UserRegistVo;
import com.offcn.user.vo.resp.UserRespVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.naming.ldap.HasControls;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//发送短信验证码
@Api(tags = "用户登陆/注模块")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @ApiOperation("获取注册的验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="phoneNo",value = "手机号",required = true)
    })
    @PostMapping("/sendCode")
    /*f4d4*/
    //返回类型在comment这个模块里
    public AppResponse<Object> sendCode(String phone){
//生成短信验证码
        String code = UUID.randomUUID().toString().substring(0, 4);
        //保存验证码到服务器中
        redisTemplate.opsForValue().set(phone,code,10000,TimeUnit.HOURS);
        System.out.println(code);
        //短信发送构造参数
        HashMap<String,String> querys = new HashMap();
        querys.put("mobile",phone);
        querys.put("param","code"+code);
        querys.put("tpl_id","TP1711063");//短信模板
        //发送短信
        String sendCode = smsTemplate.sendCode(querys);
        if (sendCode.equals("")||sendCode.equals("fail")){
            //短信失败
            return AppResponse.fail("短信发送失败");
        }
        return AppResponse.ok(sendCode);
    }

    @ApiOperation("用户注册")
    @PostMapping("/regist")

    public AppResponse regist(UserRegistVo registVo){
//首先得校验验证码
        String code = redisTemplate.opsForValue().get(registVo.getLoginacct());
        if (!StringUtils.isEmpty(code)) {
//redis里有验证码  查看是否相等
            boolean b = code.equals(registVo.getCode());
            if (b) {
                //相等的情况
                try {
                    TMember member = new TMember();
                    //将registvo的信息移入  member
                    BeanUtils.copyProperties(registVo, member);
                    //将用户信息注册到数据库
                    userService.registerUser(member);
                    log.debug("用户信息注册成功", member.getLoginacct());
                    //注册成功之后
                    return AppResponse.ok("注册成功");
                } catch (BeansException e) {
                    log.error("用户注册失败" + registVo.getLoginacct());
                    return  AppResponse.fail(e.getMessage());
                }
            }else {
                return AppResponse.fail("验证码相同");
            }

        }else {
            return AppResponse.fail("验证码过期");
        }
    }
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", value = "密码", required = true)
    })//@ApiImplicitParams：描述所有参数；@ApiImplicitParam描述某个参数
    @PostMapping("login")
    public AppResponse<UserRespVo> login(String username,String password){
        //尝试登陆
        TMember member = userService.login(username, password);
        if (member==null){
            AppResponse<UserRespVo> fail = AppResponse.fail(null);
            fail.setMsg("用户名密码错误");
            return fail;
        }

        //登陆成功 生成令牌
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        UserRespVo vo = new UserRespVo();
        BeanUtils.copyProperties(member,vo);
        vo.setAccessToken(token);
        //根据令牌查询用户的id信息
        redisTemplate.opsForValue().set(token,member.getId()+"",2, TimeUnit.HOURS);
        return  AppResponse.ok(vo);
    }

    @GetMapping("/findUser/{id}")
    public AppResponse<UserRespVo> findUser(@PathVariable("id") Integer id){
        TMember tmember = userService.findTmemberById(id);

        UserRespVo userRespVo = new UserRespVo();
        BeanUtils.copyProperties(tmember,userRespVo);
        return AppResponse.ok(userRespVo);
    }

}
/*// Endpoint以杭州为例，其它Region请按实际情况填写。
String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
String accessKeyId = "<yourAccessKeyId>";
String accessKeySecret = "<yourAccessKeySecret>";
String bucketName = "<yourBucketName>";

// 创建OSSClient实例。
OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 创建存储空间。
ossClient.createBucket(bucketName);

// 关闭OSSClient。
ossClient.shutdown();            */
/*
用户名qyzc20200924wz
桶名 qyzc20200924wz
* LTAI4G9b8ovQcW7t5FJnxVgr
    obW47TPd8tvX1sCly8nOCmTElsAefF
* */