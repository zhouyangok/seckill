package cn.crazyang.seckill.controller;

import cn.crazyang.seckill.bo.GoodsBo;
import cn.crazyang.seckill.entity.OrderInfo;
import cn.crazyang.seckill.entity.User;
import cn.crazyang.seckill.basepag.redis.RedisService;
import cn.crazyang.seckill.basepag.redis.UserKey;
import cn.crazyang.seckill.basepag.result.CodeMsg;
import cn.crazyang.seckill.basepag.result.Result;
import cn.crazyang.seckill.service.SeckillGoodsService;
import cn.crazyang.seckill.service.SeckillOrderService;
import cn.crazyang.seckill.basepag.util.CookieUtil;
import cn.crazyang.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName SeckillOrderController
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
@Controller
@RequestMapping("/order")
public class SeckillOrderController {
    @Autowired
    RedisService redisService;
    @Autowired
    SeckillOrderService seckillOrderService;
    @Autowired
    SeckillGoodsService seckillGoodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model,
                                      @RequestParam("orderId") long orderId , HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if(user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        OrderInfo order = seckillOrderService.getOrderInfo(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
}
