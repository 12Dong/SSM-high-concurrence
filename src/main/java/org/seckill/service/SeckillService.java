package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillException;

import java.util.List;

//使用者的角度设计接口
//三个角度
//方法定义粒度 参数 返回类型(return 类型 / 异常)
public interface SeckillService {
    //查询所有秒杀记录
    List<Seckill> getSeckillList();
    //查询一个秒杀记录
    Seckill getById(long seckillId);
    //输出秒杀接口地址
    //否则输出系统时间和秒杀时间
    Exposer exportSeckillUrl(long seckillId);
    //执行秒杀操作
    SeckillExecution executeSeckill(long seckilled, long userPhone, String md5) throws SeckillException,RepeatKillException;


}
