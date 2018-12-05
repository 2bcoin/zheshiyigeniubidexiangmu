package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.utils.HttpUtilManager;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.JsExcutorController;
import com.github.misterchangray.controller.ScriptDataController;
import com.github.misterchangray.controller.vo.Slave;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.jsexcutor.dto.RunningImage;
import com.github.misterchangray.service.platform.BiAnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class InitClient extends BaseService {
    Logger logger = LoggerFactory.getLogger(InitClient.class);
    public static String server; //是否为客户端

    @Value("${jsscript.server:none}")
    public void setServer(String server) {
        if("none".equalsIgnoreCase(server)) return;
        InitClient.server = server;
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {
        if (QuartzEnum.Second3 == quartzEnum) {
            if (null != server && server.length() > 6) {
                try {
                    String res = HttpUtilManager.getInstance().requestHttpGet(
                            MessageFormat.format("http://{0}/common-core/v1/scriptData/ip", server),
                            null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (QuartzEnum.Second3 == quartzEnum) {
            isOnline();
        }
        return true;
    }

    private void isOnline() {
        Set<Map.Entry<String, Slave>> set = ScriptDataController.ListIPAndScrpit.entrySet();
        Iterator<Map.Entry<String, Slave>> iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Slave> entry = iterator.next();
            String s = entry.getKey();
            try {
                String res = com.github.misterchangray.common.utils.HttpUtilManager.getInstance()
                        .requestHttpGet("http://" + s + ":8080/common-core/v1/jsexcutor/live", null, MapBuilder.build());
                if (res == null || "".equals(res)) {
                    if(null == ScriptDataController.ListIPAndScrpit.get(s)) continue;

                    int i = ScriptDataController.ListIPAndScrpit.get(s).getRetryCount();
                    if (i > 3) {
                        iterator.remove();
                    } else {
                        ScriptDataController.ListIPAndScrpit.get(s).setRetryCount(++i);
                    }
                } else {
                    if(null !=  ScriptDataController.ListIPAndScrpit.get(s)) {
                        ScriptDataController.ListIPAndScrpit.get(s).setRetryCount(0);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void pushStatusToCenterServer(RunningImage runningImage ) {
        if(null != server && server.length() > 5) {
            String res = HttpUtilManager.getInstance().requestHttpPost(
                    MessageFormat.format("http://{0}/common-core/v1/scriptData/shutThread", server),
                    MapBuilder.build().add("tId", runningImage.gettId()), null, null);
            JsExcutorController.lastPostData = "";
            if(null == res) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pushStatusToCenterServer(runningImage);
            }
        }

    }
}
