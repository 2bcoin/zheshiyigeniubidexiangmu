package com.github.misterchangray.common.enums;

import com.github.misterchangray.controller.common.OrgsInfoEnum;
import org.omg.SendingContext.RunTime;

public enum CoinOtc {
    btc, ltc, eth, etc, bch, eos, xrp, xbt;


    public static CoinOtc getCoin(String name) {
        for(CoinOtc coinOtc : CoinOtc.values()) {
            if(coinOtc.toString().equalsIgnoreCase(name)){
                return coinOtc;
            }
        }
        throw new RuntimeException("交易对名称错误，只能为[btc, ltc, eth, etc, bch, eos, xrp, xbt]");
    }



    public static String[] getCoins() {
        String[] types = new String[CoinOtc.values().length];
        int i=0;
        for(CoinOtc coinOtc : CoinOtc.values()) {
            types[i] = coinOtc.toString();
            i++;
        }
        return  types;
    }
}
