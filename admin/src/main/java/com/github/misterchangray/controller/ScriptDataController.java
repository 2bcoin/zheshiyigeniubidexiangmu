package com.github.misterchangray.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.vo.FMZConfigs;
import com.github.misterchangray.controller.common.vo.IPAndScript;
import com.github.misterchangray.controller.common.vo.ScriptData;
import com.github.misterchangray.controller.vo.RobotInfo;
import com.github.misterchangray.controller.vo.Slave;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.jsexcutor.Global;
import com.github.misterchangray.service.jsexcutor.dto.ScriptDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequestMapping("/v1/scriptData")
public class ScriptDataController {
    @Autowired
    MongoDbService mongoDbService;

    org.slf4j.Logger logger = LoggerFactory.getLogger(Global.class);

    public static Map<String, Slave> ListIPAndScrpit = new HashMap<>(100);

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/aouScript")
    public ResultSet<String> aou(@RequestBody ScriptData scriptData) {
        try {
            if (StringUtils.isNotBlank(scriptData.getId())) {
                //修改
                Map<String, Object> t = new HashMap<>();
                t.put("id", scriptData.getId());
                Object obj = mongoDbService.findOne(t, ScriptData.getCollectionName(), ScriptData.class);
                ScriptData scriptData1 = JSONUtils.json2obj(JSONUtils.obj2json(obj), ScriptData.class);
                if (null != obj && obj instanceof ScriptData) {
                    Map<String, Object> d = new HashMap<>();
                    d.put("id", scriptData.getId());
                    d.put("title", scriptData.getTitle());
                    d.put("scriptStr", scriptData.getScriptStr());
                    d.put("tag", scriptData.getTag());
                    d.put("updateTime", System.currentTimeMillis());
                    d.put("creatTime", scriptData1.getCreatTime());
                    mongoDbService.update(d, ScriptData.getCollectionName(), ScriptData.class);

                    Query q = new Query(Criteria.where("scriptID").is(scriptData.getId()));
                    List<IPAndScript> list = mongoDbService.find(q, IPAndScript.getCollectionName(), IPAndScript.class);
                    for (IPAndScript o : list) {
                        IPAndScript o2 = o;
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", o2.getId());
                        map.put("ip", o2.getIp());
                        map.put("title", o2.getTitle());
                        map.put("scriptID", o2.getScriptID());
                        map.put("scriptName", scriptData.getTitle());
                        map.put("configurationID", o2.getConfigurationID());
                        map.put("configurationName", o2.getConfigurationName());
                        map.put("creatTime", o2.getCreatTime().toString());
                        mongoDbService.update(map, IPAndScript.getCollectionName(), IPAndScript.class);
                    }

                    return ResultSet.build(ResultEnum.SUCCESS);
                } else {
                    return ResultSet.build(ResultEnum.NOT_FOUND);
                }
            } else {
                //新增
                if ("".equals(scriptData.getId())) {
                    scriptData.setId(null);
                }
                scriptData.setCreatTime(System.currentTimeMillis());
                scriptData.setUpdateTime(System.currentTimeMillis());
                mongoDbService.insert(scriptData, ScriptData.getCollectionName());
                return ResultSet.build(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/delScript")
    public ResultSet<String> del(@RequestParam(value = "id") String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                //判断数据是否存在
                Map<String, Object> t = new HashMap<>();
                t.put("id", id);
                Object obj = mongoDbService.findOne(t, ScriptData.getCollectionName(), ScriptData.class);
                if (null != obj && obj instanceof ScriptData) {
                    mongoDbService.remove(t, ScriptData.getCollectionName(), ScriptData.class);
                    return ResultSet.build(ResultEnum.SUCCESS);
                } else {
                    return ResultSet.build(ResultEnum.GONE);
                }
            } else {
                return ResultSet.build(ResultEnum.INVALID_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/listScript")
    public ResultSet<String> list() {
        List<Object> list = mongoDbService.findAll(ScriptData.collectionName, ScriptData.class);
        return ResultSet.build(ResultEnum.SUCCESS).setData(list);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/finOne")
    public ResultSet<String> findOne(@RequestParam(value = "id") String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                //判断数据是否存在
                Map<String, Object> t = new HashMap<>();
                t.put("id", id);
                Object obj = mongoDbService.findOne(t, ScriptData.getCollectionName(), ScriptData.class);
                if (null != obj && obj instanceof ScriptData) {
                    return ResultSet.build(ResultEnum.SUCCESS).setData(obj);
                } else {
                    return ResultSet.build(ResultEnum.GONE);
                }
            } else {
                return ResultSet.build(ResultEnum.INVALID_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    /**
     * IP获取
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/ip")
    public ResultSet<String> ip(HttpServletRequest request) throws IOException {
        String userIpAddr = HttpRequestParserUtils.getUserIpAddr(request);
        if (ListIPAndScrpit.get(userIpAddr) == null) {
            ListIPAndScrpit.put(userIpAddr, Slave.build(userIpAddr));
        }
        if (ListIPAndScrpit.get(userIpAddr).getRobotInfoList().size() == 0) {
            List<RobotInfo> robotInfos = showStatus(userIpAddr);
            if (robotInfos == null) {
                System.out.println(ResultEnum.RESTOREFAILURE.getMsg());
            }
        }
        return null;
    }

    /**
     * 新增
     *
     * @param ipAndScript
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/addIP")
    public ResultSet<String> addIP(@RequestBody IPAndScript ipAndScript) {
        try {
//TODO 验证str是否重复
//            String str = new StringBuffer().append(ipAndScript.getIp())
//                    .append(ipAndScript.getScriptName()).append(ipAndScript.getConfiguration()).toString();
//            Query q = new Query(Criteria.where("str").is(str));
//            Object obj = mongoDbService.find(q, IPAndScript.getCollectionName(), IPAndScript.class);
            Boolean jub = false;
            if (jub) {
                return ResultSet.build(ResultEnum.EXIST);
            } else {
                ipAndScript.setCreatTime(System.currentTimeMillis());
                mongoDbService.insert(ipAndScript, IPAndScript.getCollectionName());
                return ResultSet.build(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/findOneIP")
    public ResultSet<String> findOneIP(@RequestParam(value = "id") String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                //判断数据是否存在
                Map<String, Object> t = new HashMap<>();
                t.put("id", id);
                Object obj = mongoDbService.findOne(t, IPAndScript.getCollectionName(), IPAndScript.class);
                if (null != obj && obj instanceof IPAndScript) {
                    return ResultSet.build(ResultEnum.SUCCESS).setData(obj);
                } else {
                    return ResultSet.build(ResultEnum.GONE);
                }
            } else {
                return ResultSet.build(ResultEnum.INVALID_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/delIP")
    public ResultSet<String> delIP(@RequestParam(value = "id") String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                //判断数据是否存在
                Map<String, Object> t = new HashMap<>();
                t.put("id", id);
                Object obj = mongoDbService.findOne(t, IPAndScript.getCollectionName(), IPAndScript.class);
                if (null != obj && obj instanceof IPAndScript) {
                    mongoDbService.remove(t, IPAndScript.getCollectionName(), IPAndScript.class);
                    return ResultSet.build(ResultEnum.SUCCESS);
                } else {
                    return ResultSet.build(ResultEnum.GONE);
                }
            } else {
                return ResultSet.build(ResultEnum.INVALID_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/listIP")
    public ResultSet<String> listIP() {
        MapBuilder mapBuilder = MapBuilder.build();
        List<Object> list = mongoDbService.findAll(IPAndScript.collectionName, IPAndScript.class);
        List<Object> scriptList = mongoDbService.findAll(ScriptData.collectionName, ScriptData.class);
        List<Object> fmzList = mongoDbService.findAll(FMZConfigs.collectionName, FMZConfigs.class);


        if (list.size() > 0) {
            for (Object o : list) {
                if (o instanceof IPAndScript) {
                    IPAndScript o2 = (IPAndScript) o;
                    o2.setRobotStatus(ResultEnum.HASSTOPPED.getMsg());
                    Slave slave = ListIPAndScrpit.get(o2.getIp());
                    if (slave != null) {
                        List<RobotInfo> robotInfoList = slave.getRobotInfoList();
                        if (robotInfoList.size() > 0) {
                            for (RobotInfo robotInfo : robotInfoList) {
                                if (robotInfo.getId().equals(o2.getId())) {
                                    o2.setRobotStatus(ResultEnum.HASSTARTED.getMsg());
                                }
                            }
                        }
                    }
                }
            }
        }
        mapBuilder.put("IPMap", ListIPAndScrpit);
        mapBuilder.put("list", list);
        mapBuilder.put("scriptList", scriptList);
        mapBuilder.put("fmzList", fmzList);

        return ResultSet.build(ResultEnum.SUCCESS).setData(mapBuilder);
    }

    private static String lastBootId = null;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/start")
    public ResultSet<String> start(@RequestParam(value = "id") String id) throws IOException {
        if (id.equalsIgnoreCase(lastBootId))
            return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.NOTREPEATSTART.getMsg());
        lastBootId = id;

        IPAndScript ipAndScript = getIPByID(id);
        if (StringUtils.isNotBlank(ipAndScript.getIp())) {
            //启动前查看该托管机器人是否启动
            int i = showStatusOne(ipAndScript.getIp(), id);
            if (i == 0) {
                return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.INSERVICE.getMsg());
            } else if (i == 1) {
                lastBootId = null;
                return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.OFFLINE.getMsg());
            }

            ScriptDto scriptDto = new ScriptDto();
            scriptDto.setIpAndScriptId(ipAndScript.getId());
            scriptDto.setCustomConfig(ipAndScript.getCustomConfig());
            String configStr = getConfigByID(ipAndScript.getConfigurationID());
            if (configStr == null) {
                return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.CONFIGNONE.getMsg());
            }
            scriptDto.setIpAndScriptName(ipAndScript.getTitle());
            if (configStr == null) {
                return ResultSet.build(ResultEnum.STRATEGYNONE);
            } else {
                scriptDto.setConfig(configStr);
            }
            scriptDto.setIpAndScriptId(ipAndScript.getId());
            scriptDto.setConfigId(ipAndScript.getConfigurationID());

            //根据策略ID查询脚本
            Map<String, Object> m = new HashMap<>();
            m.put("id", ipAndScript.getScriptID());
            Object obj2 = mongoDbService.findOne(m, ScriptData.getCollectionName(), ScriptData.class);
            if (null != obj2 && obj2 instanceof ScriptData) {
                ScriptData scriptData = JSONUtils.json2obj(JSONUtils.obj2json(obj2), ScriptData.class);
                scriptDto.setScript(scriptData.getScriptStr());
                scriptDto.setTitle(scriptData.getTitle());
                scriptDto.setTag(scriptData.getTag());
                scriptDto.setScriptId(scriptData.getId());
                //将ScriptDto对象转为JOSN字符串并且base64加密
                String jsonStr = JSONUtils.obj2json(scriptDto);

                //存储需要传的参数
                Map<String, String> data = new HashMap<>();
                data.put("p", jsonStr);
                int t = (int) ((System.currentTimeMillis() / 1000) / 60);
                data.put("s", CryptoUtils.encodeMD5(String.valueOf(t)));

                String str = CryptoUtils.encodeBASE64(JSONUtils.obj2json(data));
                Map<String, String> map = new HashMap<>();
                map.put("data", str);
                try {
                    String s = HttpUtilManager.getInstance()
                            .requestHttpPost("http://" + ipAndScript.getIp() + ":8080/common-core/v1/jsexcutor/runjs", null, map, MapBuilder.build());
                    if (s == null) {
                        lastBootId = null;
                        return ResultSet.build(ResultEnum.FAILURE).setMsg("s==null");
                    }
                    System.out.println(s);
                    if (s.contains("<!doctype html><html lang=\"en\">"))
                        return ResultSet.build(ResultEnum.FAILURE).setMsg(s);
                    Map map1 = JSONUtils.json2map(s);
                    Integer code = (Integer) map1.get("code");

                    if (code == 0) {
                        RobotInfo robotInfo = new RobotInfo();
                        robotInfo.setId(ipAndScript.getId());
                        robotInfo.setConfig(getConfigByID(ipAndScript.getConfigurationID()));
                        robotInfo.setScriptId(scriptData.getId());
                        robotInfo.setTid(String.valueOf(map1.get("data")));
                        if (ListIPAndScrpit.get(ipAndScript.getIp()) != null
                                && ListIPAndScrpit.get(ipAndScript.getIp()).getRobotInfoList() != null) {
                            ListIPAndScrpit.get(ipAndScript.getIp()).getRobotInfoList().add(robotInfo);
                            return ResultSet.build(ResultEnum.SUCCESS).setMsg(ResultEnum.STARTUPSUCCESS.getMsg());
                        } else {
                            if (ListIPAndScrpit.get(ipAndScript.getIp()) == null) {
                                return ResultSet.build(ResultEnum.FAILURE).setMsg("slave为空");
                            } else {
                                return ResultSet.build(ResultEnum.FAILURE).setMsg("RobotInfoList为空");
                            }
                        }
                    } else {
                        return ResultSet.build(ResultEnum.FAILURE).setMsg(code.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return ResultSet.build(ResultEnum.STRATEGYNONE);
            }
        } else {
            return ResultSet.build(ResultEnum.GONE);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/shutdown")
    public ResultSet<String> shutdown(@RequestParam(value = "id") String id) throws IOException {

        String ip = getIPByID(id).getIp();
        Slave slave = ListIPAndScrpit.get(ip);
        if (slave == null) {
            logger.info("slave为空");
            return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.NOT_FOUND.getMsg());
        }
        List<RobotInfo> ltmp = slave.getRobotInfoList();
        if (ltmp != null) {
            for (RobotInfo robotInfo : ltmp) {
                if (robotInfo.getId().equalsIgnoreCase(id)) {

                    Map<String, String> data = new HashMap<>();
                    data.put("p", robotInfo.getTid());
                    data.put("s", CryptoUtils.encodeMD5(DateUtils.now("yyyyMMddHHmm")));
                    String str = CryptoUtils.encodeBASE64(JSONUtils.obj2json(data));
                    Map<String, String> map = new HashMap<>();
                    map.put("data", str);

                    String s = HttpUtilManager.getInstance()
                            .requestHttpGet("http://" + getIPByID(id).getIp() + ":8080/common-core/v1/jsexcutor/shutdown",
                                    map, MapBuilder.build());
                    if (s == null) {
                        return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.STOPFAILURECLIENTOFFLINE.getMsg());
                    }
                    Map map1 = JSONUtils.json2map(s);
                    Integer code = (Integer) map1.get("code");
                    if (code == 0) {
                        ltmp.remove(robotInfo);
                        slave.setRobotInfoList((CopyOnWriteArrayList) ltmp);
                        lastBootId = null;
                        return ResultSet.build(ResultEnum.SUCCESS);
                    } else if (code == 1) {
                        return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.THREADNOTCLOSEORNOTFOUND.getMsg());
                    }
                }
            }
        } else {
            return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.OFFLINE.getMsg());
        }
        return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.IPNORUNNINGTHREADS.getMsg());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/showStatus")
    private List<RobotInfo> showStatus(String ip) throws IOException {

        //托管机器人
        String s = HttpUtilManager.getInstance()
                .requestHttpGet("http://" + ip + ":8080/common-core/v1/jsexcutor/list", null, MapBuilder.build());
        if (s == null) {
            return null;
        }
        JsonNode jsonNode = JSONUtils.buildJsonNode(s);
        JsonNode jsonNode1 = jsonNode.get("data");

        CopyOnWriteArrayList<RobotInfo> list = new CopyOnWriteArrayList<>();
        for (JsonNode node : jsonNode1) {
            Boolean running = node.get("running").asBoolean();
            if (running) {
                RobotInfo robotInfo = new RobotInfo();
                robotInfo.setTid(node.get("tId").asText());
                robotInfo.setId(node.get("script").get("ipAndScriptId").asText());
                list.add(robotInfo);
            }
        }
        if (0 < list.size()) ListIPAndScrpit.get(ip).setRobotInfoList(list);
        return list;
    }

    private IPAndScript getIPByID(String id) {
        Map<String, Object> t = new HashMap<>();
        t.put("id", id);
        Object obj = mongoDbService.findOne(t, IPAndScript.getCollectionName(), IPAndScript.class);
        if (null != obj && obj instanceof IPAndScript) {
            //托管机器人
            IPAndScript ipAndScript = JSONUtils.json2obj(JSONUtils.obj2json(obj), IPAndScript.class);
            return ipAndScript;
        } else {
            return null;
        }
    }

    /**
     * 1代表机器离线
     * 2该策略已停止
     * 0策略已启动
     *
     * @param ip
     * @param id
     * @return
     * @throws IOException
     */
    private int showStatusOne(String ip, String id) throws IOException {
        //托管机器人
        String s = HttpUtilManager.getInstance()
                .requestHttpGet("http://" + ip + ":8080/common-core/v1/jsexcutor/list", null, MapBuilder.build());
        if (s == null) {
            return 1;
        }
        JsonNode jsonNode = JSONUtils.buildJsonNode(s);
        JsonNode jsonNode1 = jsonNode.get("data");

        for (JsonNode node : jsonNode1) {
            Boolean running = node.get("running").asBoolean();
            if (running && node.get("script").get("ipAndScriptId").asText().equals(id)) {
                return 0;
            }
        }
        return 2;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/shutThread")
    public ResultSet<List> shutThread(HttpServletRequest request, @RequestParam(value = "tId") String tId) {
        String userIpAddr = HttpRequestParserUtils.getUserIpAddr(request);
        if (userIpAddr != null && tId != null) {
            List<RobotInfo> robotInfoList = ListIPAndScrpit.get(userIpAddr).getRobotInfoList();
            if (robotInfoList.size() > 0) {
                for (RobotInfo robotInfo : robotInfoList) {
                    if (tId.equals(robotInfo.getTid())) {
                        robotInfoList.remove(robotInfo);
                        return ResultSet.build(ResultEnum.SUCCESS);
                    }
                }
            } else {
                return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.NOTFOUNDTHREAD.getMsg());
            }
        } else if (userIpAddr == null) {
            return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.IPNONE.getMsg());
        } else if (tId == null) {
            return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.IDNONE.getMsg());
        } else {
            return ResultSet.build(ResultEnum.FAILURE);
        }
        return null;
    }

    private String getConfigByID(List<String> id) {
        String format = "";
        if (id.size() > 0) {
            for (String s : id) {
                Map<String, Object> fmzParam = new HashMap<>();
                fmzParam.put("id", s);
                Object one = mongoDbService.findOne(fmzParam, FMZConfigs.getCollectionName(), FMZConfigs.class);
                if (one != null && one instanceof FMZConfigs) {
                    FMZConfigs one2 = (FMZConfigs) one;

                    format = format + "," + MessageFormat.format("'{'\"__AKEY\":\"{0}\",\"__SKEY\":\"{1}\",\"__ORG\":\"{2}\",\"__COIN\":\"{3}\",\"__TYPE\":\"{4}\",\"__ContractType\":\"{5}\",\"__Direction\":\"{6}\",\"__MarginLevel\":\"{7}\",\"__DefaultPeriod\":\"{8}\"'}'"
                            , one2.getAKEY(), one2.getSKEY(), one2.getORG(), one2.getCOIN(), one2.getTYPE(), one2.getContractType()
                            , one2.getDirection(), one2.getMarginLevel(), one2.getDefaultPeriod());
                }
            }
        }
        if (format == "") {
            return null;
        }
        format = format.substring(1, format.length());
        format = "[" + format + "]";
        return format;
    }
}
