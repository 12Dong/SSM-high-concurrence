package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.Success_KilledDao;
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
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

public class SeckillServiceImpl implements SeckillService{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SeckillDao seckillDao;
    private Success_KilledDao success_killedDao;
    //用于 混淆md5
    private final String slat="越复杂越好";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if(seckill==null){
            return new Exposer(false,seckillId);
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

    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException {
        if(md5==null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill rewrite");
        }
        Date now = new Date();
       try{
           int update = seckillDao.reduceNumber(seckillId,now);
           if(update<=0){
               throw new SeckillCloseException("seckill is closed");
           }else{
               //减库存成功

               int insert  = success_killedDao.insertSuccess_killed(seckillId,userPhone);
               if(insert<=0){
//                冲突 重复秒杀
                   throw new RepeatKillException("seckill repeated");
               }
               SuccessKilled successKilled = success_killedDao.queryByIdWithSeckill(seckillId,userPhone);
               return new SeckillExecution(seckillId, seckillStatEnum.SUCCESS,successKilled);
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
