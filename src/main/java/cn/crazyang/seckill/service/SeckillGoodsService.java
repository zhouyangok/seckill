package cn.crazyang.seckill.service;

import cn.crazyang.seckill.bo.GoodsBo;

import java.util.List;

/**
 * @ClassName SeckillGoodsService
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
public interface SeckillGoodsService {

    List<GoodsBo> getSeckillGoodsList();

    GoodsBo getseckillGoodsBoByGoodsId(long goodsId);

    int reduceStock(long goodsId);
}
