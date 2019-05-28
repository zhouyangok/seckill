package cn.crazyang.seckill.dao;

import cn.crazyang.seckill.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * My Blog : www.crazyang.cn
 * github: https://github.com/hfbin
 * Created by: crazyang
 * Date: 2019/5/10
 * Time: 11:29
 * Such description:
 */
public interface UserMapper {

    User selectByPhoneAndPassword(@Param("phone") String phone , @Param("password") String password);

    User checkPhone(@Param("phone") String phone );
}
