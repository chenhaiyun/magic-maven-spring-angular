package com.mavenspring.service;

import com.mavenspring.dto.Exposer;
import com.mavenspring.dto.SeckillExecution;
import com.mavenspring.entity.Seckill;
import com.mavenspring.exception.RepeatKillException;
import com.mavenspring.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by magic on 2017/1/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> seckills = seckillService.getSeckillList();
        logger.info("1.seckills={}",seckills);
    }

    @Test
    public void testGetById() throws Exception {
        long id = 1000l;
        Seckill seckill = seckillService.getById(id);
        logger.info("2.seckill={}",seckill);
    }

    //测试秒杀逻辑
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1002l;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            String md5 = exposer.getMd5();
            long phone = 18868831752l;
            try{
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("seckillExcuation={}"+seckillExecution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }
        else{
            //seckill don't start
            logger.warn("exposer={}",exposer);
        }
    }

    @Test
    public void testExecuteSeckill() throws Exception {
        long id = 1002l;
        long phone = 18868831751l;
        try{
            String md5 = "97c1af50687c20e78ec601a1e09b2bd8";
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
            logger.info("seckillExcuation={}"+seckillExecution);
        }catch (RepeatKillException e){
            logger.error(e.getMessage());
        }catch (SeckillCloseException e){
            logger.error(e.getMessage());
        }

    }
    @Test
    public void executeSeckillProcedure(){
//        long seckillId = 1004;
//        long phone = 18868831756l;
//        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
//        if(exposer.isExposed()){
//            String md5 = exposer.getMd5();
//            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
//            logger.info("============="+seckillExecution.getStateInfo());
//        }
    }

}