package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.annotation.Authorization;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.service.business.QiXianService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * 期现套利
 */
@Controller
@Authorization
@RequestMapping("/v1/qixian")
public class QiXianController {

    public static void main(String aa[]) {
        List<String> a = new ArrayList<>();
        a.add("1");
        System.out.println(a.get(a.size()));
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/charData")
    public ResultSet<String> charData() {
        return ResultSet.build(ResultEnum.SUCCESS).setData(QiXianService.charData);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/charDataLast")
    public ResultSet<String> charData2() {
        if(5 != QiXianService.charData.size()) return ResultSet.build(ResultEnum.FAILURE);

        ArrayDeque<Double> basb = (ArrayDeque<Double>) QiXianService.charData.get("basb");
        ArrayDeque<Double> sabb = (ArrayDeque<Double>) QiXianService.charData.get("sabb");
        List<Double> middle = (List<Double>) QiXianService.charData.get("middle");
        List<Double> middleUp = (List<Double>) QiXianService.charData.get("middleUp");
        List<Double> middleDown = (List<Double>) QiXianService.charData.get("middleDown");

        return ResultSet.build(ResultEnum.SUCCESS).setData(MapBuilder.build()
                .add("basb", basb.getLast())
                .add("sabb", sabb.getLast())
                .add("middle", middle.get(middle.size() - 1))
                .add("middleUp", middleUp.get(middleUp.size() - 1))
                .add("middleDown", middleDown.get(middleDown.size() - 1))
        );
    }
}
