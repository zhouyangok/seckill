package cn.crazyang.seckill.basepag.common;

/**
 * @ClassName Const
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午11:26.
 */
public class Const {

    public interface RedisCacheExtime{
        int REDIS_SESSION_EXTIME = 60 * 30;//30分钟
        int GOODS_LIST = 60 * 30 * 24;//1分钟
        int GOODS_ID = 60;//1分钟
        int SECKILL_PATH = 60;//1分钟
        int GOODS_INFO = 60;//1分钟
    }
}
