package com.github.misterchangray.common.utils;

import com.github.misterchangray.libs.binance.api.client.BinanceApiClientFactory;
import com.github.misterchangray.libs.binance.api.client.BinanceApiRestClient;
import com.github.misterchangray.libs.binance.api.client.domain.market.TickerStatistics;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Map;


/**
 *
 * 提供java模拟http请求
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 4/29/2018.
 */
public class HttpUtils {

    private static String apiKey = "BPTh9qWoh4Z2LEe*lVjS1ZmswEB2oPDm";
    private static String secretKey = "MnJ2Vmez*YzdZMbVj0ts";
    public static void main(String[] a) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //启用代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对https也开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secretKey);
        BinanceApiRestClient client = factory.newRestClient();
        TickerStatistics tickerStatistics = client.get24HrPriceStatistics("BCHUSDT");
        System.out.println(tickerStatistics);

//        secPrice.removeFirst();
//        System.out.println(secPrice.getLast());
//        secPrice.removeFirst();
//        System.out.println(secPrice.getLast());
//        secPrice.removeFirst();
    }

    /**
     * JSON格式提交;返回结果转换为Map
     * @param urlPath
     * @param header
     * @param data
     * @return
     */
    public static String jsonPost(String urlPath,  Map<String, String> header, String data) throws IOException {
        if(null == header) header = MapBuilder.build();
        header.put("Content-Type", "application/json");

        return readStringFormInputStream(http(urlPath, "POST", header, data));
    }


    /**
     * 表单格式提交;返回结果转换为Map
     * @param urlPath
     * @param header
     * @param data
     * @return
     */
    public static String formPost(String urlPath,  Map<String, String> header, String data) throws IOException {
        if(null == header) header = MapBuilder.build();
        header.put("Content-Type", "application/x-www-form-urlencoded");

        return readStringFormInputStream(http(urlPath, "POST", header, data));
    }


    /**
     * http请求核心方法
     * @param urlPath  请求地址
     * @param method  请求方式;GET;POST;DELETE;PUT
     * @param header  增加header头
     * @param data  请求数据;根据不同请求格式组织不同的数据格式
     * @return
     */
    public static InputStream http(String urlPath, String method, Map<String, String> header, String data) throws IOException {
        if(null == method) method = "GET";
        // 统一资源
        URL url = new URL(urlPath);
        // 连接类的父类，抽象类
        URLConnection urlConnection = url.openConnection();
        // http的连接类
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        // 设定请求的方法，默认是GET
        httpURLConnection.setRequestMethod(method);
        // 设置字符编码
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        httpURLConnection.setRequestProperty("accept", "*/*");
        //设置header
        if(null != header) {
            for(String key : header.keySet()) {
                httpURLConnection.setRequestProperty(key, header.get(key));
            }
        }
        if(null != data) {
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
        }
        // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
        httpURLConnection.connect();
        return httpURLConnection.getInputStream();


    }

    /**
     * 从inputStream中读取字符串
     * @param inputStream
     * @return
     */
    public static String readStringFormInputStream(InputStream inputStream) throws IOException {
        if(null == inputStream) return null;

        StringBuilder stringBuilder = new StringBuilder();
        int i;
        while(-1 != (i = inputStream.read())) {
            stringBuilder.append((char) i);
        }
        inputStream.close();

        return stringBuilder.toString();
    }


    private static SSLContext sslContext = null;
    /*
     * 处理https GET/POST请求
     * 请求地址、请求方法、参数
     * */
    public static HttpsURLConnection httpsRequest(String requestUrl,String requestMethod,String params, Map<String, String> header) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        StringBuffer buffer=null;
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
        if(null == sslContext) {
            //创建SSLContext
            HttpUtils.sslContext=SSLContext.getInstance("SSL");
            TrustManager[] tm={new HttpsProtocol()};
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());;
        }

        //获取SSLSocketFactory对象
        SSLSocketFactory ssf=sslContext.getSocketFactory();
        URL url= new URL(requestUrl);
        HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        if(null == header.get("user-agent")) {
            header.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        }
        //设置header
        if(null != header) {
            for(String key : header.keySet()) {
                conn.setRequestProperty(key, header.get(key));
            }
        }
        conn.setRequestMethod(requestMethod);
        //设置当前实例使用的SSLSoctetFactory
        conn.setSSLSocketFactory(ssf);
        //往服务器端写内容
        if(null!=params){
            OutputStream os=conn.getOutputStream();
            os.write(params.getBytes("utf-8"));
            os.flush();
            os.close();
        }
        return conn;

    }


    public static HttpsURLConnection httpsRequest(String requestUrl,String requestMethod, Map<String, String> params, Map<String, String> header) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        StringBuffer buffer=null;
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
        if(null == sslContext) {
            //创建SSLContext
            HttpUtils.sslContext=SSLContext.getInstance("SSL");
            TrustManager[] tm={new HttpsProtocol()};
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());;
        }

        //获取SSLSocketFactory对象
        SSLSocketFactory ssf=sslContext.getSocketFactory();
        URL url= new URL(requestUrl);
        HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        if(null == header.get("user-agent")) {
            header.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        }
        //设置header
        if(null != header) {
            for(String key : header.keySet()) {
                conn.setRequestProperty(key, header.get(key));
            }
        }
        conn.setRequestMethod(requestMethod);
        //设置当前实例使用的SSLSoctetFactory
        conn.setSSLSocketFactory(ssf);
        StringBuilder paramsStr = new StringBuilder();
        if(0 < params.size()) {
            for(String key : params.keySet()) {
                paramsStr.append(MessageFormat.format("{0}={1}&", key, params.get(key)));
            }
        }

        //往服务器端写内容
        if(null!=params){
            OutputStream os=conn.getOutputStream();
            os.write(paramsStr.substring(0, paramsStr.length() - 1).getBytes("utf-8"));
            os.flush();
            os.close();
        }
        return conn;

    }


}


/**
 * 自定义一个协议;相信所有证书
 */
class HttpsProtocol implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}