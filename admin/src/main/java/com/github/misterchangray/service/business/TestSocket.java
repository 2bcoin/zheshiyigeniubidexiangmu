package com.github.misterchangray.service.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.TAConfigs;
import com.neovisionaries.ws.client.*;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSocket {
    static Logger  logger = LoggerFactory.getLogger(TestSocket.class);

    public static String makeName(String type) {
        return type.replace("_", "").replace("usdt", "usd");
    }

    private static Map<String, Double> bids = new HashMap<>();
    private static Map<String, Double> asks = new HashMap<>();

    public static WebSocketFactory webSocketFactory = new WebSocketFactory();


//    public static void main(String[] a) {
//           JSExecutor jsExecutor = JSExecutor.init();
//           jsExecutor.runScript("function main(v) { Log(2);Log(Sleep(2000));Log(v)}");
//    }

    public static void main99(String[] a) {
        String type = "btc_usdt";
        Double amount = 0.023d;
        Double price = null;

//        String command = MessageFormat.format("{0}, {1}", 0 ,1);
        String command = null;
        if(null == price) command =String.format("[0,\"on\",null,{\"gid\": %d,\"cid\": %d,\"type\": \"%s\",\"symbol\": \"%s\",\"amount\": %f}]",
                0, System.currentTimeMillis(), "MARKET", makeName(type), amount);;
        if(null != price) command = String.format("[0,\"on\",null,{\"gid\": %d,\"cid\": %d,\"type\": \"%s\",\"symbol\": \"%s\",\"amount\": %f,\"price\": %f}]",
                0, System.currentTimeMillis(), "LIMIT", makeName(type), amount, price);


        System.out.println(command);
    }
    public static void main12(String[] a) {
        //格式化小数输出
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setRoundingMode(RoundingMode.DOWN);
        BigDecimal t= BigDecimal.valueOf(6);

        BigDecimal t2 = BigDecimal.valueOf(3);
        nf.setMaximumFractionDigits(0);
        System.out.println(t.compareTo(t2));
//        nf.setRoundingMode(RoundingMode.DOWN);
//        nf.setMaximumFractionDigits(6);
//        System.out.println(nf.format(t));
//        nf.setRoundingMode(RoundingMode.UP);
//        nf.setMaximumFractionDigits(5);
//        System.out.println(nf.format(t));

    }
    public static void main3(String[] a) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";


        //对websocket启用代理
        ProxySettings settings = webSocketFactory.getProxySettings();
        settings.setHost(proxyHost);
        settings.setPort(Integer.parseInt(proxyPort));
        //对HTTP启用代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对https开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        // 对SOCKS开启代理
        System.setProperty("socks.proxyHost", proxyHost);
        System.setProperty("socks.proxyPort", proxyPort);

        /**
         * regname: (unable to decode value)
         * regpwd: Zr12356789
         * regpwdrepeat: Zr12356789
         * regemail: misterchangray@hotmail.com
         * invcode: zxczxczasd
         * forward:
         * step: 2
         */
        String tmp = "becb7751b";
        for(int i=0;i<10; i++) {
            String invcode = tmp.replace("#", String.valueOf(i));
//            System.out.println(invcode);
//            if(true) continue;
            // 通过继承Thread类
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        MapBuilder b =  MapBuilder.build()
                                .add("regname", "笔写曾经").add("regpwd","Zr12356789").add("regpwdrepeat", "Zr12356789")
                                .add("regemail", "misterchangray@hotmail.com").add("invcode", invcode)
                                .add("step", "2").add("forward", "");
                        String res = HttpUtilManager.getInstance().requestHttpPost("http://cc.flexui.win/register.php?",b ,null,
                                MapBuilder.build().add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                        );
                        res = new String(res.getBytes("iso8859-1"), "gbk");
                        Pattern pattern = Pattern.compile("<center><br /><br /><br />(.*)<br />");
                        Matcher m3=pattern.matcher(res);
                        while (m3.find()) {
                            System.out.println(m3.group(1)+ JSONUtils.obj2json(b));
                        }

                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

        }
    }
























    //转换三角交易配置i

    public static void convert3(File f) {
        StringBuilder stringBuilder = new StringBuilder();

        String s = readToString(f.getPath());
        String tmp[] = s.split("\r\n");
        String[] title = null;

        List<TAConfigs> taConfigs = new ArrayList<>();
        for(int i=0;i < tmp.length; i++) {
            String line = tmp[i];

            String[] items = line.split("\\s+");
            if(!line.contains("/")) {
                title =  items;
            } else if(line.contains("/")) {
                TAConfigs taConfigs1 = new TAConfigs();
                for(int j=0; j<items.length; j++) {
                    OrgsInfoEnum org = null;
                    if(title[j].equals("OKEX")) {
                        org = OrgsInfoEnum.OKEX;
                    } else if(title[j].equals("HB")) {
                        org = OrgsInfoEnum.HuoBi;
                    } else if(title[j].equals("币安")) {
                        org = OrgsInfoEnum.BiAn;
                    }
                    taConfigs1.add(TAConfigs.build(org, items[j].replace("/","_").toLowerCase(), 4, 8, 0.002));
                    if((j+1)%3 == 0 ) {
                        taConfigs.add(taConfigs1);
                        taConfigs1 = new TAConfigs();
                    }
                }
            }
        }
        for(TAConfigs t : taConfigs) {
            t.sortTa();
            t.resetTaInfo();
        }
        for(TAConfigs t : taConfigs) {
            System.out.println(JSONUtils.obj2json(t));
        }
    }



    public static void main(String[] a) {
                File file = new File("C:\\Users\\admin\\Desktop\\策略\\三角小币种.txt");
                TestSocket.convert3(file);



                for(String key : TestSocket.tmp.keySet()) {
                    System.out.println(key);
                    for (int i=0; i< TestSocket.tmp.get(key).size(); i++) {
                        System.out.println(TestSocket.tmp.get(key).get(i));
                    }
                }

    }

    public static void convertLog() {
                File file = new File("C:\\Users\\admin\\Desktop\\日志\\日本_2018-08-13_22-29-46.log");
                TestSocket.convert2(file);
        //
        //        File file2 = new File("C:\\Users\\admin\\Desktop\\日志\\日本_2018-08-14_13-29-55.log");
        //        TestSocket.convert(file2);
        //
        //
//        File file3 = new File("C:\\Users\\admin\\Desktop\\日志\\日本_2018-08-14_18-48-08.log");
//        TestSocket.convert(file3);
    }



    private static Map<String, List<String>> tmp = new HashMap<>();
    public static void convert2(File f) {
        String s = readToString(f.getPath());
        String tmp[] = s.split("\r\n");

        for(int i=0;i < tmp.length; i++) {
            String line = tmp[i];
            if(line.length() < 10) continue;
//            if(line.split(";")[1].length() <= 1) {
//                System.out.println(line);
//                return;
//            }
//            System.out.println(i);
            String orgs = line.split(";")[1];
            orgs = orgs.replace("=", "\"=\"")
                    .replace("{", "{\"").replace("}", "\"}")
                    .replace(", ", "\",\"").replace("}\",\"", "},")
                    .replace("=", ":");
            orgs = "[" + orgs + "]";
            JsonNode jsonNode = JSONUtils.buildJsonNode(orgs);

            String date = line.split("  INFO TAService")[0];
            date = date.substring(0, date.length ()- 4);
            String lr = line.split("模拟利润为:")[1].split(";")[0].replace(",", ";");
            String key = null;

//            logger.info("{}", i);
            if(line.contains("买")) {
                String gs = line.split(" - 1,")[1].split(",")[0];
                BigDecimal lv = BigDecimalUtils.div(BigDecimal.valueOf(Double.valueOf(lr.split(";")[0])),BigDecimal.valueOf(Double.valueOf( gs.split("\\*")[0].split("=")[1])));

                key =   jsonNode.get(2).get("name").asText() + ",买入," + jsonNode.get(2).get("coin").asText() + ";" +
                        jsonNode.get(0).get("name").asText() + ",卖出," + jsonNode.get(0).get("coin").asText() +";" +
                        jsonNode.get(1).get("name").asText() + ",买入," + jsonNode.get(1).get("coin").asText();
                List<String> t = TestSocket.tmp.get(key);
                if(null == t) t = new ArrayList<String>();
                t.add(date + ";" + lr + ";" + lv.doubleValue());
                TestSocket.tmp.put(key, t);

            } else if(line.contains("卖")) {

                String gs = line.split(" - 1,")[1].split(",")[0];
                BigDecimal lv = BigDecimalUtils.div(BigDecimal.valueOf(Double.valueOf(lr.split(";")[0])),BigDecimal.valueOf(Double.valueOf( gs.split("\\*")[1])));


                key =   jsonNode.get(2).get("name").asText() + ",卖出," + jsonNode.get(2).get("coin").asText() + ";" +
                        jsonNode.get(1).get("name").asText() + ",卖出," + jsonNode.get(1).get("coin").asText() +";" +
                        jsonNode.get(0).get("name").asText() + ",买入," + jsonNode.get(0).get("coin").asText();
                List<String> t = TestSocket.tmp.get(key);
                if(null == t) t = new ArrayList<String>();
                t.add(date + ";" + lr + ";" + lv.doubleValue());
                TestSocket.tmp.put(key, t);

            }
        }
    }

    public static void convert(File f) {
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(f.getParent() + "1.txt");

        String s = readToString(f.getPath());
        String tmp[] = s.split("\r\n");

        for(int i=0;i < tmp.length; i++) {
            String line = tmp[i];
            if(line.length() < 10) continue;
//            if(line.split(";")[1].length() <= 1) {
//                System.out.println(line);
//                return;
//            }
//            System.out.println(i);
            String orgs = line.split(";")[1];
            orgs = orgs.replace("=", "\"=\"")
                    .replace("{", "{\"").replace("}", "\"}")
                    .replace(", ", "\",\"").replace("}\",\"", "},")
                    .replace("=", ":");
            orgs = "[" + orgs + "]";
            JsonNode jsonNode = JSONUtils.buildJsonNode(orgs);

            if(line.contains("买")) {
                String t2 = line.split(" - ")[1].split(", 买")[0];
                String[] t3 = t2.split(",");

                logger.info("{}个{}; 买入{}(数量:{}, 价格:{}), 卖出{}(数量:{}, 价格:{}), 买入{}(数量:{}, 价格:{});平台与交易对1,{}:{}, 2,{}:{}, 3{}:{}",
                        t3[1].split("=")[1].split("\\*")[0],
                        jsonNode.get(2).get("coin").asText(),
                        jsonNode.get(2).get("coin").asText(),
                        t3[1].split("=")[1].split("\\*")[1],
                        t3[1].split("=")[1].split("\\*")[0],
                        jsonNode.get(0).get("coin").asText(),
                        t3[3].split("=")[1].split("\\*")[0],
                        t3[3].split("=")[1].split("\\*")[1],
                        jsonNode.get(1).get("coin").asText(),
                        t3[5].split("=")[0],
                        t3[5].split("=")[1].split("/")[1],
                        jsonNode.get(2).get("coin").asText(),
                        jsonNode.get(2).get("name").asText(),
                        jsonNode.get(1).get("coin").asText(),
                        jsonNode.get(1).get("name").asText(),
                        jsonNode.get(0).get("coin").asText(),
                        jsonNode.get(0).get("name").asText()
                );
            } else if(line.contains("卖")) {
                String t2 = line.split(" - ")[1].split(", 卖")[0];
                String[] t3 = t2.split(",");


                logger.info("{}个{}; 卖出{}(数量:{}, 价格:{}), 卖出{}(数量:{}, 价格:{}), 买入{}(数量:{}, 价格:{});平台与交易对1,{}:{}, 2,{}:{}, 3{}:{}",
                        t3[1].split("=")[1].split("\\*")[0],
                        jsonNode.get(2).get("coin").asText(),
                        jsonNode.get(2).get("coin").asText(),
                        t3[1].split("=")[1].split("\\*")[0],
                        t3[1].split("=")[1].split("\\*")[1],
                        jsonNode.get(1).get("coin").asText(),
                        t3[3].split("=")[1].split("\\*")[0],
                        t3[3].split("=")[1].split("\\*")[1],
                        jsonNode.get(0).get("coin").asText(),
                        t3[5].split("=")[0],
                        t3[5].split("=")[1].split("/")[1],
                        jsonNode.get(2).get("coin").asText(),
                        jsonNode.get(2).get("name").asText(),
                        jsonNode.get(1).get("coin").asText(),
                        jsonNode.get(1).get("name").asText(),
                        jsonNode.get(0).get("coin").asText(),
                        jsonNode.get(0).get("name").asText()

                );
            }
        }
    }


    public static String readToString(String fileName) {
        String encoding = "utf8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
}
