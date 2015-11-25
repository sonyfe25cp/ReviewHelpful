package com.omartech.review.modules.app;

import com.omartech.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by OmarTech on 15-11-23.
 */
public class NewWordDetection {
    static Logger logger = LoggerFactory.getLogger(NewWordDetection.class);

    boolean info = true;
    boolean debug = false;

    String filePath = null;
    File dataFile;
    int wordLength = 5;


    File postfixFile;//以该词为开头的词的文件
    File postfixCountFile;//以该词为开头的词的个数

    File prefixFile;//以该词为结尾的词的文件
    File prefixCountFile;//以该词为结尾的词的个数

    File wordCountFile;//该词出现的次数
    int wordCount;//该文件的总字数

    File cleanFile;

    File wordNingjuFile;
    File wordEntropyFile;

    void init() {

        if (filePath == null) {
            logger.error("no input file");
            return;
        }
        dataFile = new File(filePath);

        String name = dataFile.getName();
        cleanFile = new File("output/clean-" + name);
        postfixFile = new File("output/postfix-" + name);
        prefixFile = new File("output/prefix-" + name);
        wordNingjuFile = new File("output/ningju-" + name);
        wordEntropyFile = new File("output/entropy-" + name);
        wordCountFile = new File("output/wordcount-"+name);
        prefixCountFile = new File("output/prefixCount-"+name);
        postfixCountFile = new File("output/postfixCount-"+name);

        logger.info("数据文件: {}", filePath);
        logger.info("清洗后的文件: {}", cleanFile.getAbsolutePath());
        logger.info("后缀表示文件: {}", postfixFile.getAbsolutePath());
        logger.info("前缀表示文件: {}", prefixFile.getAbsolutePath());
        logger.info("词语凝聚度文件: {}", wordNingjuFile.getAbsolutePath());
        logger.info("词语信息量文件: {}", wordEntropyFile.getAbsolutePath());
        logger.info("词语统计文件: {}", wordCountFile.getAbsolutePath());
        logger.info("该词为开头的数目文件: {}", postfixCountFile.getAbsolutePath());
        logger.info("该词为结尾的数目文件: {}", prefixCountFile.getAbsolutePath());
    }

    void process() {
        init();
        try {
            //清洗文件
            String text = cleanTxt(dataFile);
            FileUtils.write(cleanFile, text);
            logger.info("清洗文件 over");

            //生成以该词开头的文件
            List<String> postfixLines = generatePostfixFile(cleanFile);
            logger.info("生成以该词开头的文件 over");

            //生成以该词结尾的文件
            List<String> prefixLines = generatePrefixFile(cleanFile);
            logger.info("生成以该词结尾的文件 over");

            //统计长度小于等于n的词语，该词的个数
            Map<String, Integer> wordCountMap = extractCount(wordLength, false, postfixLines);//词，该词出现的次数
            writeStringIntegerMapIntoFile(wordCountMap, wordCountFile);
            wordCount = postfixLines.size();
            logger.info("统计长度小于等于n的词语，该词的个数 over");

            //统计长度小于等于n的词语，以该词为开头的词的个数
            Map<String, Integer> postfixCountMap = extractCount(wordLength, true, postfixLines);//词，该词出现的次数
            writeStringIntegerMapIntoFile(postfixCountMap, postfixCountFile);
            logger.info("统计长度小于等于n的词语，以该词为开头的词的个数 over");

            //统计长度小于等于n的词，以该词为结尾的词的个数
            Map<String, Integer> prefixCountMap = extractCount(wordLength, true, prefixLines);//词，该词出现的次数
            writeStringIntegerMapIntoFile(prefixCountMap, prefixCountFile);
            logger.info("统计长度小于等于n的词，以该词为结尾的词的个数 over");

            //计算内聚度
            Map<String, Double> ningjieduMap = computeNeijudu(wordCountMap, wordCount);
            writeStringDoubleMapIntoFile(ningjieduMap, wordNingjuFile);
            logger.info("计算内聚度 over");
            //计算信息熵


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Map<String, Double> computeNeijudu(Map<String, Integer> wordCountMap, int wordCount) {
        Map<String, Double> ningjieMap = new HashMap<>();
        for (String key : wordCountMap.keySet()) {
            if (key.length() > 1) {//从2个字的开始

                Integer count = wordCountMap.get(key);
                double pa = (double) count / wordCount;

                Map<String, String> children = cutStringIntoArray(key);
                double max = 0;
                for (Map.Entry<String, String> entry : children.entrySet()) {
                    String key1 = entry.getKey();
                    String value = entry.getValue();

                    double pkey1 = (double) wordCountMap.get(key1) / wordCount;
                    double pvalue = (double) wordCountMap.get(value) / wordCount;

                    double multi = pkey1 * pvalue;
                    if (multi >= max) {
                        max = multi;
                    }
                }

                double ningjiedu = pa / max;
                ningjieMap.put(key, ningjiedu);
            }
        }
        return ningjieMap;
    }

    private void writeStringDoubleMapIntoFile(Map<String, Double> subStringCountMap, File postfixCountFile) {
        try {
            for (Map.Entry<String, Double> entry : subStringCountMap.entrySet()) {
                FileUtils.write(postfixCountFile, entry.getKey() + " " + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStringIntegerMapIntoFile(Map<String, Integer> subStringCountMap, File postfixCountFile) {
        try {
            for (Map.Entry<String, Integer> entry : subStringCountMap.entrySet()) {
                FileUtils.write(postfixCountFile, entry.getKey() + " " + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    Pattern pattern = Pattern.compile("[^\\dA-Za-z\\u3007\\u3400-\\u4DB5\\u4E00-\\u9FCB\\uE815-\\uE864]");//去掉非汉字，英文，数字
    Pattern pattern = Pattern.compile("[^\\u3007\\u3400-\\u4DB5\\u4E00-\\u9FCB\\uE815-\\uE864]");//去掉非汉字

    private String cleanTxt(File input) throws IOException {
        String string = FileUtils.readFileToString(input);
        Matcher matcher = pattern.matcher(string);
        string = matcher.replaceAll(" ");
        string = string.replaceAll(" ", " ");

        return string;
    }

    private List<String> generatePrefixFile(File file) throws IOException {
        return generateXXfixFile(file, prefixFile, wordLength, true);
    }

    private List<String> generatePostfixFile(File file) throws IOException {
        return generateXXfixFile(file, postfixFile, wordLength, false);
    }

    static List<String> generateXXfixFile(File input, File output, int wordLength, boolean reverse) throws IOException {
        String body = FileUtils.readFileToString(input);
        List<String> sentences = cutSentences(body);
        List<String> allSubSentences = new ArrayList<>();
        for (String sentence : sentences) {
            if (reverse) {
                sentence = StringUtils.reverse(sentence);
            }
            sentence = StringUtils.trim(sentence);
            if (!StringUtils.isEmpty(sentence)) {
                List<String> subSentences = subSentence(sentence, wordLength + 1);
                allSubSentences.addAll(subSentences);
            }
        }
        Collections.sort(allSubSentences);
        FileUtils.writeLines(output, allSubSentences);
        return allSubSentences;
    }


    List<String> findPotentialWords(String body, int n) {


        List<String> potentialWords = new ArrayList<>();

        List<String> sentences = cutSentences(body);

//正向
        List<String> allSubSentences = new ArrayList<>();
        for (String sentence : sentences) {
            List<String> subSentences = subSentence(sentence, n + 1);
            allSubSentences.addAll(subSentences);
        }
        logger.info("====================================");
        Collections.sort(allSubSentences);

        if (debug) {
            for (String str : allSubSentences) {
                logger.info(str);
            }
        }
        logger.info("====================================");
        int totalWordCount = allSubSentences.size();
        logger.info("总字数: {}", totalWordCount);
        Map<String, Integer> subStringCountMap = extractCount(n, false, allSubSentences);//词，该词出现的次数
        Map<String, Integer> subStringPostfixCountMap = extractCount(n, true, allSubSentences);//词，该词作为前缀出现的次数

        if (debug) {
            logger.info("============词出现的次数========================");
            bianliMap(subStringCountMap);

            logger.info("=============以该词为前缀的词个数=======================");
            bianliMap(subStringPostfixCountMap);
        }
        logger.info("=================开始计算逆向部分===================");
        //逆向
        List<String> allReverseSubSentences = new ArrayList<>();
        for (String sentence : sentences) {
            List<String> strings = subSentence(StringUtils.reverse(sentence), n + 1);
            allReverseSubSentences.addAll(strings);
        }
        Collections.sort(allReverseSubSentences);
        if (debug) {
            for (String str : allReverseSubSentences) {
                logger.info(str);
            }
        }

        Map<String, Integer> tempStringPrefixCountMap = extractCount(n, true, allReverseSubSentences);//key也是逆向的
        Map<String, Integer> reverseStringPrefixCountMap = new HashMap<>(tempStringPrefixCountMap.size());
        for (Map.Entry<String, Integer> entry : tempStringPrefixCountMap.entrySet()) {//把key转为正向
            reverseStringPrefixCountMap.put(StringUtils.reverse(entry.getKey()), entry.getValue());
        }

        if (debug) {
            logger.info("=============以该词为后缀的词个数=======================");
            bianliMap(reverseStringPrefixCountMap);
        }
        //开始计算凝结度和信息熵
        Map<String, Double> ningjieMap = new HashMap<>();
        Map<String, Double> entropyMap = new HashMap<>();


        for (String key : subStringCountMap.keySet()) {
            if (key.length() > 1) {//从2个字的开始

                Integer count = subStringCountMap.get(key);
                double pa = (double) count / totalWordCount;

                Map<String, String> children = cutStringIntoArray(key);
                double max = 0;
                for (Map.Entry<String, String> entry : children.entrySet()) {
                    String key1 = entry.getKey();
                    String value = entry.getValue();

                    double pkey1 = (double) subStringCountMap.get(key1) / totalWordCount;
                    double pvalue = (double) subStringCountMap.get(value) / totalWordCount;

                    double multi = pkey1 * pvalue;
                    if (multi >= max) {
                        max = multi;
                    }
                }

                double ningjiedu = pa / max;
                ningjieMap.put(key, ningjiedu);
            }
        }

        /**
         * 保存内聚度结果
         */
        List<Map.Entry<String, Double>> tmpNingjieList = new ArrayList<>(ningjieMap.entrySet());
        Utils.sortMapStringAndDouble(tmpNingjieList, true);
        if (wordNingjuFile.exists()) {
            wordNingjuFile.delete();
        }
        try {
            for (Map.Entry<String, Double> entry : tmpNingjieList) {
                FileUtils.write(wordNingjuFile, entry.getKey() + " " + entry.getValue() + "\n", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (info) {
//            for (Map.Entry<String, Double> entry : tmpNingjieList) {
//                logger.info("{} -- {}", entry.getKey(), entry.getValue());
//            }
        }
        /**
         * 计算信息熵
         */
        for (String key : subStringCountMap.keySet()) {
            if (key.length() > 1) {//从2个字的开始

                //计算左右信息熵
                Integer prefixCountInteger = subStringPostfixCountMap.get(key);//以该词开头的词语个数
                Map<String, Integer> childrenPostfixMap = findPrePostfixCount(key, subStringCountMap, true);//以key为开头的词
                int prefixCount = prefixCountInteger == null ? 0 : prefixCountInteger.intValue();
                double leftEntropy = computeEntropy(childrenPostfixMap, prefixCount);


                Integer postCountInteger = reverseStringPrefixCountMap.get(key);//以该词为结尾的词的个数
                Map<String, Integer> childrenPrefixMap = findPrePostfixCount(key, subStringCountMap, false);//以key为结尾的词
                int postCount = postCountInteger == null ? 0 : postCountInteger.intValue();
                double rightEntropy = computeEntropy(childrenPrefixMap, postCount);

                logger.info("{}, prefix: {}, postfix: {}", new String[]{key, prefixCount + "", postCount + ""});
                entropyMap.put(key, leftEntropy + rightEntropy);
            }
        }

        if (info) {
            List<Map.Entry<String, Double>> tmpEntropyList = new ArrayList<>(entropyMap.entrySet());
            Utils.sortMapStringAndDouble(tmpEntropyList, true);
//            Collections.sort(tmpEntropyList, new Comparator<Map.Entry<String, Double>>() {
//                @Override
//                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
//                    Double value = o1.getValue();
//                    Double value1 = o2.getValue();
//                    if (value > value1) {
//                        return 1;
//                    } else {
//                        return 0;
//                    }
//                }
//            });
            for (Map.Entry<String, Double> entry : tmpEntropyList) {
                logger.info("{} -- {}", entry.getKey(), entry.getValue());
            }
        }
        return potentialWords;
    }

    private static double computeEntropy(Map<String, Integer> map, int totalCount) {
        double entropy = 0;
        if (map != null && map.size() > 0 && totalCount != 0) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                Integer tmpCount = entry.getValue();
                double p = (double) tmpCount / totalCount;
                double tmpM = -1 * p * Math.log(p);
                entropy += tmpM;
            }
        }
        return entropy;
    }

    private static Map<String, Integer> findPrePostfixCount(String key, Map<String, Integer> subStringCountMap, boolean isPrefix) {//找出以key为前缀或者后缀的那些词，及他们出现的次数
        Map<String, Integer> map = new HashMap<>();
        for (String k : subStringCountMap.keySet()) {
            if (isPrefix) {
                if ((k.length() > key.length()) && k.startsWith(key)) {
                    map.put(k, subStringCountMap.get(k));
                }
            } else {
                if ((k.length() > key.length()) && k.endsWith(key)) {
                    map.put(k, subStringCountMap.get(k));
                }
            }
        }
        return map;
    }

    public static void bianliMap(Map<String, Integer> subStringCountMap) {
        List<String> sortTempList = new ArrayList<>(subStringCountMap.keySet());
        Collections.sort(sortTempList);
        for (String key : sortTempList) {
            logger.info("{} count: {} ", new String[]{key, subStringCountMap.get(key) + ""});
        }
    }


    private static Map<String, Integer> extractCount(int n, boolean prefix, List<String> allSubSentences) {

        Map<String, Integer> subStringCountMap = new HashMap<>();
        //统计长度小于等于n的词语出现的次数
        for (int i = 1; i <= n; i++) {

            int begin = 0;
            int end = 0;
            int times = 0;

            while (begin < allSubSentences.size()) {
                String beginSentence = allSubSentences.get(begin);
                if (beginSentence.length() < i) {
                    begin++;
                    end = begin;
                    times = 0;
                    continue;
                }
                String beginStr = beginSentence.substring(0, i);

                if (end < allSubSentences.size()) {
                    String endSentence = allSubSentences.get(end);
                    if (prefix) {
                        if (beginSentence.length() <= (i + 1)) {
                            begin++;
                            end = begin;
                            times = 0;
                            continue;
                        }
                        beginStr = beginSentence.substring(0, i + 1);
                        if (endSentence.length() > beginStr.length() && endSentence.startsWith(beginStr)) {
                            end++;
                            times++;
                            subStringCountMap.put(beginStr, times);
                        } else {
                            begin = end;
                            times = 0;
                        }
                    } else {
                        if (endSentence.length() < i) {
                            begin = end + 1;
                            end = begin;
                            times = 0;
                        } else {
                            endSentence = endSentence.substring(0, i);
                            if (endSentence.equals(beginStr)) {
                                times++;
                                end++;
                                subStringCountMap.put(beginStr, times);
                            } else {
                                begin = end;
                                times = 0;
                            }
                        }
                    }
                } else {
                    begin = end;
                }
            }
        }

        return subStringCountMap;
    }

    //拆分为句子
    private static List<String> cutSentences(String body) {
        String[] strings = body.split("[，。、？！\"：”“)（(）,;；?. ><【《》】-]");
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
            if (st.length() > 0) {
                list.add(st);
            }
        }
        return list;
    }


    void findyuguo() {
        try {
            filePath = "data/yuguo.txt";
            init();
            String s = FileUtils.readFileToString(dataFile);
            findPotentialWords(s, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Map<String, String> cutStringIntoArray(String string) {//拆分一个词语为多个词的组合
        Map<String, String> list = new HashMap<>();
        if (!StringUtils.isEmpty(string) && string.length() >= 2) {
            int length = string.length();
            for (int i = 1; i < length; i++) {
                String pre = string.substring(0, i);
                String post = string.substring(i, length);
                list.put(pre, post);
            }
        }
        return list;
    }


    public static void main(String[] args) {
        NewWordDetection newWordDetection = new NewWordDetection();
        String str = "我爱北京天安门，天安门上太阳升。我爱吃苹果，天安门有好多人在看天安门的桥。";
//        String str = "四是四十是十十四是十四 四十是四十";
//        newWordDetection.findPotentialWords(str, 5);
//        newWordDetection.findyuguo();

//        Map<String, String> map = cutStringIntoArray("北京一家人");
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            logger.info(entry.getKey() + "          " + entry.getValue());
//        }
        newWordDetection.filePath = "data/yuguo.txt";
        newWordDetection.process();
    }

}
