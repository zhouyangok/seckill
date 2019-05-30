package cn.crazyang.seckill.service.ipml;

import cn.crazyang.seckill.dao.GoodsMapper;
import cn.crazyang.seckill.entity.Goods;
import cn.crazyang.seckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/5/30 下午3:06.
 */

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public long insertGoods(Goods goods) {
        return goodsMapper.insertSelective(goods);
    }

    @Override
    public long updateGoods(Goods goods) {
        return goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Override
    public long deleteGoods(long goodsId) {
        return goodsMapper.deleteByPrimaryKey(goodsId);
    }
}
