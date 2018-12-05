package com.github.misterchangray.controller.common;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.utils.ListBuilder;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.vo.TAConfig;
import com.github.misterchangray.controller.common.vo.TAConfigs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


/**
 * 常量配置;
 * 配置币种
 * 或者为机构单独配置币种
 */
public class Const {
    //登陆二次验证
    public static String googleKey = "TKWRSOV4OVDDFYHV";
    //所有需要加载得币种
    public static List<String> types = new ArrayList<>(Arrays.asList(
            "btc_usdt","bch_usdt","eth_usdt","etc_usdt","ltc_usdt","eos_usdt","dash_usdt","iota_usdt","zec_usdt", "btm_usdt", "qtum_usdt",  "neo_usdt", "trx_usdt",
            "eos_eth","ltc_eth","etc_eth","dash_eth","iota_eth","bch_eth","zec_eth",  "btm_eth", "qtum_eth",  "neo_eth", "trx_eth",
            "bch_btc","eth_btc","ltc_btc","etc_btc","eos_btc","dash_btc","iota_btc","zec_btc",  "btm_btc", "qtum_btc",  "neo_btc", "trx_btc"
    ));

    //根据密钥生成google验证码
    public static Map<String,List<MapBuilder>> googleVerCodes = new HashMap(){{
        put(OrgsInfoEnum.Big1.getOrgName() ,
                ListBuilder.build().append(MapBuilder.build().add("misterchangray@hotmail.com", "6HZIXNCZOPA4GRXK"))
        );
    }};

    public static Map<OrgsInfoEnum, List<String>> orgsTypes = new HashMap<OrgsInfoEnum, List<String >>(){{
        for(OrgsInfoEnum orgsInfoEnum : OrgsInfoEnum.values()) {
            put(orgsInfoEnum, types);
        }
        List<String> bianTypes = this.get(OrgsInfoEnum.BiAn);
        bianTypes.add("bnb_usdt");
        bianTypes.add("bnb_btc");

        List<String> fcoinTypes = this.get(OrgsInfoEnum.Fcoin);
        bianTypes.add("ft_usdt");
        bianTypes.add("ft_btc");

        List<String> big1Types = this.get(OrgsInfoEnum.Big1);
        bianTypes.add("one_usdt");
        bianTypes.add("add_eos");

    }};


    //保存每个交易所币种对象的价格精度,数量精度,最低下单数量,
    public static List<TAConfig> typeInfo = new ArrayList<TAConfig>() {{
        //huobi
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "eos_usdt", 4, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "btc_usdt", 2, 4,0.0001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "eth_usdt", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "ltc_usdt", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "etc_usdt", 4, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "dash_usdt", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "iota_usdt", 4, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "bch_usdt", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "btm_usdt", 2, 4,0.1));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "qtum_usdt", 2, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "neo_usdt", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "trx_usdt", 6, 2,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "eos_eth", 8, 2,0.1));
//        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "ltc_eth", 2, 4,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "etc_eth", 2, 4,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "dash_eth", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "iota_eth", 6, 4,0.01));
//        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "bch_eth", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "btm_eth", 8, 2,0.1));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "qtum_eth", 6, 4,0.01));
//        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "neo_eth", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "trx_eth", 8, 2,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "ltc_btc", 6, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "eos_btc", 8, 2,0.1));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "etc_btc", 6, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "dash_btc", 6, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "iota_btc", 8, 2,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "bch_btc", 6, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "btm_btc", 8, 2,0.1));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "qtum_btc", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "neo_btc", 6, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.HuoBi, "trx_btc", 10, 2,1.0));


        //bitfinex
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "eos_usdt", 4, 8,2.0));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "btc_usdt", 4, 8,0.002));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "eth_usdt", 4, 8,0.04));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "ltc_usdt", 4, 8,0.2));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "etc_usdt", 4, 8,0.8));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "dash_usdt", 4, 8,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "iota_usdt", 4, 8,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "bch_usdt", 4, 8,0.02));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "btm_usdt", 8, 2,0.1));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "qtum_usdt", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "neo_usdt", 8, 8,0.6));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "trx_usdt", 8, 8,550.0));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "eos_eth", 4, 8,2.0));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "ltc_eth", 2, 4,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "etc_eth", 2, 4,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "dash_eth", 2, 4,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "iota_eth", 6, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "bch_eth", 4, 8,0.02));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "btm_eth", 8, 2,0.1));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "qtum_eth", 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "neo_eth", 8, 8,0.6));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "trx_eth", 8, 8,550.0));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "ltc_btc", 8, 8,0.2));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "eos_btc", 8, 8,2.0));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "etc_btc", 8, 8,0.8));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "dash_btc", 8, 8,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "iota_btc", 8, 8,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "bch_btc", 8, 8,0.002));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "btm_btc", 8, 2,0.1));
//        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "qtum_btc, 2, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "neo_btc", 8, 8,0.6));
        this.add(TAConfigs.build(OrgsInfoEnum.Bitfinex, "trx_btc", 8, 8,550.0));



        //okex
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "eos_usdt", 4, 4,0.1));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "btc_usdt", 4, 8,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "eth_usdt", 4, 6,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "ltc_usdt", 4, 6,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "etc_usdt", 4, 5,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "dash_usdt", 4, 6,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "iota_usdt", 4, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "bch_usdt", 4, 8,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "btm_usdt", 4, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "qtum_usdt", 2, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "neo_usdt", 4, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "trx_usdt", 6, 2,10.0));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "eos_eth", 8, 4,0.1));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "ltc_eth", 8, 6,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "etc_eth", 8, 5,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "dash_eth", 8, 6,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "iota_eth", 8, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "bch_eth", 8, 8,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "btm_eth", 8, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "qtum_eth", 8, 4,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "neo_eth", 4, 4,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "trx_eth", 6, 2,10.0));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "ltc_btc", 8, 6,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "eos_btc", 8, 4,0.1));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "etc_btc", 8, 5,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "dash_btc", 8, 6,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "iota_btc", 8, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "bch_btc", 8, 8,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "btm_btc", 8, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "qtum_btc", 5, 5,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "neo_btc", 8, 5,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.OKEX, "trx_btc", 8, 2,10.0));



        //BIAN 币安得最小下单数量是根据最低成交总额反推得
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "eos_usdt", 4, 2,2.2));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "btc_usdt", 2, 6,0.002));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "eth_usdt", 2, 5,0.06));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "ltc_usdt", 2, 5,0.2));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "etc_usdt", 4, 2,1.0));
//        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "dash_usdt", 4, 6,0.001));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "iota_usdt", 4, 2,20.0));
//        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "bch_usdt", 4, 8,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "btm_usdt", 8, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "qtum_usdt", 4, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "neo_usdt", 3, 3,0.5));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "trx_usdt", 5, 1,200.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "eos_eth", 6, 2,0.5));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "ltc_eth", 5, 3,0.05));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "etc_eth", 6, 2,0.02));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "dash_eth", 5, 3,0.01));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "iota_eth", 8, 0,1.0));
//        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "bch_eth", 8, 8,0.001));
//        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "btm_eth", 8, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "qtum_eth", 6, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "neo_eth", 3, 3,0.5));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "trx_eth", 5, 1,200.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "ltc_btc", 6, 2,0.2));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "eos_btc", 7, 2,2.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "etc_btc", 6, 2,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "dash_btc", 6, 3,0.04));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "iota_btc", 8, 0,15.0));
//        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "bch_btc", 8, 8,15.0));
//        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "btm_btc", 8, 3,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "qtum_btc", 6, 2,1.0));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "neo_btc", 6, 3,0.5));
        this.add(TAConfigs.build(OrgsInfoEnum.BiAn, "trx_btc", 8, 0,200.0));


    }};


    public static List<TAConfigs> taConfig = new ArrayList<TAConfigs>(){{ }};

    //    加载在内存中得数据最多缓存de 条数
    public static int maxCacheTypes = 50;






}
