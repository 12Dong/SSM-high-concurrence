package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

public interface SeckillDao {
    //减库存
    //如果影响 行数 为 1 表示更新 的记录行数
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);
    //根据id查询秒杀对象
    Seckill queryById(long seckillId);
    //根据偏移量查询秒杀对象
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
