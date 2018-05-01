package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.Success_KilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.seckillStatEnum.seckillStatEnum;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;


@Service
public class SeckillServiceImpl implements SeckillService{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
//    注入service依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private  RedisDao redisDao;

    @Autowired
    private Success_KilledDao success_killedDao;
    //用于 混淆md5
    private final String slat="yuefuzhayuehao";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
//        redis优化 缓存优化
        /*

        get from cache
            if null
                get db
                put catch


         */

        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill==null){
            seckill = seckillDao.queryById(seckillId);
            if(seckill==null){
                return new Exposer(false,seckillId);
            }
            redisDao.putSeckill(seckill);
        }

        Date start_time = seckill.getStartTime();
        Date end_time = seckill.getEndTime();
        Date now = new Date();
        if(now.getTime()<start_time.getTime()||now.getTime()>end_time.getTime()){
            return new Exposer(false,seckillId,now.getTime(),start_time.getTime(),end_time.getTime());
        }
        //转化特定字符串编码 不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
/*
    使用注解控制事务优点
    1.开发团队达成一致约定 明确标注事务方法的 编程风格
    2.保证事务方法的执行时间尽可能短 不要穿插其他网络操作 RPC/HTTP请求 或者 还是需要 剥离到事务操作外
    做一个上级操作 把需求 封装到上级操作中
    如果没有抛掷异常则 会 commit
    若果 抛掷 运行期异常则会 rollback
    3.不是所有方法都需要事务 只有一条修改操作 或者 只读  不需要事务控制

  */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException {
        if(md5==null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill rewrite");
        }
        Date now = new Date();
        //改变 操作顺序 较少行级锁 占用时间
       try{

           int insert  = success_killedDao.insertSuccess_killed(seckillId,userPhone);
           if(insert<=0){
//                冲突 重复秒杀
               throw new RepeatKillException("seckill repeated");
           }else{
               //热点商品竞争
               int update = seckillDao.reduceNumber(seckillId,now);
               if(update<=0){
                   throw new SeckillCloseException("seckill is closed");
               }else {
                   //减库存成功
                   SuccessKilled successKilled = success_killedDao.queryByIdWithSeckill(seckillId, userPhone);
                   return new SeckillExecution(seckillId, seckillStatEnum.SUCCESS, successKilled);
               }
           }


       }catch(SeckillCloseException e1){
           throw e1;

       }catch(RepeatKillException e2){
          throw e2;
       }catch(Exception e){
           logger.error(e.getMessage(),e);
//           把编译器异常 变为 运行期异常
           throw new SeckillException("seckill inner error"+e.getMessage());
       }
    }
}
