package org.seckill.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 暴露秒杀地址dto
 */
@Data
public class Exposer implements Serializable{
    private boolean exposed;//是否开启秒杀
    private String md5;
    private long seckillId;
    private long now;//系统当前时间
    private long start;//开启时间
    private long end;

    //做几个不同的构造方法主要为了对象的初始化
    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed, long seckillId,long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }
}
