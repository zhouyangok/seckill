package cn.crazyang.seckill.service;

import cn.crazyang.seckill.entity.Goods;

/**
 * @ClassName GoodsService
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/5/30 下午3:05.
 */

public interface GoodsService {

    long insertGoods(Goods goods);

    long updateGoods(Goods goods);

    long deleteGoods(long goodsId);
}
