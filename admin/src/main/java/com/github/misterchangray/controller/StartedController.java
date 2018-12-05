package com.github.misterchangray.controller;


import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.service.business.StopLossService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1/started")
public class StartedController {
    Logger logger = LoggerFactory.getLogger(StartedController.class);

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "stoploss")
    public ResultSet<String> stoploss(@RequestParam("started") Boolean started) {
        StopLossService.started = started;
        StopLossService.startedTime = System.currentTimeMillis();
        logger.info("stoploss状态发生改变：" + StopLossService.started);
        return ResultSet.build().setData(MapBuilder.build().add("started", StopLossService.started)
                .add("startedTime", StopLossService.startedTime));
    }



}
