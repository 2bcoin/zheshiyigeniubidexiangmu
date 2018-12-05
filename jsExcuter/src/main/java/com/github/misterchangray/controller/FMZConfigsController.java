package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.vo.FMZConfigs;
import com.github.misterchangray.controller.common.vo.PageResult;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.jsexcutor.util.MongoPageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/v1/fmz")
public class FMZConfigsController {

    @Autowired
    MongoDbService mongoDbService;

    //分页的代码
    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public MongoPageHelper mongoPageHelper() {
        return new MongoPageHelper(mongoTemplate);
    }

    /**
     * 新增
     *
     * @param fmzConfigs
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/add")
    public ResultSet<String> addIP(@RequestBody FMZConfigs fmzConfigs) {
        try {
            fmzConfigs.setCreatTime(System.currentTimeMillis());
            mongoDbService.insert(fmzConfigs, FMZConfigs.getCollectionName());
            return ResultSet.build(ResultEnum.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/findOne")
    public ResultSet<String> findOne(@RequestParam(value = "id") String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                //判断数据是否存在
                Map<String, Object> t = new HashMap<>();
                t.put("id", id);
                Object obj = mongoDbService.findOne(t, FMZConfigs.getCollectionName(), FMZConfigs.class);
                if (null != obj && obj instanceof FMZConfigs) {
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
    @RequestMapping(method = RequestMethod.GET, value = "/del")
    public ResultSet<String> del(@RequestParam(value = "id") String id) {

        try {
            if (StringUtils.isNotBlank(id)) {
                //判断数据是否存在
                Map<String, Object> t = new HashMap<>();
                t.put("id", id);
                Object obj = mongoDbService.findOne(t, FMZConfigs.getCollectionName(), FMZConfigs.class);
                if (null != obj && obj instanceof FMZConfigs) {
                    mongoDbService.remove(t, FMZConfigs.getCollectionName(), FMZConfigs.class);
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
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResultSet<String> listIP(Integer pageSize, Integer pageNumber) {

        MapBuilder mapBuilder = MapBuilder.build();
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "creatTime"));
        PageResult<FMZConfigs> pageResult = mongoPageHelper()
                .pageQuery(query, FMZConfigs.class, FMZConfigs.getCollectionName(), pageSize, pageNumber);
        List<FMZConfigs> list = pageResult.getList();

        mapBuilder.put("list", list);
        mapBuilder.put("pageResult", pageResult);

        return ResultSet.build(ResultEnum.SUCCESS).setData(mapBuilder);
    }
}
