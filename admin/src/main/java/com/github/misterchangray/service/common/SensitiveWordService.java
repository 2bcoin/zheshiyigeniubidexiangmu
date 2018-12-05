package com.github.misterchangray.service.common;

import com.github.misterchangray.common.quartz.GlobalQuartz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 *  DFA算法 敏感词过滤
 * @author Created by rui.zhang on 2018/6/25.
 * @author rui.zhang
 * @version ver1.0
 * @email misterchangray@hotmail.com
 * @description
 */
@Component
public class SensitiveWordService {
    private Map sensitiveWordMap = null;    //DNF关键词模型
    private static final String ENCODING = "utf-8"; //字符编码
    private Logger logger = LoggerFactory.getLogger(GlobalQuartz.class);

    public static void main(String args[]) {
        SensitiveWordService sensitiveWordService = new SensitiveWordService();
        sensitiveWordService.initKeyWord();

        long start = System.currentTimeMillis();
        String string = "太多的伤感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
                + "然后法轮功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
                + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。";
        string = "计算机（computer）俗称电脑，是现代一种用于高速计算的电子计算机器，三级片通常指含有色情" +
                "暴露镜头的电影，源自香港在八十年代末起进行的电影分级制。自1988年底香港实行电影分级制度以来，对第三级影片的" +
                "划分标准除了裸露镜头外又可以进行逻辑计算，还具有自杀功能。是能够按照法轮功程序运行，自动、高速处理海量黄色电影";

        System.out.println(sensitiveWordService.getSensitiveWord(string, 0));
        System.out.println(sensitiveWordService.replaceSensitiveWord(string, 0, "*"));
        System.out.println("time--" + ( System.currentTimeMillis() - start));
    }


    /**
     * 初始化DNF关键词模型;全局仅用初始化一次
     * @return
     * @throws Exception
     * @since 1.8
     * @author whb
     */
    public void initKeyWord() {
        if(null != sensitiveWordMap) return;

        try {
            initSensitiveWordToHashMap(readSensitiveWordFromFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 目前从文件中初始化词库;可以替换为数据库中初始化
     * 从文件中读取敏感词库
     * @return
     * @throws Exception
     * @since 1.8
     * @author whb
     */
    private Set<String> readSensitiveWordFromFile() throws Exception {
        Set<String> set = null;
        File file = new File("D:\\workspace\\common-mvc\\src\\main\\resources\\sensitiveWord.txt");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), ENCODING))) {
            if (file.isFile() && file.exists()) {
                set = new HashSet<String>();
                String txt = null;
                while ((txt = bufferedReader.readLine()) != null) {
                    set.add(txt);
                }
            } else {
                throw new Exception("敏感词库文件不存在");
            }
            logger.info("初始化敏感词库成功,共初始化 {} 个敏感词条", set.size());
// 将去重的敏感词再写入文件
//
//                try {
//                    File file2 = new File("D:\\workspace\\common-mvc\\src\\main\\resources\\sensitiveWord2.txt");
//                    PrintStream ps = new PrintStream(new FileOutputStream(file2));
//                    Iterator<String> iterator = set.iterator();
//                    while (iterator.hasNext()) {
//                      ps.println(iterator.next());
//                    }
//                    ps.flush();
//                    ps.close();
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return set;

    }


    /**
     * 替换内容中那些敏感词汇
     * @param text 待替换内容
     * @param startIndex    开始替换索引
     * @param replaceStr    被替换符号
     * @return
     */
    public String replaceSensitiveWord(String text, Integer startIndex, String replaceStr) {
        if(null == startIndex) startIndex = 0;
        if(null == replaceStr) replaceStr = "*";

        StringBuilder stringBuilder = new StringBuilder();
        Map sensitiveMap = sensitiveWordMap;
        if(null != text &&  startIndex < text.length()) {
            String sensitiveWord = "";
            char tmp;
            for(int i=startIndex, j=text.length(); i<j; i++) {//从指定位置开始遍历字符串
                tmp = text.charAt(i);

                if(null != sensitiveMap.get(tmp)) { //如果包含敏感词则利用 sensitiveMap 进一步的匹配
                    sensitiveMap = (Map) sensitiveMap.get(tmp);
                    sensitiveWord += tmp;
                    if(isWordEnd(sensitiveMap)) { //已遍历一个完整词汇则添加
                        sensitiveWord = "";
                        stringBuilder.append(replaceStr);
                    }
                } else {
                    if(0 != sensitiveWord.length()){
                        i--; //如果在子集中未找到敏感词;则在根集合中重新查找此字符
                        stringBuilder.append(sensitiveWord);
                    } else {
                        stringBuilder.append(tmp);
                    }
                    sensitiveWord = "";
                    sensitiveMap = sensitiveWordMap;
                }
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 获取内容中有那些敏感词汇
     * @param text 待获取词汇内容
     * @param startIndex 内容起始获取下标
     * @return 敏感词汇集
     */
    public Set<String> getSensitiveWord(String text, Integer startIndex) {
        if(null == startIndex) startIndex = 0;

        Set<String> sensitiveWords = new HashSet();//储存待返回的敏感词汇集
        Map sensitiveMap = sensitiveWordMap;
        if(null != text &&  startIndex < text.length()) {
            String sensitiveWord = "";
            char tmp;
            for(int i=startIndex, j=text.length(); i<j; i++) {//从指定位置开始遍历字符串
                tmp = text.charAt(i);
                if(null != sensitiveMap.get(tmp)) { //如果包含敏感词则利用 sensitiveMap 进一步的匹配
                    sensitiveMap = (Map) sensitiveMap.get(tmp);
                    sensitiveWord += tmp;
                    if(isWordEnd(sensitiveMap)) { //已遍历一个完整词汇则添加
                        sensitiveWords.add(sensitiveWord);
                    }
                } else {
                    if(0 != sensitiveWord.length()) i--; //如果在子集中未找到敏感词;则在根集合中重新查找此字符
                    sensitiveWord = "";
                    sensitiveMap = sensitiveWordMap;
                }
            }
        }
        return sensitiveWords;
    }


    private boolean isWordEnd(Map map) {
        if(null != map.get("isWordEnd") && (true == (Boolean) map.get("isWordEnd"))) {
            return true;
        }
        return false;
    }

    /**
     * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型<br>
     * @param keyWordList
     * @since 1.8
     * @author whb
     */
    private void initSensitiveWordToHashMap(Set<String> keyWordList) {
        sensitiveWordMap = new HashMap(keyWordList.size()); //初始化敏感词容器，避免扩容操作
        String key = null;
        Map nowMap = null;
        Map<String, Boolean> newWorMap = null;
        Iterator<String> iterator = keyWordList.iterator();
        while (iterator.hasNext()) {
            key = iterator.next();
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                char charKey = key.charAt(i); //转换成char型
                Object wordMap = nowMap.get(charKey);
                if (wordMap != null) {
                    nowMap = (Map) wordMap; //一个一个放进Map中
                } else { //不存在，则构建一个Map,同时将isEnd设置为0，因为它不是最后一个
                    newWorMap = new HashMap<String, Boolean>();
                    newWorMap.put("isEnd", false);//不是最后一个
                    nowMap.put(charKey, newWorMap);//没有这个key，就把(isEnd，0) 放在Map中
                    nowMap = newWorMap;
                }
                if (i == key.length() - 1) { //最后一个
                    nowMap.put("isEnd", true);
                    nowMap.put("isWordEnd", true);
                }
            }
        }
    }



}
