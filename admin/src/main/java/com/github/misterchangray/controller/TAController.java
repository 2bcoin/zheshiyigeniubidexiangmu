package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.annotation.Authorization;
import com.github.misterchangray.common.enums.DBEnum;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.vo.TAConfigs;
import com.github.misterchangray.service.business.TAService;
import com.github.misterchangray.service.common.DbLogService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.BiAnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 三角套利
 */
@RequestMapping("/v1/ta")
@Controller
@Authorization
public class TAController {
    @Autowired
    BiAnService biAnService;
    @Autowired
    DbLogService dbLogService;
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    TAService taService;

    /**
     * 修改全局下单常量
     * @return
     */
    @RequestMapping(value = "/editFv", method = RequestMethod.POST)
    @ResponseBody
    public ResultSet editFv(@RequestBody  Map<String, Double> param) {
        if(null == param) return  ResultSet.build(ResultEnum.FAILURE);;
        if(null != param.get("fv") && param.get("fv") > 0.005) {
            TAService.default_fv = param.get("fv");
        }
        if(null != param.get("fv2") && param.get("fv2") > 0.001) {
            TAService.default_fv_for_log = param.get("fv2");
        }
        return ResultSet.build(ResultEnum.SUCCESS);
    }



    /**
     * 删除三角配置
     * @return
     */
    @RequestMapping(value = "/delTAConfigs", method = RequestMethod.GET)
    @ResponseBody
    public ResultSet delTAConfigs(@RequestParam("id") String id) {
        if(null == id) return ResultSet.build(ResultEnum.FAILURE);
        Map<String,Object> t= new HashMap<>();
        t.put("id", id);
        mongoDbService.remove(t, TAConfigs.collectionName, TAConfigs.class);
        taService.loadConfigs();
        return ResultSet.build(ResultEnum.SUCCESS);
    }


    /**
     * 三角配置列表
     * @return
     */
    @RequestMapping(value = "/listTAConfigs", method = RequestMethod.GET)
    @ResponseBody
    public ResultSet listTAConfigs() {
       List<Object> objects = mongoDbService.findAll(TAConfigs.collectionName, TAConfigs.class);
       return ResultSet.build(ResultEnum.SUCCESS).setData(objects);
    }

    /**
     * 增加三角配置
     * @return
     */
    @RequestMapping(value = "/addTAConfigs", method = RequestMethod.POST)
    @ResponseBody
    public ResultSet addTAConfigs(@RequestBody TAConfigs taConfigs) {
        if(3 == taConfigs.size()) {
            taConfigs.setInsertDate(System.currentTimeMillis());
            mongoDbService.insert(taConfigs,TAConfigs.collectionName);
            taService.loadConfigs();
            return ResultSet.build(ResultEnum.SUCCESS).setData(taConfigs);
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    /**
     * 输出三角配置到mongodb
     * @return
     */
    @RequestMapping(value = "/exportToDb", method = RequestMethod.GET)
    @ResponseBody
    public ResultSet exportToDb() {
        TAConfigs taConfigs2 = null;
        for(TAConfigs taConfigs : Const.taConfig) {
            taConfigs2 = TAConfigs.build(taConfigs.getTaConfig(0), taConfigs.getTaConfig(1), taConfigs.getTaConfig(2));
            mongoDbService.insert(taConfigs2, TAConfigs.collectionName);
        }
        return ResultSet.build().setData(TAService.isRunning);
    }


    /**
     * 启动三角运行
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    @ResponseBody
    public ResultSet start() {
        TAService.isRunning = true;
        TAService.startTime = System.currentTimeMillis();
        dbLogService.info(DBEnum.SERVICE, DBEnum.SANJIAO.getDesc() + DBEnum.START.getDesc());
        return ResultSet.build().setData(TAService.isRunning);
    }

    /**
     * 暂停三角运行
     * @return
     */
    @RequestMapping(value = "/pause", method = RequestMethod.GET)
    @ResponseBody
    public ResultSet pause() {
        TAService.isRunning = false;
        TAService.startTime = null;
        dbLogService.info(DBEnum.SERVICE, DBEnum.SANJIAO.getDesc() + DBEnum.STOP.getDesc());
        return ResultSet.build().setData(TAService.isRunning);
    }

    /**
     * 获取三角当前状态
     * @return
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public ResultSet status() {
        return ResultSet.build().setData(
                MapBuilder.build()
                        .add("status",TAService.isRunning)
                        .add("startTime", TAService.startTime)
                .add("lv1", TAService.default_fv)
                .add("lv2", TAService.default_fv_for_log)
        );
    }
}
