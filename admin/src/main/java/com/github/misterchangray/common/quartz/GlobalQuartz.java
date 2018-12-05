package com.github.misterchangray.common.quartz;

import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.utils.DateUtils;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Map;

/**
 *
 * 全局定时任务
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 2018/5/3.
 *
 * cronExpression的配置说明
 * 字段   允许值   允许的特殊字符
 * 秒    0-59    , - * /
 * 分    0-59    , - * /
 * 小时   0-23    , - * /
 * 日期    1-31    , - * ? / L W C
 * 月份    1-12 或者 JAN-DEC    , - * /
 * 星期    1-7 或者 SUN-SAT    , - * ? / L C #
 * 年（可选）    留空, 1970-2099    , - * /
 * - 区间
 * * 通配符
 * ? 你不想设置那个字段
 *
 */
@Component("GlobalQuartz")
public class GlobalQuartz {
    Logger logger = LoggerFactory.getLogger(GlobalQuartz.class);
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ThreadPoolTaskExecutor executor;

    //储存请求时间
    public static ArrayDeque<String> dateStr = new ArrayDeque(Const.maxCacheTypes);

    private void startQuartz(QuartzEnum quartzEnum) {
        long start = System.currentTimeMillis();
        //根据接口类型返回相应的所有bean
        Map<String, BaseService> map = applicationContext.getBeansOfType(BaseService.class);
            for(String key : map.keySet()) {
                BaseService baseService = map.get(key);
                try{
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            baseService.quartz(quartzEnum, start);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("[{}] {}，{} 数据抓取失败", DateUtils.now(null), key, quartzEnum);
                }
            }

    }
    @Scheduled(fixedDelay = 500)
    public void globalQuart5() {
        long start = System.currentTimeMillis();
        // 每500 毫秒执行一次
//        logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.Mse500);
//        logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }
    @Scheduled(fixedDelay = 800)
    public void globalQuartz2() {
        long start = System.currentTimeMillis();
        // 每800 毫秒执行一次
//        logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.Mse800);
//        logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }

    @Scheduled(fixedDelay = 1000)
    public void globalQuartz1() {
        long start = System.currentTimeMillis();
        // 每一秒执行一次
//        logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.Second);
//        logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }

    /**
     * 简单轮询任务
     * fixedDelay = 60 * 1000；单位:毫秒
     */
//    @Scheduled(cron ="0/50 * * * * ?")
    @Scheduled(fixedDelay = 3 * 1000)
    public void globalQuartz3() {

        long start = System.currentTimeMillis();
        // 每3秒执行一次
        // logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.Second3);
        //logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }


    /**
     * 简单轮询任务
     * fixedDelay = 60 * 1000；单位:毫秒
     */
//    @Scheduled(cron ="0/50 * * * * ?")
    @Scheduled(fixedDelay = 3 * 60 * 1000)
    public void globalQuartz5() {
        long start = System.currentTimeMillis();
        // 每3分钟执行一次
        // logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.Minute3);
        //logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }

    /**
     * 每小时开始时执行
     * fixedDelay = 60 * 1000；单位:毫秒
     */
    @Scheduled(cron ="0 0 */1 * * ?")
    public void globalQuartz8() {
        System.out.println();
        long start = System.currentTimeMillis();
        // 每小时的第一秒执行
        // logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.HourStart);
        //logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }


    /**
     * 每小时的最后一秒开始时执行
     * fixedDelay = 60 * 1000；单位:毫秒
     */
    @Scheduled(cron ="59 59 */1 * * ?")
    public void globalQuartz9() {
        System.out.println();
        long start = System.currentTimeMillis();
        // 每小时的最后一秒执行
        // logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.HourLastSec);
        //logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }

    /**
     * 简单轮询任务
     * fixedDelay = 60 * 1000；单位:毫秒
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void globalQuartz6() {
        long start = System.currentTimeMillis();
        // 每分钟执行一次
        // logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.Minute);
        //logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }

    /**
     * 简单轮询任务
     * fixedDelay = 60 * 1000；单位:毫秒
     */
    @Scheduled(fixedDelay = 5 * 1000)
    public void globalQuartz() {

        long start = System.currentTimeMillis();
        // 每分钟执行一次
       // logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
        startQuartz(QuartzEnum.Second5);
        //logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);

    }
//
//    //每小时执行一次
//    @Scheduled(fixedDelay = 60 * 60 * 1000)
//    public void globalQuartzHours() {
//
//        long start = System.currentTimeMillis();
//        // 每分钟执行一次
//        // logger.info("--------------------------- 定时任务开始[{}] ---------------------------", DateUtils.now(null));
//       startQuartz(QuartzEnum.Hour);
//        //logger.info("--------------------------- 定时任务结束[{}] ------------耗时[{}]---------------", DateUtils.now(null), System.currentTimeMillis() - start);
//
//    }
}

