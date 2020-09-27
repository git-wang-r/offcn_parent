package com.offcn.project.service;

import com.offcn.project.pojo.TProject;
import com.offcn.project.pojo.TProjectImages;
import com.offcn.project.pojo.TReturn;
import com.offcn.project.pojo.TType;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ProjectInfoService {

    //获取项目回报列表
    public List<TReturn> getProjectReturns(Integer projectId);

    //获取系统中 所有项目
    List<TProject> getAllProjects();
    //获取项目图片
    List<TProjectImages> getProjectImages(Integer id);
    //获取姓名详细信息
    TProject getProjectInfo (Integer projectId);
    //获取项目分类
    List<TType> getProjectTypes();

    //获取回报信息
    TReturn getReturnInfo(Integer returnId);
}
