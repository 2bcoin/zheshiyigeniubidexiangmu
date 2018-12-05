package com.github.misterchangray.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.annotation.PrintRunTime;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.vo.FMZConfigs;
import com.github.misterchangray.controller.common.vo.IPAndScript;
import com.github.misterchangray.controller.common.vo.PageResult;
import com.github.misterchangray.controller.common.vo.ScriptData;
import com.github.misterchangray.controller.dto.ConfigSignDTO;
import com.github.misterchangray.controller.dto.IPAndScriptDTO;
import com.github.misterchangray.controller.vo.RobotInfo;
import com.github.misterchangray.controller.vo.Slave;
import com.github.misterchangray.libs.okcoin.rest.MD5Util;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.jsexcutor.Global;
import com.github.misterchangray.service.jsexcutor.dto.ScriptDto;
import com.github.misterchangray.service.jsexcutor.util.MongoPageHelper;
import com.mongodb.util.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequestMapping("/v1/scriptData")
public class ScriptDataController {
    @Autowired
    MongoDbService mongoDbService;

    //分页的代码
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public MongoPageHelper mongoPageHelper() {
        return new MongoPageHelper(mongoTemplate);
    }

    org.slf4j.Logger logger = LoggerFactory.getLogger(Global.class);

    public static Map<String, Slave> ListIPAndScrpit = new HashMap<>(100);

    public static List<String> listStop = new ArrayList<>(100);

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
    public ResultSet<String> list(Integer pageSize, Integer pageNumber) {

        MapBuilder mapBuilder = MapBuilder.build();

        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "creatTime"));
        PageResult<ScriptData> pageResult = mongoPageHelper()
                .pageQuery(query, ScriptData.class, ScriptData.getCollectionName(), pageSize, pageNumber);
        List<ScriptData> list = pageResult.getList();

        mapBuilder.put("list", list);
        mapBuilder.put("pageResult", pageResult);

        return ResultSet.build(ResultEnum.SUCCESS).setData(mapBuilder);
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
//        if (ListIPAndScrpit.get(userIpAddr).getRobotInfoList().size() == 0) {
        List<RobotInfo> robotInfos = showStatus(userIpAddr);
        if (robotInfos == null) {
            System.out.println(ResultEnum.RESTOREFAILURE.getMsg());
        }
//        }
        return null;
    }

    /**
     * 新增
     *
     * @param ipAndScript
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/aouIP")
    public ResultSet<String> aouIP(@RequestBody IPAndScript ipAndScript) {
        try {
            if (StringUtils.isNotBlank(ipAndScript.getId())) {
                //修改
                Map<String, Object> t = new HashMap<>();
                t.put("id", ipAndScript.getId());
                Object obj = mongoDbService.findOne(t, IPAndScript.getCollectionName(), IPAndScript.class);
                IPAndScript ipAndScript1 = JSONUtils.json2obj(JSONUtils.obj2json(obj), IPAndScript.class);
                if (null != obj && obj instanceof IPAndScript) {
                    Map<String, Object> d = new HashMap<>();
                    d.put("id", ipAndScript.getId());
                    d.put("ip", ipAndScript.getIp());
                    d.put("title", ipAndScript.getTitle());
                    d.put("scriptID", ipAndScript.getScriptID());
                    d.put("scriptName", ipAndScript.getScriptName());
                    d.put("configurationName", ipAndScript.getConfigurationName());
                    d.put("configurationID", ipAndScript.getConfigurationID());
                    d.put("customConfig", ipAndScript.getCustomConfig());
                    d.put("robotStatus", ipAndScript1.getRobotStatus());
                    d.put("creatTime", ipAndScript1.getCreatTime());
                    d.put("updateTime", System.currentTimeMillis());
                    d.put("starTime", ipAndScript1.getStarTime());
                    mongoDbService.update(d, IPAndScript.getCollectionName(), IPAndScript.class);
                    return ResultSet.build(ResultEnum.SUCCESS);
                }
            } else {
                ipAndScript.setCreatTime(System.currentTimeMillis());
                ipAndScript.setUpdateTime(System.currentTimeMillis());
                ipAndScript.setStarTime(null);
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

                    //判断该托管则状态是否停止
                    IPAndScript ipAndScript = (IPAndScript) obj;
                    int i = showStatusOne(ipAndScript.getIp(), ipAndScript.getId());
                    if (i == 0) {
                        return ResultSet.build(ResultEnum.FAILURE).setMsg("请先停止托管者，再删除");
                    } else {
                        mongoDbService.remove(t, IPAndScript.getCollectionName(), IPAndScript.class);
                        return ResultSet.build(ResultEnum.SUCCESS);
                    }
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
    public ResultSet<String> listIP(Integer pageSize, Integer pageNumber, String scriptID) {
        MapBuilder mapBuilder = MapBuilder.build();
        //List<Object> list = mongoDbService.findAll(IPAndScript.collectionName, IPAndScriptDTO.class);
        Query query = new Query();
        if (StringUtils.isNotBlank(scriptID)) {
            query.addCriteria(Criteria.where("scriptID").is(scriptID));
        }
        query.with(new Sort(Sort.Direction.DESC, "creatTime"));
        PageResult<IPAndScriptDTO> pageResult = mongoPageHelper()
                .pageQuery(query, IPAndScriptDTO.class, IPAndScript.getCollectionName(), pageSize, pageNumber);
        List<IPAndScriptDTO> list = pageResult.getList();

        List<Object> scriptList = mongoDbService.findAll(ScriptData.collectionName, ScriptData.class);
        List<Object> fmzList = mongoDbService.findAll(FMZConfigs.collectionName, FMZConfigs.class);

        if (list.size() > 0) {
            for (IPAndScriptDTO ipAndScriptDTO : list) {
                List<String> configIds = ipAndScriptDTO.getConfigurationID();
                if (!CollectionUtils.isEmpty(configIds)) {
                    configIds.forEach(configId -> {
                        // 查询 configId 对应的配置 config
                        FMZConfigs config = findConfigById(fmzList, configId);
                        if (config != null) {
                            String ak = config.getAKEY();
                            String preSign = ak + config.getSKEY() + DateUtils.dateToStr(new Date(), "yyyyMMddHH");
                            String sign = MD5Util.getMD5String(preSign);
                            ConfigSignDTO configSignDTO = new ConfigSignDTO(ak, sign);
                            ipAndScriptDTO.getConfigSignList().add(configSignDTO);
                        }
                    });
                }

                Slave slave = ListIPAndScrpit.get(ipAndScriptDTO.getIp());
                if (slave != null) {
                    List<RobotInfo> robotInfoList = slave.getRobotInfoList();
                    ipAndScriptDTO.setRobotStatus(ResultEnum.HASSTOPPED.getMsg());
                    if (robotInfoList.size() > 0) {
                        for (RobotInfo robotInfo : robotInfoList) {
                            if (robotInfo.getId().equals(ipAndScriptDTO.getId())) {
                                ipAndScriptDTO.setRobotStatus(ResultEnum.HASSTARTED.getMsg());
                            }
                        }
                    }
                } else {
                    ipAndScriptDTO.setRobotStatus(ResultEnum.OFFLINE_ONE.getMsg());
                }
            }
        }
        mapBuilder.put("IPMap", ListIPAndScrpit);
        mapBuilder.put("list", list);
        mapBuilder.put("pageResult", pageResult);
        mapBuilder.put("scriptList", scriptList);
        mapBuilder.put("fmzList", fmzList);

        return ResultSet.build(ResultEnum.SUCCESS).setData(mapBuilder);
    }

    /**
     * 查询 configId 对应的配置 config
     *
     * @param configs
     * @param configId
     * @return
     */
    private FMZConfigs findConfigById(List<Object> configs, String configId) {
        if (CollectionUtils.isEmpty(configs)) {
            return null;
        }
        for (Object config : configs) {
            FMZConfigs fmzConfigs = (FMZConfigs) config;
            if (fmzConfigs != null && fmzConfigs.getId().equals(configId)) {
                return fmzConfigs;
            }
        }
        return null;
    }

    /**
     * 一键启动
     *
     * @param idArr
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/aKeyToStart")
    public ResultSet<String> aKeyToStart(@RequestParam(value = "idArr") List<String> idArr) throws IOException {
        List<Object> list = new ArrayList<>();
        ResultSet<String> result = null;
        for (String s : idArr) {
            result = start(s);
            if (result.getCode() != 0) {
                list.add(result.getCode());
            }
        }
        if (list.size() == 0) {
            return ResultSet.build(ResultEnum.SUCCESS);
        } else {
            return ResultSet.build(ResultEnum.FAILURE).setMsg(result.getMsg() + "，部分启动成功");
        }
    }

    /**
     * 根据托管者ID启动
     *
     * @param id
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/start")
    public ResultSet<String> start(@RequestParam(value = "id") String id) throws IOException {
        IPAndScript ipAndScript = getIPByID(id);
        if (StringUtils.isNotBlank(ipAndScript.getIp())) {
            //启动前查看该托管机器人是否启动
            int i = showStatusOne(ipAndScript.getIp(), id);
            if (i == 0) {
                return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.INSERVICE.getMsg());
            } else if (i == 1) {
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
                        return ResultSet.build(ResultEnum.FAILURE).setMsg("s==null");
                    }
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

                            //存储启动成功时间
                            ipAndScript.setStarTime(System.currentTimeMillis());
                            Map<String, Object> map2 = new HashMap<>();

                            map2.put("id", ipAndScript.getId());
                            map2.put("ip", ipAndScript.getIp());
                            map2.put("title", ipAndScript.getTitle());
                            map2.put("scriptID", ipAndScript.getScriptID());
                            map2.put("scriptName", ipAndScript.getScriptName());
                            map2.put("configurationName", ipAndScript.getConfigurationName());
                            map2.put("configurationID", ipAndScript.getConfigurationID());
                            map2.put("customConfig", ipAndScript.getCustomConfig());
                            map2.put("creatTime", ipAndScript.getCreatTime());
                            map2.put("updateTime", ipAndScript.getUpdateTime());
                            map2.put("starTime", System.currentTimeMillis());
                            mongoDbService.update(map2, IPAndScript.getCollectionName(), IPAndScript.class);

                            return ResultSet.build(ResultEnum.SUCCESS).setMsg(ResultEnum.STARTUPSUCCESS.getMsg());
                        } else {
                            if (ListIPAndScrpit.get(ipAndScript.getIp()) == null) {
                                return ResultSet.build(ResultEnum.FAILURE).setMsg("slave为空");
                            } else {
                                return ResultSet.build(ResultEnum.FAILURE).setMsg("RobotInfoList为空");
                            }
                        }
                    } else {
                        return ResultSet.build(ResultEnum.FAILURE).setMsg((String) map1.get("msg"));
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

    /**
     * 一键停止
     *
     * @param idArr
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/aKeyToStop")
    public ResultSet<String> aKeyToStop(@RequestParam(value = "idArr") List<String> idArr) throws IOException {
        List<Object> list = new ArrayList<>();
        ResultSet<String> result = null;
        for (String s : idArr) {
            result = shutdown(s, null);
            if (result.getCode() != 0) {
                list.add(result.getCode());
            }
        }
        if (list.size() == 0) {
            return ResultSet.build(ResultEnum.SUCCESS);
        } else {
            return ResultSet.build(ResultEnum.FAILURE).setMsg(result.getMsg() + "，部分停止失败");
        }
    }

    /**
     * 根据ID停止托管者
     * 根据传入的index判断停止方式，1为强制停止。null或""普通停止
     *
     * @param id
     * @param index
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/shutdown")
    public ResultSet<String> shutdown(@RequestParam(value = "id") String id, @RequestParam(value = "index") String index) throws IOException {

        String ip = getIPByID(id).getIp();
        Slave slave = ListIPAndScrpit.get(ip);
        if (slave == null) {
            logger.info("slave为空");
            return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.NOT_FOUND.getMsg());
        }
        Long start = System.currentTimeMillis();
        List<RobotInfo> ltmp = slave.getRobotInfoList();
        if (ltmp != null) {
            for (RobotInfo robotInfo : ltmp) {
                if (robotInfo.getId().equalsIgnoreCase(id)) {

                    Map<String, String> data = new HashMap<>();
                    data.put("p", robotInfo.getTid());
                    int t = (int) ((System.currentTimeMillis() / 1000) / 60);
                    data.put("s", CryptoUtils.encodeMD5(String.valueOf(t)));
                    String str = CryptoUtils.encodeBASE64(JSONUtils.obj2json(data));
                    Map<String, String> map = new HashMap<>();
                    map.put("data", str);
                    String url = null;
                    if (index == null || "".equals(index)) {
                        url = "http://" + getIPByID(id).getIp() + ":8080/common-core/v1/jsexcutor/shutdown";
                    } else if (index.equals("1")) {
                        url = "http://" + getIPByID(id).getIp() + ":8080/common-core/v1/jsexcutor/shutdownnow";
                    }
                    String s = HttpUtilManager.getInstance()
                            .requestHttpGet(url,
                                    map, MapBuilder.build());
                    logger.info("本次请求总共耗时{},url:{}", (System.currentTimeMillis() - start), url);
                    if (s == null) {
                        return ResultSet.build(ResultEnum.FAILURE).setMsg(ResultEnum.STOPFAILURECLIENTOFFLINE.getMsg());
                    }
                    Map map1 = JSONUtils.json2map(s);
                    Integer code = (Integer) map1.get("code");
                    if (code == 0) {
                        ltmp.remove(robotInfo);
                        slave.setRobotInfoList((CopyOnWriteArrayList) ltmp);
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

    /**
     * 根据IP恢复线程
     *
     * @param ip
     * @return
     * @throws IOException
     */
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
                robotInfo.setIpAndScriptName(node.get("script").get("ipAndScriptName").asText());
                list.add(robotInfo);
            }
        }
        if (0 <= list.size()) ListIPAndScrpit.get(ip).setRobotInfoList(list);
        return list;
    }

    /**
     * 根据托管者ID获取该托管者对应的IP
     *
     * @param id 托管者ID
     * @return
     */
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

    /**
     * 根据tid停止线程
     *
     * @param request
     * @param tId
     * @return
     */
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


    /**
     * 根据传入的akey和sign获取日志
     *
     * @param akey
     * @param sign
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/getLogByAkey")
    public ResultSet<List> getLogByAkey(@RequestParam(value = "akey") String akey, @RequestParam(value = "sign") String sign) {

        if (StringUtils.isNotBlank(akey)) {
            List<IPAndScriptDTO> listAll = new ArrayList<>();
            Query query = new Query();
            query.addCriteria(Criteria.where("AKEY").is(akey));
            List<FMZConfigs> list = mongoDbService.findList(query, FMZConfigs.getCollectionName(), FMZConfigs.class);
            String skey = list.get(0).getSKEY();
            String vilSign = MD5Util.getMD5String(akey + skey + DateUtils.dateToStr(new Date(), "yyyyMMddHH"));;
            if (vilSign.equals(sign)){
                for (FMZConfigs fmzConfigs : list) {
                    String fmzId = fmzConfigs.getId();
                    Query fmzQuery = new Query();
                    fmzQuery.addCriteria(Criteria.where("configurationID").in(fmzId));
                    List<IPAndScriptDTO> IPList = mongoDbService.findList(fmzQuery, IPAndScript.getCollectionName(), IPAndScriptDTO.class);
                    for (IPAndScriptDTO ipAndScript : IPList) {
                        listAll.add(ipAndScript);
                    }
                }
                //去重
                List<IPAndScriptDTO> list1 = removeDuplicate(listAll);


                List<Object> fmzList = mongoDbService.findAll(FMZConfigs.collectionName, FMZConfigs.class);
                for (IPAndScriptDTO ipAndScriptDTO : list1) {
                    List<String> configIds = ipAndScriptDTO.getConfigurationID();
                    if (!CollectionUtils.isEmpty(configIds)) {
                        configIds.forEach(configId -> {
                            // 查询 configId 对应的配置 config
                            FMZConfigs config = findConfigById(fmzList, configId);
                            if (config != null) {
                                String ak = config.getAKEY();
                                String preSign = ak + config.getSKEY() + DateUtils.dateToStr(new Date(), "yyyyMMddHH");
                                String signs = MD5Util.getMD5String(preSign);
                                ConfigSignDTO configSignDTO = new ConfigSignDTO(ak, signs);
                                ipAndScriptDTO.getConfigSignList().add(configSignDTO);
                            }
                        });
                    }
                }
                List<String> list2 = new ArrayList<>();
                for (IPAndScriptDTO ipAndScriptDTO : list1) {
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("id", ipAndScriptDTO.getId());
                    map1.put("ip", ipAndScriptDTO.getIp());
                    map1.put("scriptID", ipAndScriptDTO.getScriptID());
                    map1.put("configSignList", ipAndScriptDTO.getConfigSignList());
                    map1.put("clm",ipAndScriptDTO.getScriptName());
                    map1.put("tgzm",ipAndScriptDTO.getTitle());
                    String url = "http://" + ipAndScriptDTO.getIp() + ":8080/common-core/backsys/page/scriptRobotLog.html?data=";
                    list2.add(url + CryptoUtils.encodeBASE64(JSONUtils.obj2json(map1)));
                }
                return ResultSet.build(ResultEnum.FAILURE).setData(list2);
            }
            return ResultSet.build(ResultEnum.FAILURE).setMsg("签名认证失败");
        } else {
            return ResultSet.build(ResultEnum.FAILURE).setMsg("akey或sign为空");
        }
    }


    //去重
    public static List<IPAndScriptDTO> removeDuplicate(List<IPAndScriptDTO> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getId().equals(list.get(i).getId())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    /**
     * 托管者机器人的下拉搜索数据，根据策略搜索
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/getScriptList")
    public ResultSet<List> getScriptList() {

        MapBuilder mapBuilder = MapBuilder.build();

        List<Object> scriptList = mongoDbService.findAll(ScriptData.getCollectionName(), ScriptData.class);

        mapBuilder.put("scriptList", scriptList);

        return ResultSet.build(ResultEnum.SUCCESS).setData(mapBuilder);
    }
}
