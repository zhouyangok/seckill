package cn.crazyang.seckill.controller;

import cn.crazyang.seckill.bo.GoodsBo;
import cn.crazyang.seckill.basepag.common.Const;
import cn.crazyang.seckill.entity.Goods;
import cn.crazyang.seckill.entity.User;
import cn.crazyang.seckill.basepag.redis.GoodsKey;
import cn.crazyang.seckill.basepag.redis.RedisService;
import cn.crazyang.seckill.basepag.redis.UserKey;
import cn.crazyang.seckill.basepag.result.CodeMsg;
import cn.crazyang.seckill.basepag.result.Result;
import cn.crazyang.seckill.param.GoodsParam;
import cn.crazyang.seckill.service.GoodsService;
import cn.crazyang.seckill.service.SeckillGoodsService;
import cn.crazyang.seckill.basepag.util.CookieUtil;
import cn.crazyang.seckill.vo.GoodsDetailVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName GoodsController
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    RedisService redisService;
    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    private GoodsService goodsService;


    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;


    @ApiOperation(value = "查看商品详情", response = List.class)
    @RequestMapping("/list")
    @ResponseBody
    public String list(Model model, HttpServletRequest request, HttpServletResponse response) {
        //修改前
       /* List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
         model.addAttribute("goodsList", goodsList);
    	 return "goods_list";*/
        //修改后
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
        model.addAttribute("goodsList", goodsList);
        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html, Const.RedisCacheExtime.GOODS_LIST);
        }
        return html;
    }

    @RequestMapping("/to_detail2/{goodsId}")
    @ResponseBody
    public String detail2(Model model,
                          @PathVariable("goodsId") long goodsId, HttpServletRequest request, HttpServletResponse response) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        model.addAttribute("user", user);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        if (goods == null) {
            return "没有找到该页面";
        } else {
            model.addAttribute("goods", goods);
            long startAt = goods.getStartDate().getTime();
            long endAt = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();

            int miaoshaStatus = 0;
            int remainSeconds = 0;
            if (now < startAt) {//秒杀还没开始，倒计时
                miaoshaStatus = 0;
                remainSeconds = (int) ((startAt - now) / 1000);
            } else if (now > endAt) {//秒杀已经结束
                miaoshaStatus = 2;
                remainSeconds = -1;
            } else {//秒杀进行中
                miaoshaStatus = 1;
                remainSeconds = 0;
            }
            model.addAttribute("seckillStatus", miaoshaStatus);
            model.addAttribute("remainSeconds", remainSeconds);
            SpringWebContext ctx = new SpringWebContext(request, response,
                    request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
            html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
            if (!StringUtils.isEmpty(html)) {
                redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html, Const.RedisCacheExtime.GOODS_INFO);
            }
            return html;
        }
    }

    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(Model model,
                                        @PathVariable("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);

        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        if (goods == null) {
            return Result.error(CodeMsg.NO_GOODS);
        } else {
            model.addAttribute("goods", goods);
            long startAt = goods.getStartDate().getTime();
            long endAt = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();

            int miaoshaStatus = 0;
            int remainSeconds = 0;
            if (now < startAt) {//秒杀还没开始，倒计时
                miaoshaStatus = 0;
                remainSeconds = (int) ((startAt - now) / 1000);
            } else if (now > endAt) {//秒杀已经结束
                miaoshaStatus = 2;
                remainSeconds = -1;
            } else {//秒杀进行中
                miaoshaStatus = 1;
                remainSeconds = 0;
            }
            GoodsDetailVo vo = new GoodsDetailVo();
            vo.setGoods(goods);
            vo.setUser(user);
            vo.setRemainSeconds(remainSeconds);
            vo.setMiaoshaStatus(miaoshaStatus);
            return Result.success(vo);
        }
    }


    /**
     * 新增商品，新增成功，删除缓存
     * @param request
     * @param goods
     * @return
     */
    @ApiOperation(value = "新增商品", response = Long.class)
    @PostMapping("/insertGoods")
    @ResponseBody
    public Result insertGoods(HttpServletRequest request, Goods goods) {
        long result = goodsService.insertGoods(goods);
        if(result>0){
            redisService.del(GoodsKey.getGoodsList, "");
        }
        return Result.success(result);

    }

    /**
     * 更新商品，更新成功，删除缓存
     * @param request
     * @param goods
     * @return
     */
    @ApiOperation(value = "更新商品", response = Long.class)
    @PostMapping("/updateGoods")
    @ResponseBody
    public Result updateGoods(HttpServletRequest request, Goods goods) {
        long result = goodsService.updateGoods(goods);
        if(result>0){
            redisService.del(GoodsKey.getGoodsList, "");
        }
        return Result.success(result);
    }

    /**
     * 删除商品，删除成功，删除缓存
     * @param request
     * @param goodsId
     * @return
     */
    @ApiOperation(value = "删除商品", response = Long.class)
    @GetMapping("/deleteGoodsById/{goodsId}")
    @ResponseBody
    public Result deleteGoodsById(HttpServletRequest request, @PathVariable long goodsId) {
        long result = goodsService.deleteGoods(goodsId);
        if(result>0){
            redisService.del(GoodsKey.getGoodsList, "");
        }
        return Result.success(result);
    }
}

