package com.github.misterchangray.libs.zbapi;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * 封装HTTP get post请求，简化发送http请求
 * 
 * @author zhangchi
 *
 */
public class HttpUtilManager {

	private static HttpUtilManager instance = new HttpUtilManager();
	private static HttpClient client;
	private static long startTime = System.currentTimeMillis();
	public static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	private static ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {
		public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			long keepAlive = super.getKeepAliveDuration(response, context);

			if (keepAlive == -1) {
				keepAlive = 5000;
			}
			return keepAlive;
		}

	};

	private HttpUtilManager() {
		client = HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(keepAliveStrat)
//				.setProxy(new HttpHost("127.0.0.1",1080))
				.build();
	}

	public static void IdleConnectionMonitor() {

		if (System.currentTimeMillis() - startTime > 30000) {
			startTime = System.currentTimeMillis();
			cm.closeExpiredConnections();
			cm.closeIdleConnections(30, TimeUnit.SECONDS);
		}
	}

	private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000)
			.setConnectionRequestTimeout(20000).build();

	public static HttpUtilManager getInstance() {
		return instance;
	}

	public HttpClient getHttpClient() {
		return client;
	}

	private HttpPost httpPostMethod(String url) {
		return new HttpPost(url);
	}

	private HttpRequestBase httpGetMethod(String url) {
		return new HttpGet(url);
	}

	public HttpResponse getResponse(String url) throws ClientProtocolException, IOException {
		IdleConnectionMonitor();
		HttpRequestBase method = this.httpGetMethod(url);

		method.setConfig(requestConfig);
		HttpResponse response = client.execute(method);
		return response;
	}

	public String requestHttpGet(String url, Map<String, String> params) throws HttpException, IOException {
		List<String> keys = new ArrayList<String>(params.keySet());
		String pas = "";
		if (!keys.isEmpty()) {
			int keySize = keys.size();
			for (int i = 0; i < keySize; i++) {
				String key = keys.get(i);
				String value = params.get(key);
				pas += key + "=" + value + "&";
			}
			pas = pas.substring(0, pas.length() - 1);
		}
		return this.requestHttpGet(url, "", pas);
	}

	/**
	 * 绕过验证
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {

			public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) {
			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}

	/**
	 * 模拟请求
	 * 
	 * @param url
	 *            资源地址
	 * @param map
	 *            参数列表
	 * @param encoding
	 *            编码
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendHttps(String url, Map<String, String> map, String encoding) {
		String body = "";
		// 采用绕过验证的方式处理https请求
		SSLContext sslcontext;
		try {
			sslcontext = createIgnoreVerifySSL();
			// 设置协议http和https对应的处理socket链接工厂的对象
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslcontext)).build();
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			HttpClients.custom().setConnectionManager(connManager);

			// 创建自定义的httpclient对象
			CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
			// CloseableHttpClient client = HttpClients.createDefault();

			// 创建post方式请求对象
			HttpPost httpPost = new HttpPost(url);

			// 装填参数
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (map != null) {
				for (Entry<String, String> entry : map.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			// 设置参数到请求对象中
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

			System.out.println("请求地址：" + url);
			System.out.println("请求参数：" + nvps.toString());

			// 设置header信息
			// 指定报文头【Content-type】、【User-Agent】
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			// 执行请求操作，并拿到结果（同步阻塞）
			CloseableHttpResponse response = client.execute(httpPost);
			// 获取结果实体
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 按指定编码转换结果实体为String类型
				body = EntityUtils.toString(entity, encoding);
			}
			EntityUtils.consume(entity);
			// 释放链接
			response.close();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return body;
	}

	public String requestHttpGet(String url_prex, String url, String param) throws HttpException, IOException {

		IdleConnectionMonitor();
		url = url_prex + url;
		if (param != null && !param.equals("")) {
			if (url.endsWith("?")) {
				url = url + param;
			} else {
				url = url + "?" + param;
			}
		}
		HttpRequestBase method = this.httpGetMethod(url);
		method.setConfig(requestConfig);
		HttpResponse response = client.execute(method);

		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return "";
		}
		InputStream is = null;
		String responseData = "";
		try {
			is = entity.getContent();
			responseData = IOUtils.toString(is, "UTF-8");
		} finally {
			if (is != null) {
				is.close();
			}
		}
		// System.out.println(responseData);
		return responseData;
	}

	public String requestHttpPost(String url_prex, String url, Map<String, String> params)
			throws HttpException, IOException {
		IdleConnectionMonitor();
		url = url_prex + url;
		HttpPost method = this.httpPostMethod(url);
		List<NameValuePair> valuePairs = this.convertMap2PostParams(params);
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
		method.setEntity(urlEncodedFormEntity);
		method.setConfig(requestConfig);
		HttpResponse response = client.execute(method);
		// System.out.println(method.getURI());
		HttpEntity entity = response.getEntity();

		if (entity == null) {
			return "";
		}
		InputStream is = null;
		String responseData = "";
		try {
			is = entity.getContent();
			responseData = IOUtils.toString(is, "UTF-8");
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return responseData;

	}

	private List<NameValuePair> convertMap2PostParams(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		if (keys.isEmpty()) {
			return null;
		}
		int keySize = keys.size();
		List<NameValuePair> data = new LinkedList<NameValuePair>();
		for (int i = 0; i < keySize; i++) {
			String key = keys.get(i);
			String value = params.get(key);
			data.add(new BasicNameValuePair(key, value));
		}
		return data;
	}

}
