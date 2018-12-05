package com.github.misterchangray.common.enums;

import com.github.misterchangray.common.init.Init;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

public enum CoinSpot {
    btc_usdt,bch_usdt,eth_usdt,etc_usdt,ltc_usdt,eos_usdt,dash_usdt,iota_usdt,zec_usdt, btm_usdt, qtum_usdt,  neo_usdt, trx_usdt,
    eos_eth,ltc_eth,etc_eth,dash_eth,iota_eth,bch_eth,zec_eth,  btm_eth, qtum_eth,  neo_eth, trx_eth,
    bch_btc,eth_btc,ltc_btc,etc_btc,eos_btc,dash_btc,iota_btc,zec_btc,  btm_btc, qtum_btc,  neo_btc, trx_btc;


    public static CoinSpot getCoin(String name) {
        name = name.replaceAll("[-_/\\\\]", "");
        for(CoinSpot coinSpot : CoinSpot.values()) {
            if(coinSpot.toString().equalsIgnoreCase(name) ||
                    coinSpot.toString().replace("_","").equalsIgnoreCase(name)){
                return coinSpot;
            }
        }
        throw new RuntimeException("交易对名称错误，只能为[" +  StringUtils.join(getCoins(),",") + "]");
    }

    public static String[] getCoins() {
        String[] types = new String[CoinSpot.values().length];
        int i=0;
        for(CoinSpot coinOtc : CoinSpot.values()) {
            types[i] = coinOtc.toString();
            i++;
        }
        return  types;
    }

}
