package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.service.business.TestSocket;
import com.github.misterchangray.service.jsexcutor.vo.FMZTicker;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String a[]) {
        System.out.println(JSONUtils.obj2json(new FMZTicker()));;
//        String s = TestSocket.readToString("C:\\Users\\admin\\Desktop\\test.js");
//        JSExecutor.init().runScript(s);a

        List<String> test = new ArrayList<String>(){{
            this.add("lixue");
            this.add("zhangrui");
            this.add("xuyanru");
            this.add("xuyanru");
        }};

//        System.out.println(test.stream().filter(item -> item.contains("zhang")).findFirst());;
        System.out.println(
        test.stream().filter((i) -> {
            if(i.contains("x")) {
                return true;
            }
            return false;
        }).skip(1).distinct().findFirst().map(item -> item.toUpperCase()).get()
        );;
    }


}
