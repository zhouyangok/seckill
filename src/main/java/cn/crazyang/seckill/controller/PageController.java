package cn.crazyang.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName PageController
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */

@Controller
@RequestMapping("/page")
public class PageController {


    @RequestMapping("login")
    public String loginPage(){

        return "login";
    }
}
