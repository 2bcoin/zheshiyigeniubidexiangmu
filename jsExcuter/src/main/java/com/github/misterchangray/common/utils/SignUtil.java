package com.github.misterchangray.common.utils;

import com.github.misterchangray.libs.bitmex.client.BitmexSignatureGenerator;
import com.github.misterchangray.libs.bitmex.client.ISignatureGenerator;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by riecard on 2018/5/29.
 */
public class SignUtil {

    final Logger log = LoggerFactory.getLogger(getClass());

    static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    static final ZoneId ZONE_GMT = ZoneId.of("Z");

    public static String fcoinSign(String ak, String sk, String method, String uri, Map<String, String> params, Long ts) {
        StringBuffer sb = new StringBuffer();
        sb.append(method);
        sb.append(uri);
        sb.append(ts);
        String s = null;
        if (params != null) {
            Set<String> set = new TreeSet<>();
            set.addAll(params.keySet());
            for (String key:set) {
                sb.append(key);
                sb.append("=");
                sb.append(params.get(key));
                sb.append("&");
            }
            s = sb.substring(0,sb.length()-1);
        } else {
            s = sb.toString();
        }

        System.out.println(s);
        s = new String(Base64.encodeBase64(s.getBytes()));
        s = genHMAC1(s, sk);
        return s;
    }

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    /**
     * 使用 HMAC-SHA1 签名方法对data进行签名
     *
     * @param data
     *            被签名的字符串
     * @param key
     *            密钥
     * @return
    加密后的字符串
     */
    public static String genHMAC1(String data, String key) {
        byte[] result = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = Base64.encodeBase64(rawHmac);

        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println(e.getMessage());
        }
        if (null != result) {
            return new String(result);
        } else {
            return null;
        }
    }

    public static String okexSign(String sk, Map<String, String> params) {
        Set<String> set = new TreeSet<>();
        set.addAll(params.keySet());
        StringBuffer sb = new StringBuffer();
        for (String key:set) {
            sb.append(key);
            sb.append("=");
            sb.append(params.get(key));
            sb.append("&");
        }
        sb.append("secret_key=");
        sb.append(sk);
        return CryptoUtils.encodeMD5(sb.toString()).toUpperCase();
    }

    public static String bitmexSign(String method, String api, String time, Map<String, String> query, Map<String, String> params, String aKey, String sKey) {
        StringBuilder stringBuilder = new StringBuilder();
        api = HttpUtilManager.getInstance().makeQueryUrl(api, query);

        if(null != params) {
            for(String t : params.keySet()) {
                stringBuilder.append(MessageFormat.format("{0}={1}&", t, params.get(t)));
            }
        }
        String res = null;
        if(stringBuilder.length() > 1) {
            res = stringBuilder.substring(0, stringBuilder.length() - 1);
        } else {
            res = "";
        }

        ISignatureGenerator signatureGenerator = new BitmexSignatureGenerator();
        return signatureGenerator.generateSignature(sKey, method.toUpperCase(), api, Integer.parseInt(time), res);

    }

    /**
     * 创建�?个有效的签名。该方法为客户端调用，将在传入的params中添加AccessKeyId、Timestamp、SignatureVersion、SignatureMethod、Signature参数�?
     *
     * @param appKey       AppKeyId.
     * @param appSecretKey AppKeySecret.
     * @param method       请求方法�?"GET"�?"POST"
     * @param host         请求域名，例�?"be.huobi.com"
     * @param uri          请求路径，注意不�??以及后的参数，例�?"/v1/api/info"
     * @param params       原始请求参数，以Key-Value存储，注意Value不要编码
     */
    public static String huobiSign(String appKey, String appSecretKey, String method, String host,
                                   String uri, Map <String, String> params) {
        String sign = null;
        StringBuilder sb = new StringBuilder(1024);
        sb.append(method.toUpperCase()).append('\n') // GET
                .append(host.toLowerCase()).append('\n') // Host
                .append(uri).append('\n'); // /path
        params.remove("Signature");
        params.put("AccessKeyId", appKey);
        params.put("SignatureVersion", "2");
        params.put("SignatureMethod", "HmacSHA256");
        params.put("Timestamp", gmtNow());
        // build signature:
        SortedMap<String, String> map = new TreeMap<>(params);
        for (Map.Entry <String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append('=').append(urlEncode(value)).append('&');
        }
        // remove last '&':
        sb.deleteCharAt(sb.length() - 1);
        // sign:
        Mac hmacSha256 = null;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secKey =
                    new SecretKeySpec(appSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key: " + e.getMessage());
        }
        String payload = sb.toString();
        byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String actualSign = Base64.encodeBase64String(hash);
        params.put("Signature", actualSign);
        return actualSign;
    }

    public static String gmtNow() {
        return Instant.ofEpochSecond(epochNow()).atZone(ZONE_GMT).format(DT_FORMAT);
    }
    public static long epochNow() {
        return Instant.now().getEpochSecond();
    }

    public static String toQueryString(Map<String, String> params) {
        return String.join("&", params.entrySet().stream().map((entry) -> {
            return entry.getKey() + "=" + urlEncode(entry.getValue());
        }).collect(Collectors.toList()));
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported!");
        }
    }

}
