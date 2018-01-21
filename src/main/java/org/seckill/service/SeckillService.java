package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**业务接口：站在使用者的角度设计接口，不是站在实现上，否则接口会冗余
 * 三个方面：方法定义粒度（秒杀业务，不是怎么减库存具体细节,不要太繁琐也不要太抽象），
 * 参数（约简练越直接越好），
 * 返回类型（返回类型要友好/异常）
 */
public interface SeckillService {

    List<Seckill> getSeckillList();
    Seckill getById(long seckillId);//查询单个秒杀记录

    /**
     * 秒杀开启时，输出秒杀接口地址，否则输出系统时间
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * md5 是看数据串改了
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException,RepeatKillException,SeckillCloseException;
}
