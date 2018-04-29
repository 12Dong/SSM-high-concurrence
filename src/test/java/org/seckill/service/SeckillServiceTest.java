package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());


    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList()  {
        List<Seckill> list =  seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }


    @Test
    public void exportSeckillUrl() {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("list={}",exposer);
        //14:29:40.637 [main] INFO
        // o.seckill.service.SeckillServiceTest -
        // list=Exposer{exposed=true, md5='a45972f2ba146a0c9766b8736617df2d',
        // seckillId=1000, now=0, start=0, end=0}


    }

    @Test
    public void executeSeckill() {
        long id = 1000;
        long phone = 12345678902L;
        String md5 = "a45972f2ba146a0c9766b8736617df2d";
        SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
        logger.info("result={}",seckillExecution);
    }

//    14:38:54.143 [main] INFO  o.seckill.service.SeckillServiceTest - result=SeckillExecution
// {seckillId=1000, state=1, stateInfo='秒杀成功',
// successKilled=SuccessKilled
// {seckillId=1000, userPhone=12345678902, state=0, createTime=Sun Apr 29 14:38:54 CST 2018}}
}