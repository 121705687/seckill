package org.seckill.dto;

import lombok.Data;
import org.seckill.entity.SuccessKilled;

import java.io.Serializable;

/**
 * 封装秒杀执行后的结果
 */
@Data
public class SeckillExecution implements Serializable{
    private long seckillId;
    private int state;//秒杀执行结果状态
    private String stateInfo;//状态标识
    private SuccessKilled successKilled;//秒杀成功对象

    public SeckillExecution(long seckillId, int state, String stateInfo, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = state;
        this.stateInfo = stateInfo;
        this.successKilled = successKilled;
    }

    public SeckillExecution(long seckillId, int state, String stateInfo) {
        this.seckillId = seckillId;
        this.state = state;
        this.stateInfo = stateInfo;
    }
}
