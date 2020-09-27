package com.offcn.project.controller;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.offcn.enums.ProjectStatusEnume;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.pojo.TReturn;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectBaseInfoVo;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import com.offcn.project.vo.req.ProjectReturnVo;
import com.offcn.response.AppResponse;
import com.offcn.vo.BaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "项目基本功能模块")
@Slf4j
@RequestMapping("/project")
@RestController
public class ProjectCreateController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProjectCreateService projectCreateService;

    @ApiOperation("项目发起第一步阅读同意协议")
    @GetMapping("/init")
    public AppResponse<String> init(BaseVo vo){
        String accessToken = vo.getAccessToken();
        //通过登陆令牌获取项目id
        String memberId = redisTemplate.opsForValue().get(accessToken);

        if (StringUtils.isEmpty(memberId)) {
            return AppResponse.fail("无此登陆的权限");
        }
        int id = Integer.parseInt(memberId);
        //保存临时项目信息到redis
        String projectToken = projectCreateService.initCreateProject(id);
        return AppResponse.ok(projectToken);

    }

    @ApiOperation("项目发起第二步保存项目基本信息")
    @GetMapping("/savebaseInfo")
    public AppResponse<String> savebaseInfot(ProjectBaseInfoVo vo){
        //取出redis中之前存储json结构的项目信息
        String origanl = redisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + vo.getProjectToken());
        //将json结构的项目信息转换为
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(origanl, ProjectRedisStorageVo.class);
        //将页面接收的参数传递到projectRedisStorageVo
        BeanUtils.copyProperties(vo,projectRedisStorageVo);

        //将这个vo对象转换为json字符串
        String jsonString = JSON.toJSONString(projectRedisStorageVo);

        //重新更新到redis
        redisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+vo.getProjectToken(),jsonString);
        return AppResponse.ok("ok");
    }
    @ApiOperation("项目发起第3步-项目保存项目回报信息")
    @PostMapping("/savereturn")
    public AppResponse<Object> saveReturnInfo(@RequestBody List<ProjectReturnVo> pro) {
        //从集合中取出一个回报
        ProjectReturnVo projectReturnVo = pro.get(0);
        //取出项目的令牌
        String projectToken = projectReturnVo.getProjectToken();

        //根据项目令牌取出项目信息
        //1、取得redis中之前存储JSON结构的项目信息
        String projectContext = redisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
        //2、转换为redis存储对应的vo
        ProjectRedisStorageVo storageVo = JSON.parseObject(projectContext, ProjectRedisStorageVo.class);

        //3、将页面收集来的回报数据封装重新放入redis
        List<TReturn> returns = new ArrayList<>();
        for (ProjectReturnVo projectReturnVo1 : pro) {
            TReturn tReturn = new TReturn();
            BeanUtils.copyProperties(projectReturnVo1, tReturn);
            returns.add(tReturn);
        }

        //4、更新return集合
        storageVo.setProjectReturns(returns);
        String jsonString = JSON.toJSONString(storageVo);
        //5、重新更新到redis
        redisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken, jsonString);
        return AppResponse.ok("OK");

    }


    @ApiOperation("项目发起第4步-项目保存项目回报信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken",value = "用户令牌",required = true),
            @ApiImplicitParam(name = "projectToken",value="项目标识",required = true),
            @ApiImplicitParam(name="ops",value="用户操作类型 0-保存草稿 1-提交审核",required = true)})
    @PostMapping("/submit")
    public AppResponse<Object> submit(String accessToken,String projectToken,String ops){
        //前置校验
        String memberId = redisTemplate.opsForValue().get(accessToken);
        if (StringUtils.isEmpty(memberId)){
            return AppResponse.fail("无权限，请先登陆");
        }

        //根据项目token获取项目存储在redis的信息
        String projectJsoStr = redisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
        //把json格式的项目信息 还原为项目对象
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(projectJsoStr, ProjectRedisStorageVo.class);

        //判断用户操作类型不为空 进行处理
        if (!StringUtils.isEmpty(ops)){
            if (ops.equals("1")){
                //提交项目状态  提交枚举
               ProjectStatusEnume submitAuth= ProjectStatusEnume.SUBMIT_AUTH;
               //保存项目信息
                projectCreateService.saveProjectInfo(submitAuth,projectRedisStorageVo);

                return AppResponse.ok(null);
            }else if (ops.equals("0")){
                //获取项目  草稿状态
                ProjectStatusEnume projectStatusEnume = ProjectStatusEnume.DRAFT;
                //保存草稿
                projectCreateService.saveProjectInfo(projectStatusEnume,projectRedisStorageVo);
                return AppResponse.ok(null);
            }else {
                AppResponse<Object> appResponse = AppResponse.fail(null);
                appResponse.setMsg("不支持此操作");
                return appResponse;
            }
        }
    return AppResponse.fail(null);


    }




}
