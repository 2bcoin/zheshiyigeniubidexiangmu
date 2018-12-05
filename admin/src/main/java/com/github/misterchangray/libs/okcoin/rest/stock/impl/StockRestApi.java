package com.github.misterchangray.libs.okcoin.rest.stock.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.misterchangray.common.utils.CryptoUtils;
import com.github.misterchangray.common.utils.StringSort;
import com.github.misterchangray.libs.okcoin.rest.HttpUtilManager;
import com.github.misterchangray.libs.okcoin.rest.MD5Util;
import com.github.misterchangray.libs.okcoin.rest.StringUtil;
import com.github.misterchangray.libs.okcoin.rest.stock.IStockRestApi;
import org.apache.http.HttpException;





public class StockRestApi implements IStockRestApi {

	private String secret_key;
	
	private String api_key;
	
	private String url_prex;
	
	public StockRestApi(String url_prex,String api_key,String secret_key){
		this.api_key = api_key;
		this.secret_key = secret_key;
		this.url_prex = url_prex;
	}
	
	public StockRestApi(String url_prex){
		this.url_prex = url_prex;
	}


	
	/**
	 * 现货行情URL
	 */
	private final String TICKER_URL = "/api/v1/ticker.do?";
	
	/**
	 * 现货市场深度URL
	 */
	private final String DEPTH_URL = "/api/v1/depthSpot.do?";
	
	/**
	 * 现货历史交易信息URL
	 */
	private final String TRADES_URL = "/api/v1/trades.do?";
	
	/**
	 * 现货获取用户信息URL
	 */
	private final String USERINFO_URL = "/api/v1/userinfo.do?";
	
	/**
	 * 现货 下单交易URL
	 */
	private final String TRADE_URL = "/api/v1/order.do?";
	
	/**
	 * 现货 批量下单URL
	 */
	private final String BATCH_TRADE_URL = "/api/v1/batch_trade.do";
	
	/**
	 * 现货 撤销订单URL
	 */
	private final String CANCEL_ORDER_URL = "/api/v1/cancel_order.do";
	
	/**
	 * 现货 获取用户订单URL
	 */
	private final String ORDER_INFO_URL = "/api/v1/order_info.do";
	
	/**
	 * 现货 批量获取用户订单URL
	 */
	private final String ORDERS_INFO_URL = "/api/v1/orders_info.do";
	
	/**
	 * 现货 获取历史订单信息，只返回最近七天的信息URL
	 */
	private final String ORDER_HISTORY_URL = "/api/v1/order_history.do";

	@Override
	public String ticker(String symbol) throws HttpException, IOException {
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String param = "";
//		if(!StringUtil.isEmpty(symbol )) {
//			if (!param.equals("")) {
//				param += "&";
//			}
//			param += "symbol=" + symbol;
//		}
		String[] params = {"secret_key=" + secret_key,"symbol=" + symbol, "api_key=" + api_key};
		params = StringSort.getUrlParam(params);
		param = StringSort.getText(params, "&");
		param = param + "&sign=" + CryptoUtils.encodeMD5(param);

		String result = httpUtil.requestHttpGet(url_prex, TICKER_URL, param);
	    return result;
	}

	@Override
	public String depth(String symbol) throws HttpException, IOException {
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String param = "";
		if(!StringUtil.isEmpty(symbol )) {
			if(!param.equals("")) {
				param += "&";
			}
			param += "symbol=" + symbol;
		}
		String result = httpUtil.requestHttpGet(url_prex, this.DEPTH_URL, param);
	    return result;
	}

	@Override
	public String trades(String symbol, String since) throws HttpException, IOException {
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String param = "";
		if(!StringUtil.isEmpty(symbol )) {
			if (!param.equals("")) {
				param += "&";
			}
			param += "symbol=" + symbol;
		}
		if(!StringUtil.isEmpty(since )) {
			if (!param.equals("")) {
				param += "&";
			}
			param += "since=" + since;
		}
		String result = httpUtil.requestHttpGet(url_prex, this.TRADES_URL, param);
	    return result;
	}

	@Override
	public String userinfo() throws HttpException, IOException {
		// 构造参数签名
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", api_key);
		String sign = MD5Util.buildMysignV1(params, this.secret_key);
		params.put("sign", sign);
		
		// 发送post请求
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String result = httpUtil.requestHttpPost(url_prex,this.USERINFO_URL,
				params);

		return result;
	}

	@Override
	public String trade(String symbol, String type,
			String price, String amount) throws HttpException, IOException {
		// 构造参数签名
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", api_key);
		if(!StringUtil.isEmpty(symbol)){
			params.put("symbol", symbol);
		}
		if(!StringUtil.isEmpty(type)){
			params.put("type", type);
		}
		if(!StringUtil.isEmpty(price)){
			params.put("price", price);
		}
		if(!StringUtil.isEmpty(amount)){
			params.put("amount", amount);
		}
		String sign = MD5Util.buildMysignV1(params, this.secret_key);
		params.put("sign", sign);
		
		// 发送post请求
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String result = httpUtil.requestHttpPost(url_prex,this.TRADE_URL,
				params);

		return result;
	}

	@Override
	public String batch_trade( String symbol, String type,
			String orders_data) throws HttpException, IOException {
		// 构造参数签名
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", api_key);
		if(!StringUtil.isEmpty(symbol)){
			params.put("symbol", symbol);
		}
		if(!StringUtil.isEmpty(type)){
			params.put("type", type);
		}
		if(!StringUtil.isEmpty(orders_data)){
			params.put("orders_data", orders_data);
		}
		String sign = MD5Util.buildMysignV1(params, this.secret_key);
		params.put("sign", sign);
		
		// 发送post请求
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String result = httpUtil.requestHttpPost(url_prex,this.BATCH_TRADE_URL,
				params);

		return result;
	}

	@Override
	public String cancel_order(String symbol, String order_id) throws HttpException, IOException {
		// 构造参数签名
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", api_key);
		if(!StringUtil.isEmpty(symbol)){
			params.put("symbol", symbol);
		}
		if(!StringUtil.isEmpty(order_id)){
			params.put("order_id", order_id);
		}

		String sign = MD5Util.buildMysignV1(params, this.secret_key);
		params.put("sign", sign);
		
		// 发送post请求
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String result = httpUtil.requestHttpPost(url_prex,this.CANCEL_ORDER_URL,
				params);

		return result;
	}

	@Override
	public String order_info(String symbol, String order_id) throws HttpException, IOException {
		// 构造参数签名
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", api_key);
		if(!StringUtil.isEmpty(symbol)){
			params.put("symbol", symbol);
		}
		if(!StringUtil.isEmpty(order_id)){
			params.put("order_id", order_id);
		}

		String sign = MD5Util.buildMysignV1(params, this.secret_key);
		params.put("sign", sign);
		
		// 发送post请求
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String result = httpUtil.requestHttpPost(url_prex,this.ORDER_INFO_URL,
				params);

		return result;
	}

	@Override
	public String orders_info(String type, String symbol,
			String order_id) throws HttpException, IOException {
		// 构造参数签名
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", api_key);
		if(!StringUtil.isEmpty(type)){
			params.put("type", type);
		}
		if(!StringUtil.isEmpty(symbol)){
			params.put("symbol", symbol);
		}
		if(!StringUtil.isEmpty(order_id)){
			params.put("order_id", order_id);
		}

		String sign = MD5Util.buildMysignV1(params, this.secret_key);
		params.put("sign", sign);
		
		// 发送post请求
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String result = httpUtil.requestHttpPost(url_prex,this.ORDERS_INFO_URL,
				params);

		return result;
	}

	@Override
	public String order_history(String symbol, String status,
			String current_page, String page_length) throws HttpException, IOException {
		// 构造参数签名
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", api_key);
		if(!StringUtil.isEmpty(symbol)){
			params.put("symbol", symbol);
		}
		if(!StringUtil.isEmpty(status)){
			params.put("status", status);
		}
		if(!StringUtil.isEmpty(current_page)){
			params.put("current_page", current_page);
		}
		if(!StringUtil.isEmpty(page_length)){
			params.put("page_length", page_length);
		}

		String sign = MD5Util.buildMysignV1(params, this.secret_key);
		params.put("sign", sign);
		
		// 发送post请求
		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
		String result = httpUtil.requestHttpPost(url_prex,this.ORDER_HISTORY_URL,
				params);

		return result;
	}

	public String getSecret_key() {
		return secret_key;
	}

	public void setSecret_key(String secret_key) {
		this.secret_key = secret_key;
	}

	

	public String getApi_key() {
		return api_key;
	}

	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}

	public String getUrl_prex() {
		return url_prex;
	}

	public void setUrl_prex(String url_prex) {
		this.url_prex = url_prex;
	}

}
