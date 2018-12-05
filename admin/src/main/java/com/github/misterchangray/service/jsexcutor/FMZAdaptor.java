package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.service.common.ContextCacheService;
import com.github.misterchangray.service.common.H2DataBaseCache;
import com.github.misterchangray.service.jsexcutor.exception.FMZException;
import com.github.misterchangray.service.jsexcutor.util.Logger;
import com.github.misterchangray.service.jsexcutor.util.LoggerMetadata;
import com.github.misterchangray.service.jsexcutor.util.Utils;
import com.github.misterchangray.service.jsexcutor.vo.FMZAccount;
import com.github.misterchangray.service.po.AccountBalance;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * 发明者平台的函数适配
 */
@Component
@Scope(value = "prototype")
public class FMZAdaptor extends Global {
    org.slf4j.Logger logger = LoggerFactory.getLogger(Global.class);
    FMZMarketAdaptor fmzMarketAdaptor = new FMZMarketAdaptor(this);
    FMZTradeAdaptor fmzTradeAdaptor = new FMZTradeAdaptor(this);
    FMZAccountAdaptor fmzAccountAdaptor  = new FMZAccountAdaptor(this);
    FMZOtcTradeAdaptor fmzOtcTradeAdaptor  = new FMZOtcTradeAdaptor(this);
    @Autowired
    H2DataBaseCache h2DataBaseCache;

    public synchronized void log(String level, String mes) {
        log(Logger.getLevelBuyName(level), mes);
    }

    public synchronized void log(Logger.Level level, String mes) {
        Logger logger = new Logger(level, null, mes, this.getConfig__Org(), null);
        this.logger.info("h2DataBaseCache: level:{}, mes:{}" , level, mes);
        h2DataBaseCache.log(this.scriptDto.getIpAndScriptId(), JSONUtils.obj2json(logger));
    }

    public synchronized void logReset() {

    }


    /**
     *
     * @param key
     * @param value
     * @param direct get set remove
     * @return
     */
    public String Storage(String key, String value, String direct) {
        String pre = this.scriptDto.getIpAndScriptId();
        key = key + "_" + pre;
        if("get".equalsIgnoreCase(direct)) {
            return h2DataBaseCache.get(key);
        } else if("remove".equalsIgnoreCase(direct)) {
            h2DataBaseCache.remove(key);
       } else if("set".equalsIgnoreCase(direct)) {
           h2DataBaseCache.set(key, value);
        }
        return null;
    }


    ////////////////////////////////////////////////////市场信息
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String GetTicker(Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            return JSONUtils.obj2json(fmzMarketAdaptor.GetTicker());
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }
    public String GetDepth(Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            return JSONUtils.obj2json(fmzMarketAdaptor.GetDepth());
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }
    public String GetTrades(Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            return JSONUtils.obj2json(fmzMarketAdaptor.GetTrades());
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }
    public String GetRecords(String Period, String size, Map<String, String> configs) throws FMZException {
        try {
            this.currentConfigs = configs;
            //:PERIOD_M1 指1分钟, PERIOD_M5 指5分钟, PERIOD_M15 指15分钟, PERIOD_M30 指30分钟, PERIOD_H1 指1小时, PERIOD_D1 指一天。
            return JSONUtils.obj2json(fmzMarketAdaptor.GetRecords(Period, size));
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }


    ///////////////////////////////////////////////////交易操作
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String Buy(Double Price, Double Amount, Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            return fmzTradeAdaptor.Buy(Price, Amount);
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }
    public String Sell(Double Price, Double Amount, Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            return fmzTradeAdaptor.Sell(Price, Amount);
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }
    public String CancelOrder(String orderId, Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            return fmzTradeAdaptor.CancelOrder(orderId);
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }
    public String GetOrder(String orderId) {
        try {
            return fmzTradeAdaptor.GetOrder(orderId);
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }
    public String GetOrders() {
        try {
            return fmzTradeAdaptor.GetOrders();
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }

    public String IO(String method, String api, Map<String, String> param, Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            return fmzTradeAdaptor.IO_Rest(method, api, param);
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }

    ///////////////////////////////////////////////////账户信息
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String GetAccount(Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            List<AccountBalance> accountBalanceList =fmzAccountAdaptor.GetAccount();
            if(null == accountBalanceList) return null;
            FMZAccount fmzAccount = new FMZAccount();
            for(AccountBalance accountBalance : accountBalanceList) {
                if(this.isConfig__Otc() && accountBalance.getType().equals(AccountBalance.otc)) {
                    fmzAccount.Info = accountBalance.getInfo();
                    fmzAccount.Stocks = null == accountBalance.getBalance() ? 0d : accountBalance.getBalance().doubleValue();
                    return JSONUtils.obj2json(fmzAccount);
                }
                if(this.isConfig__Spot() && accountBalance.getType().equals(AccountBalance.spot)) {
                    fmzAccount.Info = accountBalance.getInfo();
                    fmzAccount.Stocks = null == accountBalance.getBalance() ? 0d : accountBalance.getBalance().doubleValue();
                    return JSONUtils.obj2json(fmzAccount);
                }
            }
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }


    ///////////////////////////////////////////////////期货交易
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String GetPosition(Map<String, String> configs) {
        try {
            this.currentConfigs = configs;
            return JSONUtils.obj2json(fmzOtcTradeAdaptor.GetPosition());
        } catch (Exception e) {
            log(Logger.Level.Warn, e.toString());
        }
        return null;
    }

}
