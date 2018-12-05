package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.service.business.TestSocket;
import com.github.misterchangray.service.jsexcutor.vo.FMZTicker;

import java.text.MessageFormat;

public class Test {
    public static void main(String a[]) {
        System.out.println(JSONUtils.obj2json(new FMZTicker()));;
        String s = TestSocket.readToString("C:\\Users\\admin\\Desktop\\test.js");
//        JSExecutor.init().runScript(s);

        System.out.println(MessageFormat.format("'{'\"a\":{0}'}'", 1));
    }


}
