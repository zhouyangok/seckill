package cn.crazyang.seckill.service.ipml;

import cn.crazyang.seckill.entity.User;
import cn.crazyang.seckill.dao.UserMapper;
import cn.crazyang.seckill.param.LoginParam;
import cn.crazyang.seckill.result.CodeMsg;
import cn.crazyang.seckill.result.Result;
import cn.crazyang.seckill.service.UserService;
import cn.crazyang.seckill.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author zhouyang
 * @Date 2019/3/15 上午10:26.
 */
@Service("userService")
public class UserServiceImpl implements UserService{

    @Autowired
    UserMapper userMapper;
    @Override
    public Result<User> login(LoginParam loginParam) {

        User user = userMapper.checkPhone(loginParam.getMobile());
        if(user == null){
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPwd= user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(loginParam.getPassword(), saltDB);
        if(!StringUtils.equals(dbPwd , calcPass)){
            return Result.error(CodeMsg.PASSWORD_ERROR);
        }
        user.setPassword(StringUtils.EMPTY);
        return Result.success(user);
    }
}
