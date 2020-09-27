package com.offcn.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.offcn.enums.ProjectStatusEnume;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.mapper.*;
import com.offcn.project.pojo.*;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import com.offcn.project.vo.req.ProjectReturnVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sun.plugin.util.UIUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Service
public class ProjectCreateServiceImpl implements ProjectCreateService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TProjectMapper projectMapper;
    @Autowired
    private TProjectImagesMapper projectImagesMapper;
    @Autowired
    private TProjectTagMapper projectTagMapper;

    @Autowired
    private TProjectTypeMapper projectTypeMapper;

    @Autowired
    private TReturnMapper tReturnMapper;
//初始化项目
    @Override
    public String initCreateProject(Integer memberId) {
        String token = UUID.randomUUID().toString().replace("_", "");

        //项目临时对象
        ProjectRedisStorageVo initVo = new ProjectRedisStorageVo();
        initVo.setMemberid(memberId);
        //init转为json字符串
        String jsonString = JSON.toJSONString(initVo);

        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + token, jsonString);

        return token;
    }
    //保存项目状态信息

    @Override                   // 状态                                  req里面的 对象
    public void saveProjectInfo(ProjectStatusEnume auth, ProjectRedisStorageVo  project) {
            //保存项目的基本信息  获取到数据库的id
        TProject projectBase = new TProject();
        //复制对象
        BeanUtils.copyProperties(project,projectBase);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        projectBase.setCreatedate(simpleDateFormat.format(new Date()));
//基本信息的插入
        projectMapper.insertSelective(projectBase);
        //获取到刚才的id
        Integer projectid = projectBase.getId();

        String headerImage = project.getHeaderImage();
        TProjectImages images = new TProjectImages(null, projectid, headerImage, ProjectImageTypeEnume.HEADER.getCode());

        //将项目上传的图片保存起来
        //保存头图
        //2保存详情图
        projectImagesMapper.insertSelective(images);
        List<String> detailsImage = project.getDetailsImage();
        for (String string : detailsImage) {
            TProjectImages img = new TProjectImages(null,projectid,string, ProjectImageTypeEnume.DETAILS.getCode());
            projectImagesMapper.insertSelective(img);
        }
        //3保存项目的标签信息
        List<Integer> tagids = project.getTagids();
        for (Integer tagid:tagids) {
            TProjectTag tProjectTag = new TProjectTag(null,projectid,tagid);
            projectTagMapper.insertSelective(tProjectTag);
        }
        //4 保存项目的分类信息
        List<Integer> typeids = project.getTypeids();
        for (Integer tid : typeids) {
            TProjectType tProjectType = new TProjectType(null,projectid,tid);
            projectTypeMapper.insertSelective(tProjectType);
        }
        //5保存回报信息
        List<TReturn> returns = project.getProjectReturns();
        //设置项目的id
        for (TReturn tReturn : returns) {
            tReturn.setProjectid(projectid);
            tReturnMapper.insertSelective(tReturn);
        }
        //删除临时数据
        stringRedisTemplate.delete(ProjectConstant.TEMP_PROJECT_PREFIX+project.getProjectToken());


    }
}
