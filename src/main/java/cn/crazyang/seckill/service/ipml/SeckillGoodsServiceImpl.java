package cn.crazyang.seckill.service.ipml;

import cn.crazyang.seckill.bo.GoodsBo;
import cn.crazyang.seckill.dao.GoodsMapper;
import cn.crazyang.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SeckillGoodsServiceImpl
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
@Service("seckillGoodsService")
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<GoodsBo> getSeckillGoodsList() {
        return goodsMapper.selectAllGoodes();
    }

    @Override
    public GoodsBo getseckillGoodsBoByGoodsId(long goodsId) {
        return goodsMapper.getseckillGoodsBoByGoodsId(goodsId);
    }

    @Override
    public int reduceStock(long goodsId) {
        return goodsMapper.updateStock(goodsId);
    }
}
