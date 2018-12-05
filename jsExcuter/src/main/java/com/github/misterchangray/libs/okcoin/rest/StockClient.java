package com.github.misterchangray.libs.okcoin.rest;

import java.io.IOException;

import com.github.misterchangray.libs.okcoin.rest.stock.IStockRestApi;
import com.github.misterchangray.libs.okcoin.rest.stock.impl.StockRestApi;
import org.apache.http.HttpException;


import com.alibaba.fastjson.JSONObject;

/**
 * 现货 REST API 客户端请求
 * @author zhangchi
 *
 */
public class StockClient {
	private static String okex_secretKey = "092230CB2C9*5F314A7092B434";
	private static String okex_api_key = "088cfef7-*-*-960e-15c6d1115d89";

	public static void main(String[] args) throws HttpException, IOException{
		String proxyHost = "127.0.0.1";
		String proxyPort = "1080";

		//启用代理
		System.setProperty("http.proxyHost", proxyHost);
		System.setProperty("http.proxyPort", proxyPort);
		// 对https也开启代理
		System.setProperty("https.proxyHost", proxyHost);
		System.setProperty("https.proxyPort", proxyPort);


		String api_key = okex_api_key;  //OKCoin申请的apiKey
		String secret_key = okex_secretKey;  //OKCoin 申请的secret_key
 	    String url_prex = "https://www.okex.com";  //注意：请求URL 国际站https://www.okcoin.com ; 国内站https://www.okcoin.cn
	
	    /**
	     * get请求无需发送身份认证,通常用于获取行情，市场深度等公共信息
	     * 
	    */
	    IStockRestApi stockGet = new StockRestApi(url_prex, api_key, secret_key);

	    /**
	     * post请求需发送身份认证，获取用户个人相关信息时，需要指定api_key,与secret_key并与参数进行签名，
	     * 此处对构造方法传入api_key与secret_key,在请求用户相关方法时则无需再传入，
	     * 发送post请求之前，程序会做自动加密，生成签名。
	     * 
	    */
	    IStockRestApi stockPost = new StockRestApi(url_prex, api_key, secret_key);
		
	    //现货行情
		Long start = System.currentTimeMillis();
	    System.out.println(stockGet.ticker("btc_usdt"));;
	    System.out.println(System.currentTimeMillis() - start);

//            //现货市场深度
//            stockGet.depthSpot("btc_usd");
//
//            //现货OKCoin历史交易信息
//            stockGet.trades("btc_usd", "20");
//
//	    //现货用户信息
//	    stockPost.userinfo();
//
//	    //现货下单交易
//	    String tradeResult = stockPost.order("btc_usd", "buy", "50", "0.02");
//	    System.out.println(tradeResult);
//	    JSONObject tradeJSV1 = JSONObject.parseObject(tradeResult);
//	    String tradeOrderV1 = tradeJSV1.getString("order_id");
//
//	    //现货获取用户订单信息
//            stockPost.order_info("btc_usd", tradeOrderV1);
//
//	    //现货撤销订单
//	    stockPost.cancel_order("btc_usd", tradeOrderV1);
//
//	    //现货批量下单
//	    stockPost.batch_trade("btc_usd", "buy", "[{price:50, amount:0.02},{price:50, amount:0.03}]");
//
//	    //批量获取用户订单
//	    stockPost.orders_info("0", "btc_usd", "125420341, 125420342");
//
//	    //获取用户历史订单信息，只返回最近七天的信息
//	    stockPost.order_history("btc_usd", "0", "1", "20");
//
		
		
		
	}
}
