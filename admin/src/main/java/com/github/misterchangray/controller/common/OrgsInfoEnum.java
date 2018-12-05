package com.github.misterchangray.controller.common;

/**
 * 配置机构信息;
 * 机构名称;socket地址;rest地址;Secret;
 */
public enum OrgsInfoEnum {
    Fcoin("Fcoin", "wss://api.fcoin.com/v2/ws", null, "https://api.fcoin.com/v2/", null),//线上配置
    HuoBi("HuoBi", "wss://api.huobi.pro/ws", null, "https://api.huobi.pro/market|https://api.huobi.pro/v1",  null),//线上配置
    Big1("Big1", null, null, "https://big.one/api/v2/",  null),//线上配置
    OKEX("OKEX", "wss://real.okex.com:10441/websocket", "wss://real.okex.com:10440/websocket/okexapi", "https://www.okex.com", "https://www.okex.com"),
    Bitfinex("Bitfinex", "wss://api.bitfinex.com/ws/2", null, "https://api.bitfinex.com/", null),
    BiAn("BiAn", "wss://stream.binance.com:9443/ws", null,null, null),


    Bit_z("Bit_z", null, null, "https://apiv2.bitz.com/", null),
    OTCBTC("OTCBTC", null, null,  null, null),
    Zb_site("Zb_site", "wss://api.zb.com:9999/websocket", null, null, null),
    Bibox("Bibox", null, null, "https://api.bibox.com", null),
    DigiFinex("DigiFinex", null, null, "https://openapi.digifinex.com", null),
    CoinbasePro("CoinbasePro", "wss://ws-feed.pro.coinbase.com",null, null, null),
    Bitmex("Bitmex", null, "wss://www.bitmex.com/realtime", null, "https://www.bitmex.com/"),
    Kraken("Kraken", null, null, "https://api.kraken.com", null);;

    private String orgName;
    private String orgSocketUrlSpot;
    private String orgSocketUrlOtc;

    private String orgRestUrlSpot;
    private String orgRestUrlOtc;


    OrgsInfoEnum(String orgName, String orgSocketUrlSpot, String orgSocketUrlOtc, String orgRestUrlSpot, String orgRestUrlOtc){
        this.orgName = orgName;
        this.orgSocketUrlOtc = orgSocketUrlOtc;
        this.orgSocketUrlSpot = orgSocketUrlSpot;
        this.orgRestUrlOtc = orgRestUrlOtc;
        this.orgRestUrlSpot = orgRestUrlSpot;
    }


    public static OrgsInfoEnum getOrgsInfoEnumByName(String orgName) {
        for(OrgsInfoEnum tmp : OrgsInfoEnum.values()) {
            if(orgName.equals(tmp.getOrgName())) {
                return tmp;
            }
        }
        throw new RuntimeException("交易机构名称错误，只能为[Fcoin, HuoBi, Big1, OKEX, Bitfinex, BiAn, Bitmex, CoinbasePro, Bit_z, ,OTCBTC, Zb_site, Bibox, DigiFinex]");
    }


    public String getOrgSocketUrlSpot() {
        return orgSocketUrlSpot;
    }

    public void setOrgSocketUrlSpot(String orgSocketUrlSpot) {
        this.orgSocketUrlSpot = orgSocketUrlSpot;
    }

    public String getOrgSocketUrlOtc() {
        return orgSocketUrlOtc;
    }

    public void setOrgSocketUrlOtc(String orgSocketUrlOtc) {
        this.orgSocketUrlOtc = orgSocketUrlOtc;
    }

    public String getOrgRestUrlSpot() {
        return orgRestUrlSpot;
    }

    public void setOrgRestUrlSpot(String orgRestUrlSpot) {
        this.orgRestUrlSpot = orgRestUrlSpot;
    }

    public String getOrgRestUrlOtc() {
        return orgRestUrlOtc;
    }

    public void setOrgRestUrlOtc(String orgRestUrlOtc) {
        this.orgRestUrlOtc = orgRestUrlOtc;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return orgName;
    }
}
