package com.github.misterchangray.service.platform.vo;



import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DepthVo {
   private Map<String, List<Depth>> depthCache = new ConcurrentHashMap();
   private Long updateTime = System.currentTimeMillis();
   private JsonNode Info; //	交易所返回的原始结构

    public static String bids = "bids"; //买
    public static String asks = "asks"; //卖

    public JsonNode getInfo() {
        return Info;
    }

    public void setInfo(JsonNode info) {
        Info = info;
    }

    public void setAsks(List<Depth> depths) {
       Collections.sort(depths);
       if(depths.size() > 100) {
           depths = depths.subList(0,100);
       }
       depthCache.put(asks, depths);
   }
   public void setBids(List<Depth> depths) {
       Collections.sort(depths);
       Collections.reverse(depths);
       if(depths.size() > 100) {
           depths = depths.subList(0,100);
       }
       depthCache.put(bids, depths);
   }


    public List<Depth>  getAsks() {
        return depthCache.get(asks);
    }
    public  List<Depth> getBids() {
        return depthCache.get(bids);
    }


    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }


}

