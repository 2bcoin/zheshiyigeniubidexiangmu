package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.annotation.Authorization;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.exception.ServiceException;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.service.business.TestSocket;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.jsexcutor.Global;
import com.github.misterchangray.service.jsexcutor.JSExecutor;
import com.github.misterchangray.service.jsexcutor.vo.FMZTicker;
import com.github.misterchangray.service.po.DbLog;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * @author Created by rui.zhang on 2018/5/25.
 * @author rui.zhang
 * @version ver1.0
 * @email misterchangray@hotmail.com
 * @description
 */
@Controller
@RequestMapping("/v1/test")
public class TestController {
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    JSExecutor jsExecutor;

    org.slf4j.Logger logger = LoggerFactory.getLogger(Global.class);
    public static void main(String a[]) {
        new TestController().logger.info("h2DataBaseCache: level:{}, mes:{}" , 2, 3);
    }


    @RequestMapping(value = "test1", method = RequestMethod.GET)
    @ResponseBody
    public ResultSet constant(@RequestParam(required = false) Integer pid) throws Exception {
        ResultSet res = ResultSet.build();
        Query q = new Query();
        q.addCriteria(
                Criteria.where("time").gte(1535336613492l).andOperator(Criteria.where("type").is("sanjiao"))
        );
        mongoDbService.find(q, DbLog.collectionName, DbLog.class);

        return res;
    }

    @RequestMapping(value = "test2", method = RequestMethod.GET)
    @ResponseBody
    public ResultSet constant2(@RequestParam(required = false) Integer pid) throws Exception {
        ResultSet res = ResultSet.build();

//        EmailBuilder.build().sendSimpleEmail("jioulongzi@qq.com", " 914590431@qq.com", "wocao", "dajia 快看啊 阿道夫为");
        res.getData().toString();
//        throw new ServiceException(ResultEnum.EXIST);
        return res;
    }
}
