package org.seckill.dao;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {
    @Autowired
    SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        long id =1000L;
        long phone = 13812341234L;
        int i = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println(i);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long id =1000L;
        long phone = 13812341234L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
        System.out.println(JSON.toJSONString(successKilled));
    }

}