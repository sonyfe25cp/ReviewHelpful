package com.omartech.review.modules.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by OmarTech on 15-11-23.
 */
public class NewWordDetection {
    static Logger logger = LoggerFactory.getLogger(NewWordDetection.class);


    static List<String> findPotentialWords(String body, int n) {

        List<String> potentialWords = new ArrayList<>();

        List<String> sentences = cutSentences(body);
        List<String> allSubSentences = new ArrayList<>();
        for (String sentence : sentences) {
            List<String> subSentences = subSentence(sentence, n + 1);
            allSubSentences.addAll(subSentences);
        }

        logger.info("====================================");

        Collections.sort(allSubSentences);
        for (String str : allSubSentences) {
            logger.info(str);
        }

        logger.info("====================================");
        int totalWordCount = allSubSentences.size();
        logger.info("总字数: {}", totalWordCount);
        Map<String, Integer> subStringCountMap = extractCount(n, false, allSubSentences);
        Map<String, Integer> subStringPrefixCountMap = extractCount(n, true, allSubSentences);


        List<String> sortTempList = new ArrayList<>(subStringCountMap.keySet());
        Collections.sort(sortTempList);
        for (String key : sortTempList) {
            logger.info("{} count: {}, {} is prefix count: {}", new String[]{key, subStringCountMap.get(key) + "", key, subStringPrefixCountMap.get(key) + ""});
        }

        return potentialWords;
    }

    private static Map<String, Integer> extractCount(int n, boolean prefix, List<String> allSubSentences) {


        logger.info("====================================");
        if (prefix) {
            logger.info("查找长度小于{}的字符串作为前缀出现的次数", n);
        } else {
            logger.info("查找长度小于{}的字符串出现的次数", n);
        }

        Map<String, Integer> subStringCountMap = new HashMap<>();
        //统计长度小于等于n的词语出现的次数
        for (int i = 1; i <= n; i++) {

            int begin = 0;
            int end = 1;
            int times = 0;
            while (begin < allSubSentences.size()) {

                String beginSentence = allSubSentences.get(begin);

                if (beginSentence.length() < i) {
                    begin++;
                    end++;
                    times = 0;
                    continue;
                }

                String beginStr = beginSentence.substring(0, i);

                String endSentence = allSubSentences.get(end);

                if (endSentence.length() < i) {
                    begin = end;
                    end++;
                    subStringCountMap.put(beginStr, times);
                    times = 0;
                    continue;
                }

                String endStr = endSentence;
                if (prefix) {
                    int endLength = endStr.length();
                    int beginLength = beginStr.length();
                    if ((endLength > beginLength) && endStr.startsWith(beginStr)) {
                        end++;
                        times++;
                        logger.info(beginStr + " : " + times);
                        subStringCountMap.put(beginStr, times);
                    } else {
                        begin = end;
                        end++;
                        times = 0;
                    }
                } else {
                    endStr = endSentence.substring(0, i);
                    if (beginStr.equals(endStr)) {
                        end++;
                        times++;
                        logger.info(beginStr + " : " + times);
                        subStringCountMap.put(beginStr, times);
                    } else {
                        begin = end;
                        end++;
                        times = 0;
                    }
                }
            }
        }
        return subStringCountMap;
    }

    //拆分为句子
    private static List<String> cutSentences(String body) {
        String[] strings = body.split("[，。、？！\"：”“,. ]");
        List<String> list = Arrays.asList(strings);
        return list;
    }


    //把句子的后缀进行输出，最大长度为n
    static List<String> subSentence(String sentence, int n) {
        int length = sentence.length();
        List<String> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < i + n && j < length; j++) {
                sb.append(sentence.charAt(j));
            }
            String st = sb.toString();
            logger.info(st);
            list.add(st);
        }
        return list;
    }


    public static void main(String[] args) {
        String str = "我爱北京天安门，天安门上太阳升。我爱吃苹果，天安门有好多人在看天安门的桥。";
//        String str = "四是四十是十十四是十四 四十是四十";
        findPotentialWords(str, 5);
    }

}
