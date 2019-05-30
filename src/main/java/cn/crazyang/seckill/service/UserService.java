package cn.crazyang.seckill.service;

import cn.crazyang.seckill.entity.User;
import cn.crazyang.seckill.param.LoginParam;
import cn.crazyang.seckill.basepag.result.Result;

/**
 * @ClassName UserService
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
public interface UserService {
    Result<User> login(LoginParam loginParam);
}
