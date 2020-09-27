package com.offcn.user.component;


import com.offcn.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Component
@Slf4j
public class SmsTemplate {
    @Value("${sms.host}")
    private String host;
    @Value("${sms.path}")
    private String path;
    @Value("${sms.methos:POST}")
    private String method;
    @Value("${sms.appcode}")
    private String appcode;

    public String sendCode(Map<String, String> querys) {
        HttpResponse response = null;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);

        Map<String, String> bodys = new HashMap<String, String>();
        try {
            if (method.equalsIgnoreCase("get")) {
                response = HttpUtils.doGet(host, path, method, headers, querys);
            } else {
                response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            }
            String string = EntityUtils.toString(response.getEntity());
            log.info("短信发送完成；响应数据是：{}", string);
            return string;
        } catch (Exception e) {
            log.error("短信发送失败；发送参数是：{}", querys);
            return "fail";
        }
    }
    /*@Value("${AppCode}")
    private String appcode;

    @Value("${tpl}")
    private String tpl;

    private String host="http://dingxin.market.alicloudapi.com";
//传入一个手机号和   需要输入的内容
    public HttpResponse sendSms(String mobile,String param) throws Exception {
        *//*String host = "http://dingxin.market.alicloudapi.com";*//*
        String path = "/dx/sendSms";
        String method = "POST";
        *//* String appcode = "c2cf98770e0c4332b5cc4b6745aaff8d"; // 输入自己的appcode*//*
        Map<String, String> headers = new HashMap<String, String>();
// 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile); // 输入正确的手机号码
        querys.put("param", "code:"+param);
        querys.put("tpl_id", tpl);
        Map<String, String> bodys = new HashMap<String, String>();


*//**
 * 重要提示如下: HttpUtils请从
 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
 * 下载
 *
 * 相应的依赖请参照
 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
 *//*
        HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        System.out.println(response.toString());
        return response;
// 获取response的body
// System.out.println(EntityUtils.toString(response.getEntity()));
//{"return_code":"00000","order_id":"ALY1549881237643456814"}

     }*/
    }











