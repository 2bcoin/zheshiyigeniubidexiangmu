package com.github.misterchangray.controller;


import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.utils.ListBuilder;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1/init")
public class InitController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "allOrgs")
    public ResultSet<String> allPlatform() {
        MapBuilder data = MapBuilder.build();
        for(OrgsInfoEnum org : Const.orgsTypes.keySet()) {
           data.add(org.getOrgName(), org.getOrgName());
        }
        return ResultSet.build().setData(data);
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "allTypes")
    public ResultSet<String> allTypes() {
        MapBuilder data = MapBuilder.build();
        ListBuilder listBuilder = new ListBuilder();
        for(String type : Const.types) {
            listBuilder.append(type);
        }
        return ResultSet.build().setData(listBuilder);
    }
}
