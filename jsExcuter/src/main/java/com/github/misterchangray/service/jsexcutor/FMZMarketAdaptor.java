package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.CoinSpot;
import com.github.misterchangray.common.utils.DateUtils;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.jsexcutor.util.Logger;
import com.github.misterchangray.service.jsexcutor.util.Utils;
import com.github.misterchangray.service.jsexcutor.vo.*;
import com.github.misterchangray.service.platform.OKEXService;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Kline;
import com.github.misterchangray.service.platform.vo.Ticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FMZMarketAdaptor {
    private FMZAdaptor fmzAdaptor;

    ////////////////////////////////////////////////////市场信息
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public FMZTicker GetTicker() {
        BaseService baseService = fmzAdaptor.getConfig__Service();
        if(null == baseService) return null;
        Ticker ticker = null;
        try {
            if(fmzAdaptor.isConfig__Otc()) {
                ticker = baseService.getTickerOtc(CoinOtc.getCoin(fmzAdaptor.getConfig__Coin()),
                        fmzAdaptor.getConfig__ContractType());
            } else {
                ticker = baseService.getTickerSpot(fmzAdaptor.getConfig__Coin());
            }
            if(null != ticker) {
                FMZTicker fmzTicker = new FMZTicker();
                fmzTicker.Buy = Utils.convertBigDecimal(ticker.getBuy());
                fmzTicker.High = Utils.convertBigDecimal(ticker.getHigh());
                fmzTicker.Last = Utils.convertBigDecimal(ticker.getLast());
                fmzTicker.Low = Utils.convertBigDecimal(ticker.getLow());
                fmzTicker.Sell = Utils.convertBigDecimal(ticker.getSell());
                fmzTicker.Volume = Utils.convertBigDecimal(ticker.getVol());
                fmzTicker.Info = ticker.getInfo();
                return fmzTicker;
            }
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }
        return null;
    }

    public FMZDepth GetDepth( ) {
        BaseService baseService = fmzAdaptor.getConfig__Service();
        if(null == baseService) return null;
        DepthVo depthVo = null;
        try {
            if(fmzAdaptor.isConfig__Otc()) {
                depthVo = baseService.getDepthOtc(CoinOtc.getCoin(fmzAdaptor.getConfig__Coin()),
                        fmzAdaptor.getConfig__ContractType());
            } else {
                depthVo = baseService.getDepthSpot(fmzAdaptor.getConfig__Coin());
            }
            if(null != depthVo) {
                FMZDepth fmzDepth = new FMZDepth();
                fmzDepth.Bids = new ArrayList<Depth>();
                for(com.github.misterchangray.service.platform.vo.Depth depthTmp : depthVo.getBids()) {
                    Depth depth = new Depth();
                    depth.Amount = Utils.convertBigDecimal(depthTmp.getQty());
                    depth.Price = Utils.convertBigDecimal(depthTmp.getPrice());
                    fmzDepth.Bids.add(depth);
                };
                fmzDepth.Asks = new ArrayList<Depth>();
                for(com.github.misterchangray.service.platform.vo.Depth depthTmp : depthVo.getAsks()) {
                    Depth depth = new Depth();
                    depth.Amount = Utils.convertBigDecimal(depthTmp.getQty());
                    depth.Price = Utils.convertBigDecimal(depthTmp.getPrice());
                    fmzDepth.Asks.add(depth);
                };
                fmzDepth.Info = depthVo.getInfo();
                return fmzDepth;
            }
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }

        return null;
    }
    public Object GetTrades( ) {
        return null;
    }
    public List<FMZKline> GetRecords(String Period, String size) {
        //:PERIOD_M1 指1分钟, PERIOD_M5 指5分钟, PERIOD_M15 指15分钟, PERIOD_M30 指30分钟, PERIOD_H1 指1小时, PERIOD_D1 指一天。
        BaseService baseService = fmzAdaptor.getConfig__Service();
        if(null == baseService) return null;
        List<Kline> klines = null;
        try {
            if(fmzAdaptor.isConfig__Otc()) {
                klines =  baseService.getRecordsOtc(CoinOtc.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__ContractType(),
                        Period, Long.valueOf(size));
            } else {
                klines =  baseService.getRecordsSpot(CoinSpot.getCoin(fmzAdaptor.getConfig__Coin()), Period, Long.valueOf(size));
            }
            if(null != klines) {
                List<FMZKline> fmzKline = new ArrayList<FMZKline>();
                for(Kline kline : klines) {
                    FMZKline fmzKline1 = new FMZKline();
                    fmzKline1.High = Utils.convertBigDecimal(kline.getHigh());
                    fmzKline1.Low = Utils.convertBigDecimal(kline.getLow());
                    fmzKline1.Open = Utils.convertBigDecimal(kline.getOpen());
                    fmzKline1.Close = Utils.convertBigDecimal(kline.getClose());
                    fmzKline1.Time = kline.getTime();
                    fmzKline1.Volume = Utils.convertBigDecimal(kline.getVol());
                    fmzKline1.Info = kline.getInfo();
                    fmzKline.add(fmzKline1);
                }
                return fmzKline;
            }
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }

        return null;
    }

    public FMZMarketAdaptor(FMZAdaptor fmzAdaptor) {
        this.fmzAdaptor = fmzAdaptor;
    }

}
