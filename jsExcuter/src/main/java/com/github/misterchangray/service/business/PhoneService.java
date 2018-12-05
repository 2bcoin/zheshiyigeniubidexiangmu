package com.github.misterchangray.service.business;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsRequest;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.stereotype.Service;

/**
 * Created by riecard on 2018/11/3.
 */
@Service
public class PhoneService {

    //云通信产品-语音API服务产品名称（产品名固定，无需修改）
    final String product = "Dyvmsapi";
    //产品域名（接口地址固定，无需修改）
    final String domain = "dyvmsapi.aliyuncs.com";
    //AK信息
    final String accessKeyId = "";
    final String accessKeySecret = "";


    /**
     * ${name}请注意，${coin}产品的价格波动较大，${upordown}了百分之${per}，请登录运维平台查看。
     */
    public void call(String phone, String name, String coin, String upordown,String per) {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient 暂时不支持多region
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SingleCallByTtsRequest request = new SingleCallByTtsRequest();
        //必填-被叫显号,可在语音控制台中找到所购买的显号
        request.setCalledShowNumber("02867876096");
        //必填-被叫号码
        request.setCalledNumber(phone);
        //必填-Tts模板ID
        request.setTtsCode("TTS_150574363");
        //可选-当模板中存在变量时需要设置此值  ${name}，${suo}的${icon}
        request.setTtsParam("{\"name\":\""+name+"\",\"coin\":\""+coin+"\",\"upordown\":\""+upordown+"\",\"per\":\""+per+"\"}");
        //可选-音量 取值范围 0--200
        request.setVolume(100);
        //可选-播放次数
        request.setPlayTimes(3);
        //可选-外部扩展字段,此ID将在回执消息中带回给调用方
        request.setOutId("yourOutId");
        try {
            //hint 此处可能会抛出异常，注意catch
            SingleCallByTtsResponse singleCallByTtsResponse = acsClient.getAcsResponse(request);
            if(singleCallByTtsResponse.getCode() != null && singleCallByTtsResponse.getCode().equals("OK")) {
                //请求成功
                System.out.println("语音文本外呼---------------");
                System.out.println("RequestId=" + singleCallByTtsResponse.getRequestId());
                System.out.println("Code=" + singleCallByTtsResponse.getCode());
                System.out.println("Message=" + singleCallByTtsResponse.getMessage());
                System.out.println("CallId=" + singleCallByTtsResponse.getCallId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 尊敬的${name}，${suo}的${icon}数量已到临界值，请马上关注一下吧。
     */
    private void call(String phone, String name, String suo, String icon) {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient 暂时不支持多region
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SingleCallByTtsRequest request = new SingleCallByTtsRequest();
        //必填-被叫显号,可在语音控制台中找到所购买的显号
        request.setCalledShowNumber("02867876096");
        //必填-被叫号码
        request.setCalledNumber(phone);
        //必填-Tts模板ID
        request.setTtsCode("TTS_137427435");
        //可选-当模板中存在变量时需要设置此值  ${name}，${suo}的${icon}
        request.setTtsParam("{\"name\":\""+name+"\",\"suo\":\""+suo+"网\",\"icon\":\""+icon+"币\"}");
        //可选-音量 取值范围 0--200
        request.setVolume(100);
        //可选-播放次数
        request.setPlayTimes(3);
        //可选-外部扩展字段,此ID将在回执消息中带回给调用方
        request.setOutId("yourOutId");
        try {
            //hint 此处可能会抛出异常，注意catch
            SingleCallByTtsResponse singleCallByTtsResponse = acsClient.getAcsResponse(request);
            if(singleCallByTtsResponse.getCode() != null && singleCallByTtsResponse.getCode().equals("OK")) {
                //请求成功
                System.out.println("语音文本外呼---------------");
                System.out.println("RequestId=" + singleCallByTtsResponse.getRequestId());
                System.out.println("Code=" + singleCallByTtsResponse.getCode());
                System.out.println("Message=" + singleCallByTtsResponse.getMessage());
                System.out.println("CallId=" + singleCallByTtsResponse.getCallId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
