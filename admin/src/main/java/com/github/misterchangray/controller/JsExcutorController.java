package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.vo.IPAndScript;
import com.github.misterchangray.service.common.H2DataBaseCache;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.jsexcutor.JSExecutor;
import com.github.misterchangray.service.jsexcutor.dto.RunningImage;
import com.github.misterchangray.service.jsexcutor.dto.ScriptDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/v1/jsexcutor")
public class JsExcutorController {
    @Autowired
    JSExecutor jsExcutor;
    @Autowired
    H2DataBaseCache h2DataBaseCache;
    @Autowired
    MongoDbService mongoDbService;

    /**
     * data 应该为 base64编码 且为map
     * map 应该为以下格式
     * {
     * s:"",
     * p:""
     * }
     * 其中p为你想携带得参数
     *
     * @param data
     * @return
     */
    private static String getParam(String data) {
        String s = CryptoUtils.decodeBASE64(data);
        Map<String, String> m = JSONUtils.json2map(s);
        if (null == m.get("s") || null == m.get("p")) return null;
        int t = (int) ((System.currentTimeMillis() / 1000) / 60);
        if (!m.get("s").equalsIgnoreCase(CryptoUtils.encodeMD5(String.valueOf(t)))) return null;
        return m.get("p");
    }


    //防止重复提交脚本
    private static String lastPostData = "";

    @RequestMapping(value = "/runjs", method = RequestMethod.POST)
    @ResponseBody
    public ResultSet runjs(@RequestParam(value = "data") String data) {
        data = getParam(data);
        if (null == data)
            return ResultSet.build(ResultEnum.VALIDATEPARAMETER);
        if (data.equalsIgnoreCase(lastPostData))
            return ResultSet.build(ResultEnum.DONTRESUBMIT);
        lastPostData = data;
        ScriptDto scriptDto = JSONUtils.json2obj(data, ScriptDto.class);
        scriptDto.setConfig("Configs(" + scriptDto.getConfig() + ");");
        String pid = jsExcutor.init().runScript(scriptDto);
        return ResultSet.build(ResultEnum.SUCCESS).setData(pid);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/shutdown")
    public ResultSet<String> shutdown(@RequestParam("data") String data) {
        data = getParam(data);
        if (null == data)
            return ResultSet.build(ResultEnum.VALIDATEPARAMETER);
        for (RunningImage runningImage : JSExecutor.runningImage) {
            if (runningImage.isRunning() && runningImage.gettId().toString().equals(data)) {
                runningImage.getThread().stop();
                runningImage.setRunning(false);
                lastPostData = "";
                return ResultSet.build(ResultEnum.SUCCESS);
            }
        }
        return ResultSet.build(ResultEnum.NOTFOUNDTHREAD);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResultSet<String> list() {
        ListBuilder listBuilder = ListBuilder.build();
        for (RunningImage runningImage : JSExecutor.runningImage) {
            RunningImage runningImage1 = new RunningImage();
            BeanUtils.copyProperties(runningImage, runningImage1);

            runningImage1.getScript().setConfig(null);
            runningImage1.getScript().setScript(null);
            listBuilder.append(runningImage1);
        }
        return ResultSet.build(ResultEnum.SUCCESS).setData(listBuilder);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/info")
    public ResultSet<ScriptDto> info(@RequestParam("id") String id) {
        ListBuilder listBuilder = ListBuilder.build();
        MapBuilder m  = MapBuilder.build();
        Object ipAndScript = mongoDbService.findOne(MapBuilder.build().add("id", id), IPAndScript.getCollectionName(), IPAndScript.class);
        if(null != ipAndScript) {
            IPAndScript t = (IPAndScript) ipAndScript;
            m.put("title", t.getScriptName());
            m.put("ipAndScriptName", t.getTitle());
            m.put("ip", makeIp(t.getIp()));
            for (RunningImage runningImage : JSExecutor.runningImage) {
                if(runningImage.isRunning() &&
                        runningImage.getScript().getIpAndScriptId().equalsIgnoreCase(id)) {
                    m.put("running", true);
                    m.put("date", runningImage.getScript().getDate());
                }
            }
            return ResultSet.build(ResultEnum.SUCCESS).setData(m);
        }

        return ResultSet.build(ResultEnum.FAILURE);

    }

    private String makeIp(String ip) {
        String res = "";
        String[] i = ip.split("\\.");
        for(int j=0;j<i.length; j++) {
            if(j != 3) {
                res += i[j] + ".";
            } else {
                res += "*";
            }
        }
        return res;
    }

    /**
     * @param id 托管者ID
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/cache")
    public ResultSet<String> cache(@RequestParam("id") String id) {
        String key = "cache_" + id;
        return ResultSet.build(ResultEnum.SUCCESS).setData(h2DataBaseCache.get(key));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/live")
    public ResultSet<String> live() {
        return ResultSet.build(ResultEnum.SUCCESS).setData(true);
    }


    /**
     * 托管者id
     *
     * @param page
     * @param tId
     * @param limit
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/log")
    public ResultSet<String> log(@RequestParam(value = "page", required = false) Integer page,
                                 @RequestParam(value = "id") String tId,
                                 @RequestParam(value = "limit", required = false) Integer limit) {
        if (null == limit) limit = 200; //默认每页200条
        if (null == page || 1 == page) page = 0;
        return ResultSet.build(ResultEnum.SUCCESS).setData(h2DataBaseCache.log(tId, limit, page));
    }
}
