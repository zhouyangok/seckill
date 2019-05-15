package cn.crazyang.seckill.vo;

import cn.crazyang.seckill.bo.GoodsBo;
import cn.crazyang.seckill.entity.OrderInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName SeckillOrderService
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
@Setter
@Getter
public class OrderDetailVo {
    private GoodsBo goods;
    private OrderInfo order;
}
