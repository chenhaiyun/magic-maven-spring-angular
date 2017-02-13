package com.mavenspring.controller;

import com.mavenspring.dto.Exposer;
import com.mavenspring.dto.SeckillExecution;
import com.mavenspring.dto.SeckillResult;
import com.mavenspring.entity.Seckill;
import com.mavenspring.enums.SeckillStatEnum;
import com.mavenspring.exception.RepeatKillException;
import com.mavenspring.exception.SeckillCloseException;
import com.mavenspring.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by magic on 2017/1/21.
 */
@Controller
@RequestMapping("/seckill")  //module
public class SeckillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    /**
     * get all seckill list
     * @param model
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        //list.jsp + model = ModelAndView
        List<Seckill> seckills = seckillService.getSeckillList();
        model.addAttribute("seckills", seckills);
        return "list";  //WEB-INF/jsp/list.jsp    look at spring-web.xml
    }

    /**
     * Get All Seckill List With Json
     * @param
     * @return
     */
    @RequestMapping(value = "/listWithJson",method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<List> listWithJson(){
        //list.jsp + model = ModelAndView
        List<Seckill> seckills = seckillService.getSeckillList();
        //model.addAttribute("seckills", seckills);
        //return "list";  //WEB-INF/jsp/list.jsp    look at spring-web.xml
        return new SeckillResult<List> (true, seckills);
    }


    /**
     * get seckill by seckillId
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(null == seckill){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/detailWithJson",method = RequestMethod.GET,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult detailWithJson(@PathVariable("seckillId") Long seckillId){
//        if(seckillId == null){
//            //return "redirect:/seckill/list";
//        }
        Seckill seckill = seckillService.getById(seckillId);
//        if(null == seckill){
//            //return "forward:/seckill/list";
//        }
        return new SeckillResult<Seckill> (true, seckill);
    }

    //ajax return json
    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})//http context
    @ResponseBody  //packging return result to json
    public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
        SeckillResult<Exposer> result;
        try{
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.error(e.getMessage());
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution",method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,@PathVariable("md5") String md5,
                                                  @CookieValue(value = "killPhone",required = false) Long phone){
        if(phone == null){
            return new SeckillResult<SeckillExecution>(false,"do not login");
        }
        try{
            SeckillExecution seckillExcution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true,seckillExcution);
        }catch (RepeatKillException e){
            SeckillExecution seckillExcution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,seckillExcution);
        }catch (SeckillCloseException e){
            SeckillExecution seckillExcution = new SeckillExecution(seckillId,SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,seckillExcution);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            SeckillExecution seckillExcution = new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,seckillExcution);
        }
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Long> time(){
        Date date = new Date();
        return  new SeckillResult<Long>(true,date.getTime());
    }
}