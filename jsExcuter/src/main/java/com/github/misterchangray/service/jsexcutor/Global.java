package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.jsexcutor.dto.ScriptDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class Global {
    //储存全局常量包括起始的配置信息，大概包含以下的一些配置
    /**
     * __AKEY:api key
     * __SKEY:secrt key
     * __ORG: 平台 例如：火币
     * __COIN:交易币种 例如:btc_usdt
     * __TYPE:交易类型 期货otc,现货spot
     * __ContractType: 合约类型
     * __Direction:下单方向
     * __MarginLevel:杠杆大小
     * __Command 执行的命令
     * __Precision 下单精度;使用;号分割,PricePrecision;AmountPrecision
     */
    public Map<String, String> currentConfigs = new HashMap<>();
    public ScriptDto scriptDto;


    @Autowired
    ApplicationContext applicationContext;

    //此map用于映射交易平台名称到交易服务具体实现得类名
    private static Map<String, String> maps = new HashMap<String, String>(){{
        this.put(OrgsInfoEnum.BiAn.getOrgName(), "BiAnService");
        this.put(OrgsInfoEnum.Bitfinex.getOrgName(), "BitfinexService");
        this.put(OrgsInfoEnum.Bitmex.getOrgName(), "BitMexService");
        this.put(OrgsInfoEnum.HuoBi.getOrgName(), "HuoBiService");
        this.put(OrgsInfoEnum.Fcoin.getOrgName(), "FcoinService");
        this.put(OrgsInfoEnum.OKEX.getOrgName(), "OKEXService");
    }};




    public void sleep(int time) throws InterruptedException{
        if(time < Integer.MAX_VALUE) {
            Thread.sleep(time);
        }
    }

    //////////////////////////////全局配置便捷访问函数
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private String trimInvalid(String s) {
        if(null == s) return null;
        if(s.startsWith("\"") && s.endsWith("\"")) {
            return s.replaceAll("^\"|\"$", "");
        }
        return s;
    }
    public String getConfig__Coin() {
        return currentConfigs.get("__COIN");
    }

    public String getConfig__Direction() {
        //Direction可以取buy, closebuy, sell, closesell四个参数
        String key = currentConfigs.get("__Direction");
        if(key.equalsIgnoreCase("buy")) {
            return "duo";
        } else if(key.equalsIgnoreCase("closebuy")) {
            return "pingduo";
        } else if(key.equalsIgnoreCase("sell")) {
            return "kong";
        } else if(key.equalsIgnoreCase("closesell")) {
            return "pingkong";
        }
        return null;
    }

    public boolean canTrade() {
        if(null != getConfig__AKEY() && null != getConfig__SKEY()) return false;
        if(getConfig__AKEY().length() > 5 && getConfig__SKEY().length() > 5) return true;
        return false;
    }


    public Integer getConfig__MarginLevel() {
        String key =  String.valueOf(currentConfigs.get("__MarginLevel"));
        return Integer.parseInt(key);
    }

    public String getConfig__Org() {
        return  String.valueOf(currentConfigs.get("__ORG"));
    }

    /**
     * 根据 __Precision 配置格式化价格精度
     * @param price
     * @return
     */
    public Double formatPrice(Double price) {
        String key = currentConfigs.get("__Precision");
        if(null != key) {
            int w = Integer.parseInt(key.split(";")[0]);
            int t = (int)(Math.pow(10, w));
            double target = ((int)(price.doubleValue() * t)) / t;
            return target;
        }
        return price;
    }

    /**
     * 根据 __Precision 配置格式化数量精度
     * @param amount
     * @return
     */
    public Double formatAmount(Double amount) {
        String  key =  currentConfigs.get("__Precision");
        if(null != key) {
            int w = Integer.parseInt(key.split(";")[1]);
            int t = (int)(Math.pow(10, w));
            double target = ((int)(amount.doubleValue() * t)) / t;
            return target;
        }
        return amount;
    }
    public Integer getConfig__AmountPrecision() {
        String key =  currentConfigs.get("__Precision");
        return Integer.parseInt(key);
    }
    public String getConfig__SKEY() {
        String key =  currentConfigs.get("__SKEY");
        return key;
    }

    public String getConfig__AKEY() {
        String key =  currentConfigs.get("__AKEY");
        return key;
    }

    public String getConfig__ContractType() {
        return  currentConfigs.get("__ContractType");
    }

    public boolean isConfig__Spot() {
        if( "otc".equalsIgnoreCase(currentConfigs.get("__TYPE"))) {
            return false;
        }
        return true;
    }


    public boolean isConfig__Otc() {
        if("otc".equalsIgnoreCase(currentConfigs.get("__TYPE"))) {
            return true;
        }
        return false;
    }

    public BaseService getConfig__Service() {
        String name = currentConfigs.get("__ORG");
        if(null == name) return null;
        name = maps.get(name);
        if(null != name) {
            Map<String, BaseService> map = applicationContext.getBeansOfType(BaseService.class);
            BaseService baseService = null;
            for(String key : map.keySet()) {
                if(key.equalsIgnoreCase(name)) {
                    baseService = map.get(key);
                    break;
                }
            }
            return baseService;
        }
        return null;
    }
}
