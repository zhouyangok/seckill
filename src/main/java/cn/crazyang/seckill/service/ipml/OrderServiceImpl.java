package cn.crazyang.seckill.service.ipml;

import cn.crazyang.seckill.dao.OrdeInfoMapper;
import cn.crazyang.seckill.entity.OrderInfo;
import cn.crazyang.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName OrderServiceImpl
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrdeInfoMapper ordeInfoMapper;

    @Override
    public long addOrder(OrderInfo orderInfo) {
        return ordeInfoMapper.insertSelective(orderInfo);
    }

    @Override
    public OrderInfo getOrderInfo(long orderId) {
        return ordeInfoMapper.selectByPrimaryKey(orderId);
    }
}
