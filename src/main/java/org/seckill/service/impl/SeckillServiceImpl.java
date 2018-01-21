package org.seckill.service.impl;

import lombok.extern.log4j.Log4j;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Log4j
@Service
public class SeckillServiceImpl implements SeckillService{
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;

    private final String slag = "fewgerweewretetrerew";//混淆燕子,加大破解难度

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if(seckill == null){
            return new Exposer(false,seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        //秒杀还未开始，秒杀已经结束
        if(nowTime.getTime()<startTime.getTime()
                ||nowTime.getTime()>endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = this.getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }


    /**
     * 异常都给到一起，可行，但是无法区分究竟是哪种异常，所以多个catch
     * 使用注解控制事务优点：
     * 1.开发团队达成一致的约定，明确标注事务方法的编程风格。
     * 2.保证事务方法的执行时间尽可能短，尽量不要穿插其他网络操作，RPC/http，实在需要剥离到事务方法外
     * 3.不是所有的方法都需要事务。如只有1条修改操作，只读操作不需要事务。
     */
    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5==null || !md5.equals(this.getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");//秒杀数据被重写了
        }
        //执行秒杀逻辑：减库存 + 记录购买行为
        Date nowTime = new Date();
        try{//执行这个过程的任何一次都要防范
            int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
            if(updateCount<=0){//没有更新到记录
                throw new SeckillCloseException("秒杀结束");
            }else{
                //记录购买行为    ,seckillId,userPhone唯一
                int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
                if(insertCount<=0){//重复秒杀
                    throw new RepeatKillException("=重复秒杀");
                }else{//秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS.getState(),SeckillStatEnum.SUCCESS.getStateInfo(),successKilled);//枚举表示常量更好
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){//把所有的检查异常转化为运行期异常,spring 的声明式事务，能感受到运行期异常。进行回滚
            log.error(e.getMessage(),e);
            throw new SeckillException("异常"+e.getMessage());
        }
    }

    private String getMD5(long seckillId){
        String base = seckillId+"/"+slag;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());//是spring的工具类
        return md5;
    }
}
