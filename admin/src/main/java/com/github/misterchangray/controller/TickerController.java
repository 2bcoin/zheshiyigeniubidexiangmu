package com.github.misterchangray.controller;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 获取并处理K线数据
 */
@Controller
@RequestMapping("/v1/ticker")
public class TickerController {
    @Autowired
    OKEXService OKEXService;
    @Autowired
    BiAnService biAnService;
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    HuoBiService huoBiService;
    @Autowired
    Big1Service big1Service;
    @Autowired
    OTCBTCService OTCBTCService;
    @Autowired
    FcoinService fcoinDataService;
    @Autowired
    BitfinexService bitfinexService;
    @Autowired
    ZbSiteService zbSiteService;
    @Autowired
    BitzService bitzService;
    @Autowired
    CoinbaseProService coinbaseProService;
    @Autowired
    DigiFinexService digiFinexService;
    @Autowired
    BiboxService biboxService;
    @Autowired
    KrakenService krakenService;


    //获取最小价格
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "lastPrice")
    public ResultSet<String> getLastPrice(@RequestParam("type" ) String type) {
        MapBuilder data = MapBuilder.build();
        data.add(OrgsInfoEnum.OKEX.getOrgName(), OKEXService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.BiAn.getOrgName(), biAnService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.HuoBi.getOrgName(), huoBiService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.Big1.getOrgName(), big1Service.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.OTCBTC.getOrgName(), OTCBTCService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.Fcoin.getOrgName(), fcoinDataService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.Bitfinex.getOrgName(), bitfinexService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.Zb_site.getOrgName(), zbSiteService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.Bit_z.getOrgName(), bitzService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.CoinbasePro.getOrgName(), coinbaseProService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.DigiFinex.getOrgName(), digiFinexService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.Bibox.getOrgName(), biboxService.getTickerSpot(type).getLast());
        data.add(OrgsInfoEnum.Kraken.getOrgName(), krakenService.getTickerSpot(type).getLast());

        return ResultSet.build().setData(data);
    }



    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "lastPriceAll")
    public ResultSet<String> getLastPrice() {
        MapBuilder res = MapBuilder.build();
        for(String type : Const.types) {
            MapBuilder data = MapBuilder.build();
            data.add(OrgsInfoEnum.OKEX.getOrgName(), OKEXService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.BiAn.getOrgName(), biAnService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.HuoBi.getOrgName(), huoBiService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.Big1.getOrgName(), big1Service.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.OTCBTC.getOrgName(), OTCBTCService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.Fcoin.getOrgName(), fcoinDataService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.Bitfinex.getOrgName(), bitfinexService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.Zb_site.getOrgName(), zbSiteService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.Bit_z.getOrgName(), bitzService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.CoinbasePro.getOrgName(), coinbaseProService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.DigiFinex.getOrgName(), digiFinexService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.Bibox.getOrgName(), biboxService.getTickerSpot(type).getLast());
            data.add(OrgsInfoEnum.Kraken.getOrgName(), krakenService.getTickerSpot(type).getLast());
            res.put(type, data);
        }
        return ResultSet.build().setData(res);
    }

}
