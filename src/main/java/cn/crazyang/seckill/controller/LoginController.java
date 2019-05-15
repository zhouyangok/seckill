package cn.crazyang.seckill.controller;

import cn.crazyang.seckill.common.Const;
import cn.crazyang.seckill.entity.User;
import cn.crazyang.seckill.param.LoginParam;
import cn.crazyang.seckill.redis.RedisService;
import cn.crazyang.seckill.redis.UserKey;
import cn.crazyang.seckill.result.Result;
import cn.crazyang.seckill.service.UserService;
import cn.crazyang.seckill.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @ClassName LoginController
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @RequestMapping("/login")
    @ResponseBody
    public Result<User> doLogin(HttpServletResponse response, HttpSession session , @Valid LoginParam loginParam) {
        Result<User> login = userService.login(loginParam);
        if (login.isSuccess()){
            CookieUtil.writeLoginToken(response,session.getId());
            redisService.set(UserKey.getByName , session.getId() ,login.getData(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME );
        }
        return login;
    }

    @RequestMapping("/logout")
    public String doLogout(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request , response);
        redisService.del(UserKey.getByName , token);
        return "login";
    }
}
