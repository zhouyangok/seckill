package cn.crazyang.seckill.vo;

import cn.crazyang.seckill.bo.GoodsBo;
import cn.crazyang.seckill.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName SeckillOrderService
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
@Getter
@Setter
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsBo goods ;
    private User user;
}
