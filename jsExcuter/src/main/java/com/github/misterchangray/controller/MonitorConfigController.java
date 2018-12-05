package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.CommonUtils;
import com.github.misterchangray.controller.common.vo.MonitorConfig;
import com.github.misterchangray.controller.common.vo.MonitorUserInfo;
import com.github.misterchangray.service.common.MongoDbService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监控配置
 */
@RestController
@RequestMapping("/v1/monitorConfig")
public class MonitorConfigController {
    @Autowired
    private MongoDbService mongoDbService;

    /**
     * 获取配置的用户
     * @return
     */
    @RequestMapping("/listUser")
    public ResultSet<String> listUser() {
        List<MonitorUserInfo> userInfos = mongoDbService.list(MonitorUserInfo.collectionName, MonitorUserInfo.class);
        return ResultSet.build(ResultEnum.SUCCESS).setData(userInfos);
    }

    /**
     * 获取监控配置
     * @return
     */
    @RequestMapping("/listMonitorConfig")
    public ResultSet<String> listMonitorConfig() {
        List<MonitorConfig> userInfos = mongoDbService.list(MonitorConfig.collectionName, MonitorConfig.class);
        return ResultSet.build(ResultEnum.SUCCESS).setData(userInfos);
    }

    /**
     * 新增或更新用户
     * @param userInfo
     * @return
     */
    @RequestMapping("/addOrUpdateUser")
    public ResultSet<String> addUser(@RequestBody MonitorUserInfo userInfo) {
        if (StringUtils.isBlank(userInfo.getId())) {
            userInfo.setId(null);
            userInfo.setEnabled(true);
            userInfo.setDeleted(false);
            userInfo.setUpdateTime(System.currentTimeMillis());
            userInfo.setCreateTime(System.currentTimeMillis());
            mongoDbService.insert(userInfo, MonitorUserInfo.collectionName);
        } else {
            userInfo.setUpdateTime(System.currentTimeMillis());
            Map<String, Object> update = CommonUtils.objectToMap(userInfo, CommonUtils.getNullPropertyNames(userInfo));
            mongoDbService.update(update, MonitorUserInfo.collectionName, MonitorUserInfo.class);
        }
        return ResultSet.build(ResultEnum.SUCCESS);
    }

    /**
     * 新增或更新监控配置
     * @param monitorConfig
     * @return
     */
    @RequestMapping("/addOrUpdateMonitorConfig")
    public ResultSet<String> addMonitorConfig(@RequestBody MonitorConfig monitorConfig) {
        if (StringUtils.isBlank(monitorConfig.getId())) {
            monitorConfig.setId(null);
            monitorConfig.setEnabled(true);
            monitorConfig.setDeleted(false);
            monitorConfig.setUpdateTime(System.currentTimeMillis());
            monitorConfig.setCreateTime(System.currentTimeMillis());
            mongoDbService.insert(monitorConfig, MonitorConfig.collectionName);
        } else {
            monitorConfig.setUpdateTime(System.currentTimeMillis());
            Map<String, Object> update = CommonUtils.objectToMap(monitorConfig, CommonUtils.getNullPropertyNames(monitorConfig));
            mongoDbService.update(update, MonitorConfig.collectionName, MonitorConfig.class);
        }
        return ResultSet.build(ResultEnum.SUCCESS);

    }

    /**
     * 删除监控配置
     * @param id
     * @return
     */
    @RequestMapping("/deleteMonitorConfig")
    public ResultSet<String> deleteMonitorConfig(String id) {
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> delete = new HashMap<>();
            delete.put("id", id);
            mongoDbService.remove(delete, MonitorConfig.collectionName, MonitorConfig.class);
        }
        return ResultSet.build(ResultEnum.SUCCESS);
    }

    /**
     * 删除监控配置通知用户
     * @param id
     * @return
     */
    @RequestMapping("/deleteUserInfo")
    public ResultSet<String> deleteUserInfo(String id) {
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> delete = new HashMap<>();
            delete.put("id", id);
            mongoDbService.remove(delete, MonitorUserInfo.collectionName, MonitorUserInfo.class);
        }
        return ResultSet.build(ResultEnum.SUCCESS);
    }

}
