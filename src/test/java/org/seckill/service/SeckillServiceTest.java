package org.seckill.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

//集成测试，service和dao层一起，需要的配置文件都要进来
@Log4j
//@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        log.info(JSON.toJSONString(seckillList));//暂未整合logback
    }

    @Test
    public void getById() throws Exception {
        Seckill serviceById = seckillService.getById(1000L);
        System.out.println(JSON.toJSONString(serviceById));
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id =1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        System.out.println(JSON.toJSONString(exposer));
    }

    @Test
    public void executeSeckill() throws Exception {
        long id=1000;
        long phone = 13512341234L;
        String md5 = "3707b652d3deb5dc8899bfae1ca36c51";
        SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
        System.out.println(JSON.toJSONString(seckillExecution));
    }

}