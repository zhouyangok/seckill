package cn.crazyang.seckill.controller;

import cn.crazyang.seckill.annotations.AccessLimit;
import cn.crazyang.seckill.bo.GoodsBo;
import cn.crazyang.seckill.common.Const;
import cn.crazyang.seckill.entity.OrderInfo;
import cn.crazyang.seckill.entity.SeckillOrder;
import cn.crazyang.seckill.entity.User;
import cn.crazyang.seckill.mq.MQSender;
import cn.crazyang.seckill.mq.SeckillMessage;
import cn.crazyang.seckill.redis.GoodsKey;
import cn.crazyang.seckill.redis.RedisService;
import cn.crazyang.seckill.redis.UserKey;
import cn.crazyang.seckill.result.CodeMsg;
import cn.crazyang.seckill.result.Result;
import cn.crazyang.seckill.service.SeckillGoodsService;
import cn.crazyang.seckill.service.SeckillOrderService;
import cn.crazyang.seckill.util.CookieUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName SeckillController
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
@Controller
@RequestMapping("seckill")
public class SeckillController implements InitializingBean {


    @Autowired
    RedisService redisService;

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    SeckillOrderService seckillOrderService;

    @Autowired
    MQSender mqSender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 系统初始化
     */
    public void afterPropertiesSet() throws Exception {
        List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
        if (goodsList == null) {
            return;
        }
        for (GoodsBo goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), goods.getStockCount(), Const.RedisCacheExtime.GOODS_LIST);
            localOverMap.put(goods.getId(), false);
        }
    }

    @RequestMapping("/seckill2")
    public String list2(Model model,
                        @RequestParam("goodsId") long goodsId, HttpServletRequest request) {

        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }
        //判断库存
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillOrderService.insert(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }

    @RequestMapping(value = "/{path}/seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model,
                                @RequestParam("goodsId") long goodsId,
                                @PathVariable("path") String path,
                                HttpServletRequest request) {

        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        //验证path
        boolean check = seckillOrderService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }/**/
        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);//10
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(mm);
        return Result.success(0);//排队中
        /*//判断库存
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        if(goods == null) {
            return Result.error(CodeMsg.NO_GOODS);
        }
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillOrderService.insert(user, goods);
        return Result.success(orderInfo);*/
    }

    /**
     * 客户端轮询查询是否下单成功
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(@RequestParam("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        long result = seckillOrderService.getSeckillResult((long) user.getId(), goodsId);
        return Result.success(result);
    }
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, User user,
                                         @RequestParam("goodsId") long goodsId) {
        String loginToken = CookieUtil.readLoginToken(request);
        user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        String path = seckillOrderService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }
}
