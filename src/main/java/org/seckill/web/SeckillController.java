package org.seckill.web;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill") //模块/资源/{id}/细分/seckill/list
//@Log4j
public class SeckillController {
    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list")
    public String list(Model model){
        //获取列表页
        List<Seckill> seckillList = seckillService.getSeckillList();
        System.out.println(JSON.toJSONString(seckillList));
        model.addAttribute("list",seckillList);
        return "list";  //springmvc中配置了  /WEB-INF/jsp/list.jsp
    }

    @RequestMapping(value = "/{seckillId}/detail")
    public String detail (@PathVariable("seckillId")Long seckillId,Model model){
        if(seckillId==null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill==null){
            return "forward:/seckill/list";
        }
        seckill.setStartStr(seckill.getStartTime().getTime());
        seckill.setEndStr(seckill.getEndTime().getTime());
        model.addAttribute("seckill",seckill);
        return "detail";
    }
    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
                    produces = {"application/json;charset=UTF-8"})
    @ResponseBody   //有这个注解springmvc 视图将数据封装成json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try{
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution")
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5")String md5,
                                                   @CookieValue(value = "killPhone",required = false) Long phone){
        //可以使用 springMvc 的验证
        if(phone == null){
            return new SeckillResult<SeckillExecution>(false,"未注册");
        }
        try{
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (RepeatKillException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL.getState(),SeckillStatEnum.REPEAT_KILL.getStateInfo());
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (SeckillCloseException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.END.getState(),SeckillStatEnum.END.getStateInfo());
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (Exception e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR.getState(),SeckillStatEnum.INNER_ERROR.getStateInfo());
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }
    }

    //获取系统时间
    @RequestMapping(value = "/time/now")
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult(true,now.getTime());
    }
}
