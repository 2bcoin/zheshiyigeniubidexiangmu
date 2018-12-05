package org.knowm.xchange.okcoin.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.utils.JSONUtils;
import com.mongodb.util.JSON;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Kline;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.okcoin.FuturesContract;
import org.knowm.xchange.okcoin.OkCoinAdapters;
import org.knowm.xchange.service.marketdata.MarketDataService;

import javax.websocket.OnOpen;

public class OkCoinFuturesMarketDataService extends OkCoinMarketDataServiceRaw
    implements MarketDataService {
  /** Default contract to use */
  private final FuturesContract futuresContract;

  @Override
  public List<Kline> getKline(CurrencyPair currencyPair, Map<String, Object> param) throws IOException {
    if(null == param || null == param.get("type") || null == param.get("contract_type") || null == param.get("size")) {
      throw new RuntimeException("getKline required params type, contract_type, size ");
    }
    String str = JSONUtils.obj2json(this.getFutureKlines(currencyPair, param.get("type").toString(), param.get("contract_type").toString(), param.get("size").toString()));
    JsonNode jsonNode = JSONUtils.buildJsonNode(str);
    List<Kline> klines = new ArrayList<>(100);
    for (JsonNode item : jsonNode) {
      klines.add(new Kline(item.get(0).asLong(),
              item.get(1).asDouble(), item.get(4).asDouble(),
              item.get(3).asDouble(), item.get(2).asDouble(),
              item.get(5).asDouble()));
    }
    return klines;
  }

  /**
   * Constructor
   *
   * @param exchange
   */
  public OkCoinFuturesMarketDataService(Exchange exchange, FuturesContract futuresContract) {

    super(exchange);

    this.futuresContract = futuresContract;
  }

  @Override
  public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws IOException {
    if (args != null && args.length > 0) {
      return OkCoinAdapters.adaptTicker(
          getFuturesTicker(currencyPair, (FuturesContract) args[0]), currencyPair);
    } else {
      return OkCoinAdapters.adaptTicker(
          getFuturesTicker(currencyPair, futuresContract), currencyPair);
    }
  }

  @Override
  public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args) throws IOException {
    if (args != null && args.length > 0) {
      return OkCoinAdapters.adaptOrderBook(
          getFuturesDepth(currencyPair, (FuturesContract) args[0]), currencyPair);
    } else {
      return OkCoinAdapters.adaptOrderBook(
          getFuturesDepth(currencyPair, futuresContract), currencyPair);
    }
  }

  @Override
  public Trades getTrades(CurrencyPair currencyPair, Object... args) throws IOException {
    if (args != null && args.length > 0) {
      return OkCoinAdapters.adaptTrades(
          getFuturesTrades(currencyPair, (FuturesContract) args[0]), currencyPair);
    } else {
      return OkCoinAdapters.adaptTrades(
          getFuturesTrades(currencyPair, futuresContract), currencyPair);
    }
  }
}
