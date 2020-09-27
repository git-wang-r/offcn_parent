package com.offcn.order.service.impl;

import com.offcn.dycommon.enums.OrderStatusEnumes;
import com.offcn.order.mapper.TOrderMapper;
import com.offcn.order.pojo.TOrder;
import com.offcn.order.service.OrderService;
import com.offcn.order.service.ProjectServiceFeign;
import com.offcn.order.vo.req.OrderInfoSubmitVo;
import com.offcn.order.vo.req.TReturn;
import com.offcn.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private ProjectServiceFeign projectServiceFeign;

    @Override
    public TOrder saveOrder(OrderInfoSubmitVo vo) {
        TOrder order = new TOrder();
        //获得令牌
        String accessToken = vo.getAccessToken();
        //从缓存中得到会员id
        String memberId  = redisTemplate.opsForValue().get(accessToken);
        order.setMemberid(Integer.parseInt(memberId));
        //项目ID
        order.setProjectid(vo.getProjectid());
        //回报项目id可以有多个 默认的那个
        order.setReturnid(vo.getReturnid());
        //生成订单编号
        String orderNum = UUID.randomUUID().toString().replace("-", "");
        order.setOrdernum(orderNum);
        AppResponse<List<TReturn>> returnAppResponse = projectServiceFeign.returnInfo(vo.getProjectid());
        List<TReturn> tReturnList = returnAppResponse.getData();
        TReturn tReturn = tReturnList.get(0);
        //计算回报金额 支持数量  支持金额  运费

        Integer totalMoney = vo.getRtncount() * tReturn.getSupportmoney() + tReturn.getFreight();
        order.setMoney(totalMoney);
        //回报数量
        order.setRtncount(vo.getRtncount());
        //支付状态
        order.setStatus(OrderStatusEnumes.UNPAY.getCode()+"");
        //收货地址
        order.setAddress(vo.getAddress());
        //是否开发票
        order.setInvoice(vo.getInvoice().toString());
        //发票名头
        order.setInvoictitle(vo.getRemark());
        //被指
        order.setRemark(vo.getRemark());
        orderMapper.insertSelective(order);

        return order;


    }
}
