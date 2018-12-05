package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.jsexcutor.util.Logger;
import com.github.misterchangray.service.po.AccountBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

public class FMZAccountAdaptor {
    private FMZAdaptor fmzAdaptor;

    ///////////////////////////////////////////////////账户信息
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<AccountBalance>  GetAccount() {
        BaseService baseService = fmzAdaptor.getConfig__Service();
        if(null == baseService) return null;
        try {
            return  baseService.getAccount(fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }
        return null;
    }

    public FMZAccountAdaptor(FMZAdaptor fmzAdaptor) {
        this.fmzAdaptor = fmzAdaptor;
    }
}
