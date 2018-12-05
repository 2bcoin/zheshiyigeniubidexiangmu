package com.github.misterchangray.common.utils;

import com.github.misterchangray.common.init.Init;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import sun.security.krb5.internal.PAData;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class HttpUtilManager {

	private static HttpUtilManager instance = new HttpUtilManager();
	private static HttpClient client;
	private static long startTime = System.currentTimeMillis();
	private static PoolingHttpClientConnectionManager cm ;
	private static ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {
		@Override
		public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			HeaderElementIterator it = new BasicHeaderElementIterator
					(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();
				if (value != null && param.equalsIgnoreCase
						("timeout")) {
					return Long.parseLong(value) * 1000;
				}
			}
			return 3 * 60 * 1000;//如果没有约定，则默认定义时长为3分钟
		}

	};

	public static void main(String[] a) {
		try {
			HttpUtilManager.getInstance().requestHttpGet("http://www.baidu.com",null,null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private HttpUtilManager() {
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(500);
		cm.setDefaultMaxPerRoute(100);//例如默认每路由最高50并发，具体依据业务来定
		client = Init.httpClientBuilder
				.setConnectionManager(cm)
				.setKeepAliveStrategy(keepAliveStrat)
				.setMaxConnPerRoute(100)
				.setRetryHandler(new HttpRequestRetryHandler() {
					@Override
					public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
						return false;  //不需要retry
					}
				})
				.setMaxConnTotal(100)
				.build();
	}

	public static void IdleConnectionMonitor(){

		if(System.currentTimeMillis()-startTime>30000){
			startTime = System.currentTimeMillis();
			cm.closeExpiredConnections();
			cm.closeIdleConnections(30, TimeUnit.SECONDS);
		}
	}

	private static RequestConfig requestConfig = RequestConfig.custom()
			.setConnectTimeout(1000) //连接超时
			.setSocketTimeout(6000)//读取超时
			.setConnectionRequestTimeout(1000)
			.build();



	public static HttpUtilManager getInstance() {
		return instance;
	}


	public String request(String method, String url,Map<String, String> query, Map<String, String> params, Map<String, String> header) {
		try {
			if("GET".equalsIgnoreCase(method)) {
				return this.requestHttpGet(url, query, header);
			} else if("POST".equalsIgnoreCase(method)) {
				return this.requestHttpPost(url, query, params, header);
			} else if("PUT".equalsIgnoreCase(method)) {
				return this.requestHttpPut(url, query, params, header);
			} else if("DELETE".equalsIgnoreCase(method)) {
				return this.requestHttpDelete(url, query, header);
			} else if("PATCH".equalsIgnoreCase(method)) {
				return this.requestHttpPatch(url, query, params, header);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  null;
	}
	public String makeQueryUrl(String url, Map<String, String> query){
		if(null != query){
			if(url.contains("?")) {
				if(!url.endsWith("&")) {
					url += "&";
				}
			} else  {
				url += "?";
			}
			for (String key : query.keySet()) {
				url += key + "=" + String.valueOf(query.get(key)) + "&";
			}

			if(query.keySet().size() > 0) {
				url = url.substring(0, url.length() - 1);
			}
		}
		return url;
	}

	public String requestHttpGet(String url, Map<String, String> query, Map<String, String> header) throws IOException{
		IdleConnectionMonitor();
		if(null != query) {
			url = makeQueryUrl(url, query);
		}

		HttpGet request = new HttpGet(url);
		//设置header
		if(null != header) {
			for(String key : header.keySet()) {
				request.setHeader(key, header.get(key));
			}
		}
		if(null == header ||  null == header.get("Connection")) {
			request.setHeader("Connection", "keep-alive");
		}
		request.setConfig(requestConfig);

		String responseData = null;
		try{
			HttpResponse response = client.execute(request);
			responseData = EntityUtils.toString(response.getEntity());//获得返回的结果
		}catch(Exception e){
			request.abort();
//			e.printStackTrace();
		}finally {
		}

		return responseData;
	}
	public String requestHttpDelete(String url, Map<String,String> query, Map<String, String> header) throws IOException{
		IdleConnectionMonitor();
		if(null != query) {
			url = makeQueryUrl(url, query);
		}

		HttpDelete request = new HttpDelete(url);
		//设置header
		if(null != header) {
			for(String key : header.keySet()) {
				request.setHeader(key, header.get(key));
			}
		}
		if(null == header ||  null == header.get("Connection")) {
			request.setHeader("Connection", "keep-alive");
		}
		request.setConfig(requestConfig);

		String responseData = null;
		try{
			HttpResponse response = client.execute(request);
			responseData = EntityUtils.toString(response.getEntity());//获得返回的结果
		}catch(Exception e){
			request.abort();
//			e.printStackTrace();
		}finally {
		}

		return responseData;
	}

	/**
	 * 默认使用表单方式提交;如提交JSON请指定content-type
	 * @param url
	 * @param query url方式传参
	 * @param params 表单方式传参
	 * @param header
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public String requestHttpPost(String  url, Map<String,String> query, Map<String,String> params, Map<String, String> header) {

		if(null != query) {
			url = makeQueryUrl(url, query);
		}
		HttpPost request = new HttpPost(url);
		request.setConfig(requestConfig);
		String responseData = null;
		//设置header
		if(null != header) {
			for(String key : header.keySet()) {
				request.setHeader(key, String.valueOf(header.get(key)));
			}
		}
		setRequestBody(request, params, header);

		try{
			HttpResponse response = client.execute(request);
			responseData = EntityUtils.toString(response.getEntity());//获得返回的结果
		}catch(IOException e){
//			e.printStackTrace();
			request.abort();
		}finally {}

		return responseData;

	}
	public String requestHttpPut(String  url, Map<String,String> query, Map<String,String> params, Map<String, String> header) {
		if(null != query) {
			url = makeQueryUrl(url, query);
		}
		HttpPut request = new HttpPut(url);
		request.setConfig(requestConfig);
		String responseData = null;
		//设置header
		if(null != header) {
			for(String key : header.keySet()) {
				request.setHeader(key, String.valueOf(header.get(key)));
			}
		}
		setRequestBody(request, params, header);
		try{
			HttpResponse response = client.execute(request);
			responseData = EntityUtils.toString(response.getEntity());//获得返回的结果
		}catch(IOException e){
//			e.printStackTrace();
			request.abort();
		}finally { }
		return responseData;

	}

	public String requestHttpPatch(String  url, Map<String,String> query, Map<String,String> params, Map<String, String> header) {
		if(null != query) {
			url = makeQueryUrl(url, query);
		}
		HttpPatch request = new HttpPatch(url);
		request.setConfig(requestConfig);
		String responseData = null;
		//设置header
		if(null != header) {
			for(String key : header.keySet()) {
				request.setHeader(key, String.valueOf(header.get(key)));
			}
		}
		setRequestBody(request, params, header);
		try{
			HttpResponse response = client.execute(request);
			responseData = EntityUtils.toString(response.getEntity());
		}catch(IOException e){
//			e.printStackTrace();
			request.abort();
		}finally { }
		return responseData;

	}


	private HttpEntityEnclosingRequestBase setRequestBody(HttpEntityEnclosingRequestBase request, Map<String,String> params, Map<String,String> header) {
		if(null != params && null != header.get("Content-Type") && header.get("Content-Type").contains("json")) {
			try {
				request.setEntity(new StringEntity(JSONUtils.obj2json(params)));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if(null != params) {
			List<NameValuePair> valuePairs = this.convertMap2PostParams(params);
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
			request.setEntity(urlEncodedFormEntity);
		}
		return request;
	}
	private List<NameValuePair> convertMap2PostParams(Map<String,String> params){
		List<String> keys = new ArrayList<String>(params.keySet());
		if(keys.isEmpty()){
			return null;
		}
		int keySize = keys.size();
		List<NameValuePair>  data = new LinkedList<NameValuePair>() ;
		for(int i=0;i<keySize;i++){
			String key = keys.get(i);
			String value = String.valueOf(params.get(key));
			data.add(new BasicNameValuePair(key,value));
		}
		return data;
	}

}

