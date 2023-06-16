package com.frankcooper.sensitive.dfa;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Description: 敏感词过滤
 */
public class SensitiveWordFilter {

    private Map sensitiveWordMap = null;
    public static int minMatchTYpe = 1;      //最小匹配规则,查询到敏感词就返回
    public static int maxMatchType = 2;      //最大匹配规则

    /**
     * 构造函数，初始化敏感词库
     */
    public SensitiveWordFilter(){
        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
    }

    /**
     * 判断文字是否包含敏感字符
     * @param txt 文字
     * @param matchType
     * @return
     */
    public boolean containsSensitiveWord(String txt, int matchType){
        boolean flag = false;
        for(int i = 0 ; i < txt.length() ; i++){
            int matchFlag = this.checkSensitiveWord(txt, i, matchType); //判断是否包含敏感字符
            if(matchFlag > 0){    //大于0存在，返回true
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 获取文字中的敏感词
     */
    public Set<String> getSensitiveWord(String txt , int matchType){
        Set<String> sensitiveWordList = new HashSet<String>();

        for(int i = 0 ; i < txt.length() ; i++){
            int length = checkSensitiveWord(txt, i, matchType);    //判断是否包含敏感字符
            if(length > 0){    //存在,加入list中
                sensitiveWordList.add(txt.substring(i, i+length));
                i = i + length - 1;    //减1的原因，是因为for会自增
            }
        }

        return sensitiveWordList;
    }

    /**
     * 替换敏感字字符
     */
    public String replaceSensitiveWord(String txt,int matchType,String replaceChar){
        String resultTxt = txt;
        Set<String> set = getSensitiveWord(txt, matchType);     //获取所有的敏感词
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 获取替换字符串
     */
    private String getReplaceChars(String replaceChar,int length){
        String resultReplace = replaceChar;
        for(int i = 1 ; i < length ; i++){
            resultReplace += replaceChar;
        }

        return resultReplace;
    }

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：
     */
    public int checkSensitiveWord(String txt, int beginIndex, int matchType){
        boolean  flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况

        int matchFlag = 0;     //匹配标识数默认为0
        char word = 0;
        Map nowMap = sensitiveWordMap;
        for(int i = beginIndex; i < txt.length() ; i++){
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);     //获取指定key
            if(nowMap != null){     //存在，则判断是否为最后一个
                matchFlag++;     //找到相应key，匹配标识+1
                if("1".equals(nowMap.get("isEnd"))){       //如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag = true;       //结束标志位为true
                    if(SensitiveWordFilter.minMatchTYpe == matchType){    //最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            }else{     //不存在，直接返回
                break;
            }
        }
        if(matchFlag < 2 || !flag){        //长度必须大于等于1，为词
            matchFlag = 0;
        }
        return matchFlag;
    }

    public static void main(String[] args) throws IOException {
        SensitiveWordFilter filter = new SensitiveWordFilter();
        System.out.println("敏感词的数量：" + filter.sensitiveWordMap.size());
        List<String> chatTextList = FileUtils.readLines(new File("D:\\Dev\\Documents\\GFile\\dev\\snesitve_words\\chat_text.txt"), Charset.defaultCharset());
        for (String chatText : chatTextList) {
//            String string = "中央领导我的国家我的世界，车仑工力这是一种宗教";
//            System.out.println("待检测语句字数：" + string.length());
            long beginTime = System.currentTimeMillis();
            Set<String> set = filter.getSensitiveWord(chatText, 1);
            String txt=filter.replaceSensitiveWord(chatText,1,"*");
            long endTime = System.currentTimeMillis();
            System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
            System.out.println(txt);
            System.out.println("-----------------------------------------------------");
//            System.out.println("总共消耗时间为：" + (endTime - beginTime));
        }


    }
}
