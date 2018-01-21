package org.seckill.dao;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath:spring/spring-dao.xml")
public class SeckillDaoTest {
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() throws Exception {
        long id=1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(JSON.toJSONString(seckill));
    }

    @Test
    public void queryAll() throws Exception {
        List<Seckill> seckillList = seckillDao.queryAll(0, 100);
        System.out.println(JSON.toJSONString(seckillList));
    }

    @Test
    public void reduceNumber() throws Exception {
        Date keillTime = new Date();
        int i = seckillDao.reduceNumber(1000L, keillTime);
        System.out.println(i);
    }

}