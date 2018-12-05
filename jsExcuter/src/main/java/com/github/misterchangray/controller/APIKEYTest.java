package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.service.platform.BitMexService;
import com.github.misterchangray.service.platform.OKEXService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * api key测试是否能正常下单
 */
@Controller
@RequestMapping("/v1/atest")
public class APIKEYTest {

    @Autowired
    private OKEXService okexService;
    @Autowired
    private BitMexService bitMexService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/akey")
    public ResultSet<String> keyTest(@RequestBody() Map<String, String> params) throws Exception {
        BigDecimal number = new BigDecimal(0);
        int value = 1;
        number = BigDecimal.valueOf((int) value);

        String akey = params.get("akey");
        String skey = params.get("skey");
        String bourse = params.get("bourse");
        String coin = params.get("coin");
        String contract = params.get("contract");
        CoinOtc e = CoinOtc.getCoin(coin);
        if (bourse.equals("OKEX")) {
            ResultSet<String> msg = null;
            if (e != null) {
                msg = okexService.futureOrder(e, contract, "duo", 10, null, number, akey, skey);
                if (StringUtils.isNotBlank(msg.getData())) {
                    Thread.sleep(1000);
                    ResultSet<String> cancelMsg = okexService.futureOrder(e, contract, "pingduo", 10, null, number, akey, skey);
                    if (cancelMsg.getCode().equals(0)) {
                        return ResultSet.build(ResultEnum.SUCCESS).setMsg("OKEX:下单成功,此api有效");
                    }
                } else {
                    return ResultSet.build(ResultEnum.FAILURE).setMsg("OKEX:下单失败" + msg.getMsg());
                }
            } else {
                return ResultSet.build(ResultEnum.FAILURE).setMsg("OKEX:请输入正确的币种");
            }
        } else if (bourse.equals("Bitmex")) {
            if (e != null) {
                ResultSet<String> msg = bitMexService.futureOrder(e, contract, "duo", 10, null, number, akey, skey);
                if (StringUtils.isNotBlank(msg.getData())) {
                    ResultSet<String> cancelMsg = bitMexService.futureOrder(e, contract, "pingduo", 10, null, number, akey, skey);
                    if (cancelMsg.getCode().equals(0)) {
                        return ResultSet.build(ResultEnum.SUCCESS).setMsg("Bitmex:下单成功,此api有效");
                    }
                } else {
                    return ResultSet.build(ResultEnum.FAILURE).setMsg("Bitmex:下单失败" + msg.getMsg());
                }
            } else {
                return ResultSet.build(ResultEnum.FAILURE).setMsg("Bitmex:请输入正确的币种");
            }
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }
}
