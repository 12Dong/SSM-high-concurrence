package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class Success_KilledDaoTest {

    @Resource
    Success_KilledDao success_killedDao;

    @Test
    public void insertSuccess_killed() {
        long phone = 12345678901L;
        int insertCount = success_killedDao.insertSuccess_killed(1000L,phone);
        System.out.println(insertCount);
    }

    @Test
    public void queryByIdWithSeckill() {
        SuccessKilled successKilled = success_killedDao.queryByIdWithSeckill(1000L,12345678901L);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}