package com.offcn.project.vo.resp;

import lombok.Data;

@Data
public class ProjectVo {
    // 会员id
    private Integer memberid;
    //项目id
    private Integer id;
    // 项目名称
    private String name;
    // 项目简介
    private String remark;
    // 项目头部图片
    private String headerImage;
}
