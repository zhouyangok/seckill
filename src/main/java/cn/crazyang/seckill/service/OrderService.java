package cn.crazyang.seckill.service;

import cn.crazyang.seckill.entity.OrderInfo;

/**
 * @ClassName OrderService
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
public interface OrderService {

    long addOrder(OrderInfo orderInfo);

    OrderInfo getOrderInfo(long rderId);
}
