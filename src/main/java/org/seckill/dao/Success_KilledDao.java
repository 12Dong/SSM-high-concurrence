package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface Success_KilledDao {
    //插入秒杀成功明细 可过滤重复
    //返回不为0 插入失败
    int insertSuccess_killed(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    //根据Id 查询successKilled并携带Seckilled信息
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
