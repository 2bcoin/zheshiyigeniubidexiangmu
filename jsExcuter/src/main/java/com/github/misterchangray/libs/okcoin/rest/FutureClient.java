package com.github.misterchangray.libs.okcoin.rest;

import java.io.IOException;

import com.github.misterchangray.libs.okcoin.rest.future.IFutureRestApi;
import com.github.misterchangray.libs.okcoin.rest.future.impl.FutureRestApiV1;
import org.apache.http.HttpException;


import com.alibaba.fastjson.JSONObject;


/**
 * 期货 REST API 客户端请求
 * @author zhangchi
 *
 */
public class FutureClient {

	public static void main(String[] args) throws HttpException, IOException {

	
	    String api_key = "";  //OKCoin申请的apiKey
       	String secret_key = "";  //OKCoin申请的secretKey
 	    String url_prex = "";  //注意：请求URL 国际站https://www.okcoin.com ; 国内站https://www.okcoin.cn
		/**
		 *  get请求无需发送身份认证,通常用于获取行情，市场深度等公共信息
		 */
		IFutureRestApi futureGetV1 = new FutureRestApiV1(url_prex);

		/**
		 * post请求需发送身份认证，获取用户个人相关信息时，需要指定api_key,与secret_key并与参数进行签名，
		 * 此处对构造方法传入api_key与secret_key,在请求用户相关方法时则无需再传入，
		 * 发送post请求之前，程序会做自动加密，生成签名。
		 * 
		*/
		IFutureRestApi futurePostV1 = new FutureRestApiV1(url_prex, api_key,secret_key);
		
	    //期货行情信息
		futureGetV1.future_ticker("btc_usd", "this_week");

		//期货指数信息
		futureGetV1.future_index("btc_usd");

		//期货交易信息
		futureGetV1.future_trades("btc_usd", "this_week");

		//期货市场深度
		futureGetV1.future_depth("btc_usd", "this_week");

		//美元-人民币汇率
		futureGetV1.exchange_rate();

		//期货下单
		String tradeResultV1 = futurePostV1.future_trade("btc_usd","this_week", "10.134", "1", "1", "0");
		JSONObject tradeJSV1 = JSONObject.parseObject(tradeResultV1);
		String tradeOrderV1 = tradeJSV1.getString("order_id");
		System.out.println(tradeResultV1);

		//期货用户订单查询
		futurePostV1.future_order_info("btc_usd", "this_week",tradeOrderV1, "1", "1", "2");

		//取消订单
		futurePostV1.future_cancel("btc_usd", "this_week",tradeOrderV1);

		//期货账户信息
		futurePostV1.future_userinfo();

		//逐仓期货账户信息
		futurePostV1.future_userinfo_4fix();
		
		//期货用户持仓查询
		futurePostV1.future_position("btc_usd", "this_week");

		//期货用户逐仓持仓查询
	    futurePostV1.future_position_4fix("btc_usd", null);


	}
}
