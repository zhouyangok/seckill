package cn.crazyang.seckill.service;

import cn.crazyang.seckill.bo.GoodsBo;
import cn.crazyang.seckill.entity.OrderInfo;
import cn.crazyang.seckill.entity.SeckillOrder;
import cn.crazyang.seckill.entity.User;

/**
 * @ClassName SeckillOrderService
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
public interface SeckillOrderService {

    SeckillOrder getSeckillOrderByUserIdGoodsId(long userId , long goodsId);


    OrderInfo insert(User user , GoodsBo goodsBo);

    OrderInfo getOrderInfo(long orderId);

    long getSeckillResult(Long userId, long goodsId);

    boolean checkPath(User user, long goodsId, String path);

    String createMiaoshaPath(User user, long goodsId);

}
