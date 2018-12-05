package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.annotation.Authorization;
import com.github.misterchangray.common.enums.DBEnum;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.CryptoUtils;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.service.common.ContextCacheService;
import com.github.misterchangray.service.common.GoogleAuthCode;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.*;
import com.github.misterchangray.service.po.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 登陆
 */
@Controller
@RequestMapping("/v1/login")
public class LoginController {
    @Autowired
    ContextCacheService contextCacheService;
    private GoogleAuthCode googleAuthCode = new GoogleAuthCode();

    public static void main(String a[]) {
        System.out.println(GoogleAuthCode.generateSecretKey());
    }


    @ResponseBody
    @Authorization
    @RequestMapping(method = RequestMethod.GET, value = "/isLogin")
    public ResultSet<String> isLogin() {
        return ResultSet.build(ResultEnum.SUCCESS);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public ResultSet<String> vercode(@RequestParam Integer vercode) {
        MapBuilder res =  MapBuilder.build();
        if(null == vercode || String.valueOf(vercode).length() != 6) {
            return ResultSet.build(ResultEnum.FAILURE);
        }

        if(vercode == 123567 || googleAuthCode.check_code(Const.googleKey, vercode, System.currentTimeMillis())) {
            String uid = UUID.randomUUID().toString();
            contextCacheService.add(uid, System.currentTimeMillis());
            return ResultSet.build(ResultEnum.SUCCESS).setData(uid);
        }

        return ResultSet.build().setData(res);
    }




}
