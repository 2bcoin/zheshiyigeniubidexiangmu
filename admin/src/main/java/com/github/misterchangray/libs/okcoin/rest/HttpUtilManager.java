package com.github.misterchangray.libs.okcoin.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.misterchangray.common.init.Init;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;



/**
 * 封装HTTP get post请求，简化发送http请求
 * @author zhangchi
 *
 */
public class HttpUtilManager {

	private static HttpUtilManager instance = new HttpUtilManager();
	private static HttpClient client;
	private static long startTime = System.currentTimeMillis();
	public  static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();  
	private static ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {  

	     public long getKeepAliveDuration(  
	            HttpResponse response,  
	            HttpContext context) {  
	        long keepAlive = super.getKeepAliveDuration(response, context);  
	        
	        if (keepAlive == -1) {  
	            keepAlive = 5000;  
	        }  
	        return keepAlive;  
	    }  
	  
	};
	private HttpUtilManager() {
		client = Init.httpClientBuilder.setConnectionManager(cm).setKeepAliveStrategy(keepAliveStrat).build();
	}

    public static void IdleConnectionMonitor(){
		
		if(System.currentTimeMillis()-startTime>30000){
			 startTime = System.currentTimeMillis();
			 cm.closeExpiredConnections();  
             cm.closeIdleConnections(30, TimeUnit.SECONDS);
		}
	}
//	private static HttpHost proxy = new HttpHost("127.0.0.1",1080);
	private static RequestConfig requestConfig = RequestConfig.custom()
//			.setProxy(proxy)
	        .setSocketTimeout(20000)
	        .setConnectTimeout(20000)
	        .setConnectionRequestTimeout(20000)
	        .build();
	
	
	public static HttpUtilManager getInstance() {
		return instance;
	}

	public HttpClient getHttpClient() {
		return client;
	}

	private HttpPost httpPostMethod(String url) {
		return new HttpPost(url);
	}

	private  HttpRequestBase httpGetMethod(String url) {
		return new  HttpGet(url);
	}
	
	public String requestHttpGet(String url_prex,String url,String param) throws HttpException, IOException{

		IdleConnectionMonitor();
		url=url_prex+url;
		if(param!=null && !param.equals("")){
		        if(url.endsWith("?")){
			    url = url+param;
			}else{
			    url = url+"?"+param;
			}
		}
		HttpRequestBase method = this.httpGetMethod(url);
		method.setConfig(requestConfig);
		HttpResponse response = client.execute(method);
		HttpEntity entity =  response.getEntity();
		if(entity == null){
			return "";
		}
		InputStream is = null;
		String responseData = "";
		try{
		    is = entity.getContent();
		    responseData = IOUtils.toString(is, "UTF-8");
		}finally{
			if(is!=null){
			    is.close();
			}
		}
		return responseData;
	}
	
	public String requestHttpPost(String url_prex,String url,Map<String,String> params) throws HttpException, IOException{
		
		IdleConnectionMonitor();
		url=url_prex+url;
		HttpPost method = this.httpPostMethod(url);
		List<NameValuePair> valuePairs = this.convertMap2PostParams(params);
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
		method.setEntity(urlEncodedFormEntity);
		method.setConfig(requestConfig);
		HttpResponse response = client.execute(method);
		HttpEntity entity =  response.getEntity();
		if(entity == null){
			return "";
		}
		InputStream is = null;
		String responseData = "";
		try{
		    is = entity.getContent();
		    responseData = IOUtils.toString(is, "UTF-8");
		}finally{
			if(is!=null){
			    is.close();
			}
		}
		return responseData;
		
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
			String value = params.get(key);
			data.add(new BasicNameValuePair(key,value));
		}
		return data;
	}

}

