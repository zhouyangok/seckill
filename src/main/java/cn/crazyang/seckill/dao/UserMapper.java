package cn.crazyang.seckill.dao;

import cn.crazyang.seckill.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * My Blog : www.crazyang.cn
 * github: https://github.com/hfbin
 * Created by: HuangFuBin
 * Date: 2018/7/10
 * Time: 11:29
 * Such description:
 */
public interface UserMapper {

    User selectByPhoneAndPassword(@Param("phone") String phone , @Param("password") String password);

    User checkPhone(@Param("phone") String phone );
}
