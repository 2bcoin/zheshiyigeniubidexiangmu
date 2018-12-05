package com.github.misterchangray.service.jsexcutor.util;

import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.common.utils.ListBuilder;
import org.knowm.xchange.currency.CurrencyPair;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Utils {

    public static void main(String a[]) {

//        for(int i=0; i<100; i++) {
//                    writeToFile(ListBuilder.build().append(new Logger(Logger.Level.Error, String.valueOf(i), String.valueOf(i), String.valueOf(i), String.valueOf(i)))
//                .append(new Logger(Logger.Level.Error, String.valueOf(i), String.valueOf(i), String.valueOf(i), String.valueOf(i))), "C:\\Users\\admin\\Desktop\\test22.json");
//        }


        List<Logger> t = getLogToFile("C:\\Users\\admin\\Desktop\\test22.json", 4l);
        System.out.println(t.size());
    }


    public static List<Logger> getLogToFile(String filePathName, Long lineNum) {
        String charSet = "UTF-8";
        //读取字节转换到字符
        FileInputStream fileInputStream = null;
        InputStreamReader reader = null;
        StringBuilder builder = new StringBuilder();
        Long count = 0l;
        if (null == lineNum || 0 == lineNum || 1 == lineNum) lineNum = 1l;
        try {
            fileInputStream = new FileInputStream(filePathName);
            reader = new InputStreamReader(fileInputStream, charSet);
            BufferedReader bufferedReader = new BufferedReader(reader);
            Stream<String> s = bufferedReader.lines();
            s = s.skip(lineNum - 1);
            String res = s.findFirst().get();
            if (null != res && res.length() > 2) {
                builder.append("[");
                builder.append(res.substring(1, res.length()));
                builder.append("]");
                List<Logger> tmp = JSONUtils.json2list(builder.toString(), Logger.class);
                res = null;
                return tmp;
            }
        } catch (Exception e) {
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static void writeToFile(List<Logger> b, String filePathName) {
        if (null == b) return;
        String file = filePathName;
        String charSet = "UTF-8";
        //写字符转换成字节流
        FileOutputStream fileWriter = null;
        OutputStreamWriter writer = null;
        try {
            File file1 = new File(filePathName);
            if(!file1.exists()) {
                file1.getParentFile().mkdirs();
                file1.createNewFile();
            }
            //写字符转换成字节流
            fileWriter = new FileOutputStream(file1, true);
            writer = new OutputStreamWriter(fileWriter, charSet);
            String s = JSONUtils.obj2json(b);
            if (s.length() > 100) {
                writer.append("," + s.substring(1, s.length() - 1));
                writer.append("\r\n");
            }
            s = null;
        } catch (Exception e) {
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static BigDecimal convertBigDecimal(String b) {
        if (null == b) return null;
        try {
            return BigDecimal.valueOf(Double.valueOf(b));
        } catch (Exception ae) {
            ae.printStackTrace();
            return null;
        }
    }

    public static Integer convertInt(String b) {
        if (null == b) return null;
        try {
            return Integer.parseInt((b));
        } catch (Exception ae) {
            ae.printStackTrace();
            return null;
        }
    }

    public static BigDecimal convertBigDecimal(Integer b) {
        if (null == b) return null;
        try {
            return BigDecimal.valueOf(Double.valueOf(b));
        } catch (Exception ae) {
            ae.printStackTrace();
            return null;
        }
    }

    public static BigDecimal abs(BigDecimal b) {
        if (null == b) return null;
        try {
            return convertBigDecimal(Math.abs(b.doubleValue()));
        } catch (Exception ae) {
            ae.printStackTrace();
            return null;
        }
    }

    public static BigDecimal convertBigDecimal(Double b) {
        if (null == b) return null;
        try {
            return BigDecimal.valueOf(b);
        } catch (Exception ae) {
            ae.printStackTrace();
            return null;
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
