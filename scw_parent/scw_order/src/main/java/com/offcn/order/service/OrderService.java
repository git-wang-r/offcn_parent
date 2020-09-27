package com.offcn.order.service;

import com.offcn.order.pojo.TOrder;
import com.offcn.order.vo.req.OrderInfoSubmitVo;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    //保存订单方法
    TOrder saveOrder(OrderInfoSubmitVo vo);
}
