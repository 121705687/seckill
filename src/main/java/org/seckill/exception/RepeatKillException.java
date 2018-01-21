package org.seckill.exception;

/**
 * 重复秒杀异常，防止恶意重复
 * 运行期异常
 * spring事务只控制运行期异常
 */
public class RepeatKillException extends SeckillException{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
