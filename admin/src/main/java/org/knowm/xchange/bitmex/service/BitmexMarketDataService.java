package org.knowm.xchange.bitmex.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import org.knowm.xchange.bitmex.BitmexAdapters;
import org.knowm.xchange.bitmex.BitmexExchange;
import org.knowm.xchange.bitmex.BitmexPrompt;
import org.knowm.xchange.bitmex.dto.account.BitmexTicker;
import org.knowm.xchange.bitmex.dto.marketdata.BitmexKline;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Kline;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;

/**
 * Implementation of the market data service for Bitmex
 *
 * <ul>
 *   <li>Provides access to various market data values
 * </ul>
 */
public class BitmexMarketDataService extends BitmexMarketDataServiceRaw
    implements MarketDataService {

  /**
   * Constructor
   *
   * @param exchange
   */
  public BitmexMarketDataService(BitmexExchange exchange) {

    super(exchange);
  }
  @Override
  public List<Kline> getKline(CurrencyPair currencyPair, Map<String,Object> param) throws IOException {
//       String binSize,
//      Boolean partial,
//      CurrencyPair pair,
//      BitmexPrompt prompt,
//      long count,
//      Boolean reverse
    if(null == param || null == param.get("binSize") || null ==  param.get("partial")
            || null == param.get("prompt") || null ==  param.get("count") || null ==  param.get("reverse")) {
      throw new RuntimeException("params required binSize, partial, pair, prompt, count, reverse");
    }
    List<BitmexKline> tmp = this.getBucketedTrades(param.get("binSize").toString(), (boolean) param.get("partial"), currencyPair,
            (BitmexPrompt) param.get("prompt"), (int) param.get("count"), (boolean) param.get("reverse"));
    List<Kline> klines = new ArrayList<>(200);
    for(BitmexKline bitmexKline : tmp) {
      klines.add(new Kline(Date.from(Instant.parse(bitmexKline.getTimestamp())).getTime(), bitmexKline.getOpen().doubleValue(), bitmexKline.getClose().doubleValue(),
              bitmexKline.getLow().doubleValue(), bitmexKline.getHigh().doubleValue(), bitmexKline.getVolume().doubleValue()));
    }
    return klines;
  }


  @Override
  public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws IOException {

    List<BitmexTicker> bitmexTickers =
        getTicker(currencyPair.base.toString() + currencyPair.counter.toString());
    if (bitmexTickers.isEmpty()) {
      return null;
    }

    BitmexTicker bitmexTicker = bitmexTickers.get(0);

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    Ticker ticker = null;

    try {
      ticker =
          new Ticker.Builder()
              .currencyPair(currencyPair)
              .open(bitmexTicker.getOpenValue())
              .last(bitmexTicker.getLastPrice())
              .bid(bitmexTicker.getBidPrice())
              .ask(bitmexTicker.getAskPrice())
              .high(bitmexTicker.getHighPrice())
              .low(bitmexTicker.getLowPrice())
              .vwap(new BigDecimal(bitmexTicker.getVwap()))
              .volume(bitmexTicker.getVolume24h())
              .quoteVolume(null)
              .timestamp(format.parse(bitmexTicker.getTimestamp()))
              .build();
    } catch (ParseException e) {

      return null;
    }

    return ticker;
  }

  @Override
  public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args) throws IOException {

    BitmexPrompt prompt = null;
    if (args != null && args.length > 0) {
      Object arg0 = args[0];
      if (arg0 instanceof BitmexPrompt) {
        prompt = (BitmexPrompt) arg0;
      } else {
        throw new ExchangeException("args[0] must be of type BitmexPrompt!");
      }
    }
    Object[] argsToPass = Arrays.copyOfRange(args, 1, args.length);
    return BitmexAdapters.adaptOrderBook(
        getBitmexDepth(BitmexAdapters.adaptCurrencyPair(currencyPair), prompt, argsToPass),
        currencyPair);
  }

  @Override
  public Trades getTrades(CurrencyPair currencyPair, Object... args) throws IOException {

    Long since = null;
    BitmexPrompt prompt = null;
    if (args != null && args.length > 0) {
      Object arg0 = args[0];
      if (arg0 instanceof BitmexPrompt) {
        prompt = (BitmexPrompt) arg0;
      } else {
        throw new ExchangeException("args[0] must be of type BitmexPrompt!");
      }
    }
    Object[] argsToPass = Arrays.copyOfRange(args, 1, args.length);
    // Trades bitmexTrades = getBitmexTrades(BitmexAdapters.adaptCurrencyPair(currencyPair), prompt,
    // argsToPass);
    return getBitmexTrades(BitmexAdapters.adaptCurrencyPair(currencyPair), prompt, argsToPass);
  }
}
