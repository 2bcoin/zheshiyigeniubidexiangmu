package com.github.misterchangray.controller.common.vo;

import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;

import java.util.ArrayList;
import java.util.List;

public class TAConfigs {
    public static String collectionName = "taConfigs";
    private List<TAConfig> taConfigList;
    private Double lv;//成交利率；默认请使用Const.default_fv
    private String id;
    private Long insertDate;//添加时间

    @Override
    public String toString() {
        return "TAConfigs{" +
                "taConfigList=" + taConfigList +
                ", lv=" + lv +
                ", id='" + id + '\'' +
                ", insertDate=" + insertDate +
                '}';
    }


    //三角 交易对排序
    public void sortTa() {
        List<TAConfig> tmp = new ArrayList<>();
        if(this.taConfigList.size() == 3) {
           for(int i=0; i<this.taConfigList.size(); i++) {
               for(int j=0; j<this.taConfigList.size(); j++) {
                   if(j == i) continue;
                   for(int k=0; k<this.taConfigList.size(); k++) {
                       if(k == j || k == i) continue;
                       TAConfig tmp1 = this.taConfigList.get(i);
                       TAConfig tmp2 = this.taConfigList.get(j);
                       TAConfig tmp3 = this.taConfigList.get(k);

                       String[] temp1 = tmp1.getCoin().split("[_-]");
                       String[] temp2 = tmp2.getCoin().split("[_-]");
                       String[] temp3 = tmp3.getCoin().split("[_-]");
                       if(temp1[0].equals(temp3[0]) && temp2[0].equals(temp3[1])) {
                           tmp.add(tmp1);
                           tmp.add(tmp2);
                           tmp.add(tmp3);
                           this.taConfigList = tmp;
                           return;
                       }
                   }
               }
           }
        }

    }

    //为增加的条目 设置默认下单精度和下单数量
    public void resetTaInfo() {
        for(TAConfig taConfig :this.taConfigList) {
            for(TAConfig taConfig1 : Const.typeInfo) {
                if(taConfig.getCoin().equals(taConfig1.getCoin())
                        && taConfig.getOrg() == taConfig1.getOrg()) {
                    taConfig.setAmountPrecision(taConfig1.getAmountPrecision());
                    taConfig.setMinAmount(taConfig1.getMinAmount());
                    taConfig.setPricePrecision(taConfig1.getPricePrecision());
                }
            }
        }
    }


    public Long getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Long insertDate) {
        this.insertDate = insertDate;
    }

    public List<TAConfig> getTaConfigList() {
        return taConfigList;
    }

    public void setTaConfigList(List<TAConfig> taConfigList) {
        this.taConfigList = taConfigList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static TAConfig build(OrgsInfoEnum org, String coin, int price, int amount ,Double minAmount) {
        return new TAConfig(org, coin, price, amount, minAmount);
    }

    public TAConfigs(List<TAConfig> taConfigList, Double lv) {
        this.taConfigList = taConfigList;
        this.lv = lv;
    }

    public static TAConfigs build(TAConfig... args) {
        if(args.length > 0) {
            TAConfigs taConfigs = new TAConfigs();
            taConfigs.insertDate = System.currentTimeMillis();

            taConfigs.taConfigList = new ArrayList<>();
            for(TAConfig taConfig : args) {
                taConfigs.taConfigList.add(taConfig);
            }

            return taConfigs;
        }
        return null;
    }

    public int size() {
        return taConfigList.size();
    }

    public TAConfig getTaConfig(int i) {
        return taConfigList.get(i);
    }

    public TAConfigs() {
        this.insertDate = System.currentTimeMillis();
    }
    public static TAConfigs build() {
        TAConfigs taConfigs = new TAConfigs();
        taConfigs.insertDate = System.currentTimeMillis();

        return taConfigs;
    }


    public static TAConfigs build(Double lv) {
        TAConfigs taConfigs = new TAConfigs();
        taConfigs.setLv(lv);
        taConfigs.setInsertDate(System.currentTimeMillis());
        return taConfigs;
    }

    public  TAConfigs add(TAConfig arg) {
        if(null == this.taConfigList) {
            this.taConfigList = new ArrayList<>();
        }
        if(null != arg) {
           this.taConfigList.add(arg);
            return this;
        }
        return this;
    }

    public Double getLv() {
        return lv;
    }

    public TAConfigs setLv(Double lv) {
        this.lv = lv;
        return this;
    }
}


