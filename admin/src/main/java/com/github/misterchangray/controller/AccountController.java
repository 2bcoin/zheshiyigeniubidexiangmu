package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.annotation.Authorization;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
import com.github.misterchangray.controller.common.vo.GoogleAuth;
import com.github.misterchangray.service.common.GoogleAuthCode;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.*;
import com.github.misterchangray.service.po.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取账户余额数据
 */
@Controller
@RequestMapping("/v1/account")
public class AccountController {
    @Autowired
    OKEXService OKEXService;
    @Autowired
    BiAnService biAnService;
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    HuoBiService huoBiService;
    @Autowired
    Big1Service big1Service;
    @Autowired
    OTCBTCService OTCBTCService;
    @Autowired
    FcoinService fcoinDataService;
    @Autowired
    BitfinexService bitfinexService;
    @Autowired
    ZbSiteService zbSiteService;
    @Autowired
    BitzService bitzService;

    public static List<Long> lastUpdateTime = new ArrayList<>();


    @ResponseBody
    @Authorization
    @RequestMapping(method = RequestMethod.GET, value = "/accountInfo")
    public ResultSet<String> delApiKey() {
        MapBuilder data = MapBuilder.build();
        Object os = mongoDbService.find(new Query(Criteria.where("time").is(HuoBiService.lastSpiderAccountData)), AccountInfo.collectionName, AccountInfo.class);
        return ResultSet.build(ResultEnum.SUCCESS).setData(os);
    }


    @ResponseBody
    @Authorization
    @RequestMapping(method = RequestMethod.GET, value = "/delApiKey")
    public ResultSet<String> delApiKey(@RequestParam(value = "id") String id) {
        MapBuilder data = MapBuilder.build();
        if(null == id) return ResultSet.build(ResultEnum.FAILURE);
        Map<String,Object> t= new HashMap<>();
        t.put("id", id);


        Object obj = mongoDbService.findOne(t, ApiConfigs.collectionName, ApiConfigs.class);
        if(null != obj && obj instanceof ApiConfigs) {
            ApiConfigs apiConfigs = (ApiConfigs) obj;

            Criteria criteria = Criteria.where("aKey").is(apiConfigs.getaKey());
            mongoDbService.update(criteria, MapBuilder.build().add("last", false), AccountInfo.collectionName, AccountInfo.class);
            mongoDbService.remove(t, ApiConfigs.collectionName, ApiConfigs.class);
        }


        return ResultSet.build(ResultEnum.SUCCESS);
    }


    @ResponseBody
    @Authorization
    @RequestMapping(method = RequestMethod.POST, value = "/addApiKey")
    public ResultSet<String> addApiKey(@RequestBody ApiConfigs apiConfigs) {
        MapBuilder data = MapBuilder.build();

        List<Object> apiConfigs1 = mongoDbService.findAll(ApiConfigs.collectionName, ApiConfigs.class);
        if(null != apiConfigs.getaKey() && null != apiConfigs.getsKey() && null != apiConfigs.getAccount()) {

            for(Object o : apiConfigs1) {
                if(o instanceof ApiConfigs) {
                    ApiConfigs a = (ApiConfigs) o;
                    if(a.getsKey().equals(apiConfigs.getsKey()) && a.getaKey().equals(apiConfigs.getaKey())) {
                        return ResultSet.build(ResultEnum.FAILURE).setMsg("已存在");
                    }
                }
            }
            apiConfigs.setCreateTime(System.currentTimeMillis());
            mongoDbService.insert(apiConfigs, ApiConfigs.collectionName);
            return ResultSet.build(ResultEnum.SUCCESS);
        }



        return ResultSet.build(ResultEnum.FAILURE);
    }

    @ResponseBody
    @Authorization
    @RequestMapping(method = RequestMethod.GET, value = "/listApiKey")
    public ResultSet<String> listApiKey() {
        MapBuilder data = MapBuilder.build();
        List<Object> apiConfigs1 = mongoDbService.findAll(ApiConfigs.collectionName, ApiConfigs.class);
        return ResultSet.build(ResultEnum.SUCCESS).setData(apiConfigs1);
    }







    ///////////////////////google验证码部分

    @ResponseBody
    @Authorization
    @RequestMapping(method = RequestMethod.GET, value = "/delLingPai")
    public ResultSet<String> delLingPai(@RequestParam(value = "id") String id) {
        MapBuilder data = MapBuilder.build();
        if(null == id) return ResultSet.build(ResultEnum.FAILURE);
        Map<String,Object> t= new HashMap<>();
        t.put("id", id);
        mongoDbService.remove(t, GoogleAuth.collectionName, GoogleAuth.class);
        return ResultSet.build(ResultEnum.SUCCESS);
    }


    @ResponseBody
    @Authorization
    @RequestMapping(method = RequestMethod.POST, value = "/addLingPai")
    public ResultSet<String> addLingPai(@RequestBody GoogleAuth googleAuth) {
        MapBuilder data = MapBuilder.build();

        if(null != googleAuth.getMiyao() && null != googleAuth.getAccount() ) {
            List<Object> auths = mongoDbService.findAll(GoogleAuth.collectionName, GoogleAuth.class);
            for(Object o : auths) {
                if(o instanceof GoogleAuth) {
                    GoogleAuth a = (GoogleAuth) o;
                    if(googleAuth.getMiyao().equals(a.getMiyao())) {
                        return ResultSet.build(ResultEnum.FAILURE).setMsg("已存在");
                    }
                }
            }

            googleAuth.setCreateTime(System.currentTimeMillis());
            mongoDbService.insert(googleAuth, GoogleAuth.collectionName);
            return ResultSet.build(ResultEnum.SUCCESS);
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    @ResponseBody
    @Authorization
    @RequestMapping(method = RequestMethod.GET, value = "/listLingPai")
    public ResultSet<String> listLingPai() {
        MapBuilder data = MapBuilder.build();
        List<Object> auths = mongoDbService.findAll(GoogleAuth.collectionName, GoogleAuth.class);
        return ResultSet.build(ResultEnum.SUCCESS).setData(auths);
    }


    /**
     * 获取所有google验证码
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/vercode")
    public ResultSet<String> vercode() {
        ArrayList arrayList = new ArrayList<MapBuilder>();
        List<Object> auths = mongoDbService.findAll(GoogleAuth.collectionName, GoogleAuth.class);
        for(Object o : auths) {
            if(o instanceof  GoogleAuth) {
                GoogleAuth googleAuth = (GoogleAuth) o;
                String vercode = GoogleAuthCode.getTOTP(googleAuth.getMiyao().toString(),System.currentTimeMillis() / 1000 / 30);
                arrayList.add(MapBuilder.build().add("account", googleAuth.getAccount()).add("desc", googleAuth.getDesc()).add("ver", vercode));
            }
        }
        return ResultSet.build().setData(arrayList);
    }



    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/info")
    public ResultSet<String> account() {
        MapBuilder data = MapBuilder.build();

        Map<String, AccountInfo> res = new HashMap<>();
        List<Object> accountInfoList = mongoDbService.findAll("accountInfo", AccountInfo.class);

        AccountInfo accountInfo;
        for(Object tmp : accountInfoList) {
            if(tmp instanceof AccountInfo) {
                accountInfo = (AccountInfo) tmp;

                String name = accountInfo.getOrgName() + accountInfo.getType();
                AccountInfo tmp2 =  res.get(name);

                if(null == tmp2) {
                    res.put(name, accountInfo);
                } else if(accountInfo.getTime() > tmp2.getTime()) {
                    res.put(name, accountInfo);
                };
            }
        }

        return ResultSet.build().setData(res);
    }



}
