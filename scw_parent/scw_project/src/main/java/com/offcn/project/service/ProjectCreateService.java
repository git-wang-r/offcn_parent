package com.offcn.project.service;

import com.offcn.enums.ProjectStatusEnume;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import com.offcn.project.vo.req.ProjectReturnVo;

public interface ProjectCreateService {
    //初始化一个项目
    public String initCreateProject(Integer memberId);
    //保存项目信息
    public void saveProjectInfo(ProjectStatusEnume auth, ProjectRedisStorageVo project);

}
