package com.offcn.project.service.impl;

import com.offcn.project.mapper.TProjectImagesMapper;
import com.offcn.project.mapper.TProjectMapper;
import com.offcn.project.mapper.TReturnMapper;
import com.offcn.project.mapper.TTypeMapper;
import com.offcn.project.pojo.*;
import com.offcn.project.service.ProjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectInfoServiceImpl implements ProjectInfoService{
        @Autowired
        private TReturnMapper returnMapper;
        @Autowired
        private TProjectMapper projectMapper;
        @Autowired
        private TProjectImagesMapper projectImagesMapper;

        @Autowired
        private TTypeMapper typeMapper;
    @Override
    public List<TReturn> getProjectReturns(Integer projectId) {
        //获取项目回报列表
        TReturnExample example = new TReturnExample();
        TReturnExample.Criteria criteria = example.createCriteria();
        criteria.andProjectidEqualTo(projectId);
        return returnMapper.selectByExample(example);
    }

    //获取所有项目
        @Override
        public List<TProject> getAllProjects() {
            return projectMapper.selectByExample(null);
        }
//获取项目的图片
        @Override
        public List<TProjectImages> getProjectImages(Integer id) {
            TProjectImagesExample example = new TProjectImagesExample();
            TProjectImagesExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(id);
            return projectImagesMapper.selectByExample(example);
        }
    //查出项目详细信息
    @Override
    public TProject getProjectInfo(Integer projectId) {

        return projectMapper.selectByPrimaryKey(projectId);
    }

    @Override
    public List<TType> getProjectTypes() {
        return typeMapper.selectByExample(null);
    }

    @Override
    public TReturn getReturnInfo(Integer returnId) {
        return  returnMapper.selectByPrimaryKey(returnId);
    }


}
