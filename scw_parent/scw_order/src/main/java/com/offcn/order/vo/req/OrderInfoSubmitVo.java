package com.offcn.order.vo.req;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderInfoSubmitVo {
    private String accessToken;
    private Integer projectid;//项目ID
    private Integer returnid;//回报ID
    private Integer rtncount;//回报数量
    private String address;//收货地址
    private Byte invoice;//是否开发票 0 - 不开发票， 1 - 开发票
    private String invoictitle;//发票名头
    private String remark;//备注
}
