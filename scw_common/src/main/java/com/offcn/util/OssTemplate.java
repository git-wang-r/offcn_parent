package com.offcn.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Data
@ToString
public class OssTemplate {
    private String endpoint;
    private String bucketDomain;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public String upload(InputStream inputStream, String fileName) throws Exception {
        //1、加工文件夹和文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folderName = sdf.format(new Date());


        fileName = UUID.randomUUID().toString().replace("-","")+"_"+fileName;
        //2、创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        //3、// 上传文件流，指定bucket的名称
        ossClient.putObject(bucketName,"pic/"+folderName+"/"+fileName,inputStream);

        //4、关闭资源
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ossClient.shutdown();
        String url= "https://"+bucketDomain+"/pic/"+folderName+"/"+fileName;
        System.out.println("上传文件访问路径:"+url);
        return url;
    }


}





