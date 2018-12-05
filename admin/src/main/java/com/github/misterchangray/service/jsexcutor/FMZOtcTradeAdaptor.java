package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.jsexcutor.util.Logger;
import com.github.misterchangray.service.jsexcutor.vo.FMZPosition;
import com.github.misterchangray.service.platform.vo.Position;
import javafx.geometry.Pos;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FMZOtcTradeAdaptor {
    private FMZAdaptor fmzAdaptor;

    ///////////////////////////////////////////////////期货交易
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<FMZPosition> GetPosition() {
        BaseService baseService = fmzAdaptor.getConfig__Service();
        if(null == baseService) return null;
        List<Position> positions = null;
        try {
            positions = baseService.getPositionOtc(CoinOtc.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__ContractType(),
                    fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
            if(null != positions) {
                List<FMZPosition> fmzPositions = new ArrayList<>();
                for(Position position : positions) {
                    FMZPosition fmzPosition = new FMZPosition();
                    fmzPosition.Type = position.getType();
                    fmzPosition.ContractType = position.getContractType();
                    fmzPosition.Info = position.getInfo();
                    fmzPosition.Amount = position.getAmount();
                    fmzPosition.FrozenAmount = position.getFrozenAmount();
                    fmzPosition.MarginLevel = position.getMarginLevel();
                    fmzPosition.Price = position.getPrice();
                    fmzPosition.Profit = position.getProfit();
                    fmzPosition.CanCover = position.getCanCover();
                    fmzPositions.add(fmzPosition);
                }
                return fmzPositions;
            }
        } catch (Exception e) {
           fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }
        return null;
    }

    public FMZOtcTradeAdaptor(FMZAdaptor fmzAdaptor) {
        this.fmzAdaptor = fmzAdaptor;
    }
}
