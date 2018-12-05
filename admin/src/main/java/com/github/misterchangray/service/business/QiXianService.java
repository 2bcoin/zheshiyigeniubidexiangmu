package com.github.misterchangray.service.business;

import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.utils.BigDecimalUtils;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.platform.BitMexService;
import com.github.misterchangray.service.platform.OKEXService;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Kline;
import org.knowm.xchange.okcoin.dto.marketdata.OkCoinTickerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QiXianService extends BaseService {
    private static int size = 600;
    public static Map<String, Object> charData = new HashMap<>();
    private static int depth = 1; //取几价

    @Autowired
    BitMexService bitMexService;
    @Autowired
    OKEXService okexService;

    public void statics() {

    }



    public void scanerDepth() {
        DepthVo bitmexOrderBook = getBitmexDepth();
        DepthVo okexOrderBook = getOkexDepth();
        if(null == bitmexOrderBook || null == okexOrderBook) return;;

        Object basbTmp =  charData.get("basb");
        Object sabbTmp =  charData.get("sabb");
        if(null == basbTmp) basbTmp = new ArrayDeque(size);
        if(null == sabbTmp) sabbTmp = new ArrayDeque(size);

        ArrayDeque<Double> basb  = (ArrayDeque<Double>) basbTmp;
        ArrayDeque<Double> sabb = (ArrayDeque<Double>) sabbTmp;
        if(size < basb.size()) basb.removeFirst();
        if(size < sabb.size()) sabb.removeFirst();


        basb.add(BigDecimalUtils.sub(bitmexOrderBook.getAsks().get(depth).getPrice() ,
                okexOrderBook.getBids().get(depth).getPrice()).doubleValue());
        sabb.add(BigDecimalUtils.sub(bitmexOrderBook.getBids().get(depth).getPrice(),
                okexOrderBook.getAsks().get(depth).getPrice()).doubleValue());

        charData.put("basb", basb);
        charData.put("sabb", sabb);
    }

    public void scanerKline() {
        List<Kline> bitmexKline = getBitmexKline();
        List<Kline> okexKline = getOkexKline();
        List<Double> middle = null;
        List<Double> middleUp = null;
        List<Double> middleDown = null;
        if(null == charData.get("middle")) {
            middle = new ArrayList<>(size);
        } else {
            middle = (List<Double>) charData.get("middle");
        }
        if(null == charData.get("middleUp")) {
            middleUp = new ArrayList<>(size);
        } else {
            middleUp = (List<Double>) charData.get("middleUp");
        }
        if(null == charData.get("middleDown")) {
            middleDown = new ArrayList<>(size);
        } else {
            middleDown = (List<Double>) charData.get("middleDown");
        }

        if(null == bitmexKline || null == okexKline) return;
        if(size  == bitmexKline.size() && size == okexKline.size()) {
            List<Double> sub = new ArrayList<>(okexKline.size());

            for(int i=0, j=bitmexKline.size(); i<j; i++) {
                double m = bitmexKline.get(i).getClose() - okexKline.get(i).getClose();
                sub.add(m);
            }

            double total = 0;
            for(Double tmp : sub) {
                total += tmp;
            }
            double average = total/sub.size();
            total = 0;
            for(int i=0;i<sub.size();i++){
                total += (sub.get(i)-average)*(sub.get(i)-average);   //求出方差，如果要计算方差的话这一步就可以了
            }
            double standardDeviation = Math.sqrt(total/sub.size());   //求出标准差

            middle.add(average);
            middleUp.add(average + (standardDeviation * 2));
            middleDown.add(average - (standardDeviation * 2));
            charData.put("middle", middle);
            charData.put("middleUp", middleUp);
            charData.put("middleDown", middleDown);
        }
    }

    public DepthVo getBitmexDepth() {
        DepthVo depthVo = bitMexService.getDepthOtc(CoinOtc.btc, "XBTUSD");
        return depthVo;
    }

    public DepthVo getOkexDepth() {
        OkCoinTickerResponse ticker = null;
        DepthVo depthVo = okexService.getDepthOtc(CoinOtc.btc, "quarter");
        return  depthVo;
    }


    public List<Kline> getBitmexKline() {
        try {
            return bitMexService.getRecordsOtc(CoinOtc.btc, "XBTUSD", "1min", (long)size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public List<Kline> getOkexKline() {
        try {
            return okexService.getRecordsOtc(CoinOtc.btc, "quarter", "1min", (long)size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    private static int flag = 0;
    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {
        if(flag < 5 && quartzEnum == QuartzEnum.Minute) {
            flag ++;
        }
        if(flag > 3) {
            if(QuartzEnum.Second5 == quartzEnum) {
                scanerKline();
            }
            if(QuartzEnum.Second3 == quartzEnum) {
                scanerDepth();
                statics();
            }
        }
        return null;
    }
}
