package com.omartech.review.modules.app;

import com.omartech.utils.Utils;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by OmarTech on 15-11-23.
 */
public class NewWordDetection {
    private static final Double NEIJUTHETA = 20.0;
    private static final Double COMBINETHETA = 5.0;
    static Logger logger = LoggerFactory.getLogger(NewWordDetection.class);

    boolean info = true;
    boolean debug = false;

    String filePath = null;
    File dataFile;
    int wordLength = 6;


    File postfixFile;//以该词为开头的词的文件
    File postfixCountFile;//以该词为开头的词的个数

    File prefixFile;//以该词为结尾的词的文件
    File prefixCountFile;//以该词为结尾的词的个数

    File wordCountFile;//该词出现的次数
    int wordCount;//该文件的总字数

    File wordLeftEntropyFile;
    File wordRightEntropyFile;
    File wordMergedEntropyFile;
    File wordCombineScoreFile;
    File cleanFile;
    File potentialWordsFile;

    File wordNingjuFile;
    File wordEntropyFile;
    File analysisFile;

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
        wordLeftEntropyFile = new File("output/leftEntropy-"+name);
        wordRightEntropyFile = new File("output/rightEntropy-"+name);
        wordMergedEntropyFile = new File("output/mergedEntropy-"+name);
        wordCombineScoreFile = new File("output/combineScore-"+name);
        analysisFile = new File("output/analysis-"+name);
        potentialWordsFile = new File("output/potentitalwords-"+name);

        logger.info("数据文件: {}", filePath);
        logger.info("清洗后的文件: {}", cleanFile.getAbsolutePath());
        logger.info("后缀表示文件: {}", postfixFile.getAbsolutePath());
        logger.info("前缀表示文件: {}", prefixFile.getAbsolutePath());
        logger.info("词语凝聚度文件: {}", wordNingjuFile.getAbsolutePath());
        logger.info("词语信息量文件: {}", wordEntropyFile.getAbsolutePath());
        logger.info("词语统计文件: {}", wordCountFile.getAbsolutePath());
        logger.info("该词为开头的数目文件: {}", postfixCountFile.getAbsolutePath());
        logger.info("该词为结尾的数目文件: {}", prefixCountFile.getAbsolutePath());
        logger.info("该词LeftEntropy文件: {}", wordLeftEntropyFile.getAbsolutePath());
        logger.info("该词RightEntropy文件: {}", wordRightEntropyFile.getAbsolutePath());
        logger.info("该词MergedEntropy文件: {}", wordMergedEntropyFile.getAbsolutePath());
        logger.info("该词CombineScore文件: {}", wordCombineScoreFile.getAbsolutePath());
        logger.info("该词Analysis文件: {}", analysisFile.getAbsolutePath());
        logger.info("该词Potential文件: {}", potentialWordsFile.getAbsolutePath());

    }

    void process() {
        long l1 = System.currentTimeMillis();
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
            Map<String, Double> leftEntropyMap = computeEntropy(wordCountMap, prefixLines, true);
            writeStringDoubleMapIntoFile(leftEntropyMap, wordLeftEntropyFile);
            logger.info("计算leftEntropy over");
            Map<String, Double> rightEntropyMap = computeEntropy(wordCountMap, postfixLines, false);
            writeStringDoubleMapIntoFile(rightEntropyMap, wordRightEntropyFile);
            logger.info("计算RightEntropy over");

            //Merge left entropy with right entropy
            Map<String, Double> mergeEntropyMap = computeMergeEntropy(leftEntropyMap, rightEntropyMap);
            writeStringDoubleMapIntoFile(mergeEntropyMap, wordMergedEntropyFile);
            logger.info("计算MergedEntropy over");

            //Combine both neiju and entropy
            Map<String, Double> combineScoreMap = computeMergeEntropy(mergeEntropyMap, ningjieduMap);
            writeStringDoubleMapIntoFile(combineScoreMap, wordCombineScoreFile);
            logger.info("计算combineScoreMap over");

            //Analysis all results
            List<String> candidates = analysis(combineScoreMap, mergeEntropyMap, leftEntropyMap, rightEntropyMap, ningjieduMap, analysisFile);
            logger.info("Analysis file over");

            //Original words
            Set<String> terms = fetchOriginalWords(text);
            logger.info("fetch original words over");

            List<String> potentialWords = compare(terms, candidates);
            FileUtils.writeLines(potentialWordsFile, potentialWords);
            logger.info("{} potential new words are found from {} candidates.", potentialWords.size(), candidates.size());
            logger.info("Compute Potential words list over");

        } catch (IOException e) {
            e.printStackTrace();
        }
        long l2 = System.currentTimeMillis();
        logger.info("Compute completed, cost {}s", (l2-l1)/1000.0);
    }

    private List<String> compare(Set<String> set, List<String> candidates) {
        List<String> list = new ArrayList<>();
        for(String tmp : candidates){
            if(!set.contains(tmp)){
                list.add(tmp);
            }
        }
        return list;
    }

    /**
     * Original words about text
     * @param text
     * @return
     */
    private Set<String> fetchOriginalWords(String text) {
        Set<String> set = new HashSet<>();
        int batch = 100000;
        for(int i = 0; i < text.length(); i = i + batch) {
            int end = (i+batch) > text.length() ? text.length() : (i+batch);
            String part = text.substring(i, end);
            List<Term> terms = ToAnalysis.parse(part);
            for (Term term : terms) {
                set.add(term.getName());
            }
        }
        return set;
    }

    private List<String> analysis(Map<String, Double> combineScoreMap, Map<String, Double> mergeEntropyMap, Map<String, Double> leftEntropyMap, Map<String, Double> rightEntropyMap, Map<String, Double> ningjieduMap, File analysisFile) {
        if(analysisFile.exists()){
            analysisFile.delete();
        }
        List<String> candidates = new ArrayList<>();
        List<Map.Entry<String, Double>> entries = extractSortList(combineScoreMap);
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(analysisFile))) {
            for (Map.Entry<String, Double> entry : entries) {
                String key = entry.getKey();
                Double combine = entry.getValue();
                if(combine < COMBINETHETA){
                    continue;
                }
                Double ningjie = ningjieduMap.get(key);
                if(ningjie < NEIJUTHETA){
                    continue;
                }
                Double merge = mergeEntropyMap.get(key);
                Double left = leftEntropyMap.get(key);
                Double right = rightEntropyMap.get(key);
                String str = key + "\t" + combine + "\t" + ningjie + "\t" + merge + "\t" + left + "\t" + right + "\t" + "\n";
//                FileUtils.write(analysisFile, str, true);
                bw.write(str);
                candidates.add(key);
            }
            bw.flush();
        }catch (Exception e ){
            logger.error("File is broken");
        }
        return candidates;
    }

    private Map<String, Double> computeMergeEntropy(Map<String, Double> leftEntropyMap, Map<String, Double> rightEntropyMap) {
       Map<String, Double> merged = new HashMap<>();
        for(Map.Entry<String, Double> entry : leftEntropyMap.entrySet()){
            String key = entry.getKey();
            double left = entry.getValue();
            Double right = rightEntropyMap.get(key);
            double merge = left;
            if(right != null && left != 0 && right !=0){
                right = right.doubleValue();
                merge = (2 * left * right) / (left + right);
//                logger.info("left: {}, right:{}, merge:{}",new String[]{left+"", right+"", merge+""});
            }else{
                merge = 0;
            }
            merged.put(key, merge);
        }
        return merged;
    }

    private Map<String, Double> computeEntropy(Map<String, Integer> wordCountMap, List<String> lines, boolean reverse) {
        Map<String, Double> entropyMap = new HashMap<>();
        int c = 0;
        for (String key : wordCountMap.keySet()) {
            if(key.length()==1){//Single word is ignored
                continue;
            }
            if(reverse) {
                key = StringUtils.reverse(key);
            }
            List<String> lineswithWord = findLineswithWord(lines, key);
            Map<String, Integer> summary = new HashMap<>();
            for (String str : lineswithWord) {
                String sub = str.substring(0, key.length()+1);
                Integer intger = 0;
                if (summary.containsKey(sub)) {
                    intger = summary.get(sub);
                }
                intger++;
                summary.put(sub, intger);
            }
            logger.debug("find {} lines start with {}", summary.size(), key);
            double entropy = computeEntropyFunction(summary, lineswithWord.size());
            if(reverse) {
                key = StringUtils.reverse(key);
            }
            entropyMap.put(key, entropy);
            c++;
            if(c / 10000 == 0){
                logger.info("{} words passed", c);
            }
        }
        return entropyMap;
    }

    public List<String> findLineswithWord(List<String> lines, String word){
        int length = lines.size()-1;
        int begin = 0;
        int end = length;
        int anyposition = -1;
        while(begin < end) {
            String beginStr = lines.get(begin);
            String endStr = lines.get(end);
            if(endStr.compareTo(word) == -1 || beginStr.compareTo(word) == 1){
                logger.error("There is no #{}# in the array.", word);
                break;
            }
            int middle = 0;
            if(beginStr.startsWith(word)){
                anyposition = begin;
                break;
            }else if(endStr.startsWith(word)){
                anyposition = end;
                break;
            }else{
                middle = begin + (end - begin)/2;
            }
            String middleStr = lines.get(middle);
            if(middleStr.compareTo(word) > 0){
                end = middle -1;
            }else{
                begin = middle+1;
            }
        }
        logger.debug("anyposition : {}", anyposition);

        int beginPosition = anyposition;
        //find the begin position of word
        if(anyposition > 0){
            for(int i = anyposition; i > 0; i --){
                String str = lines.get(i);
                if(str.startsWith(word)){
                    beginPosition = i;
                }else{
                    break;
                }
            }
        }
        logger.debug("beginPositon : {}", beginPosition);
        int endPosition = anyposition+1;
        //find the end position of word
        if(anyposition > 0  && anyposition < length){
            for(int i = anyposition; i < length; i ++){
                String str = lines.get(i);
                if(str.startsWith(word)){
                    endPosition = i;
                }else{
                    break;
                }
            }
        }
        logger.debug("endPosition: {}", endPosition);
        List<String> rightLines = new ArrayList<>();
        if(beginPosition != -1 && endPosition !=-1) {
            for (int i = beginPosition; i < endPosition; i++) {
                String current = lines.get(i);
                if(!current.equals(word)){
                    rightLines.add(current);
                }
            }
        }
        return rightLines;
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

    private void writeStringDoubleMapIntoFile(Map<String, Double> subStringCountMap, File outputFile) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            List<Map.Entry<String, Double>> entryList = extractSortList(subStringCountMap);
            for (int i = 0 ; i < entryList.size(); i ++) {
                Map.Entry<String, Double> entry = entryList.get(i);
                String str = entry.getKey() + " " + entry.getValue() + "\n";
                bw.write(str);
            }
            bw.flush();
//            FileUtils.write(outputFile, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Map.Entry<String, Double>> extractSortList(Map<String, Double> subStringCountMap) {
        List<Map.Entry<String, Double>> entryList = new ArrayList<>(subStringCountMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                Double value = o1.getValue();
                Double value1 = o2.getValue();
                int compare = Double.compare(value1, value);
                return compare;
            }
        });
        return entryList;
    }

    private void writeStringIntegerMapIntoFile(Map<String, Integer> subStringCountMap, File postfixCountFile) {
        try {
            List<Map.Entry<String, Integer>> entryList = new ArrayList<>(subStringCountMap.entrySet());
            Utils.sortMapStringAndInteger(entryList, false);
            StringBuilder sb = new StringBuilder();
            for (int i = 0 ; i < entryList.size(); i ++) {
                Map.Entry<String, Integer> entry = entryList.get(i);
                sb.append(entry.getKey() + " " + entry.getValue() + "\n");
            }
            FileUtils.write(postfixCountFile, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        Pattern pattern = Pattern.compile("[^\\dA-Za-z\\u3007\\u3400-\\u4DB5\\u4E00-\\u9FCB\\uE815-\\uE864]");//去掉非汉字，英文，数字
//    Pattern pattern = Pattern.compile("[^\\u3007\\u3400-\\u4DB5\\u4E00-\\u9FCB\\uE815-\\uE864]");//去掉非汉字

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

    private static double computeEntropyFunction(Map<String, Integer> map, int totalCount) {
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
//        newWordDetection.filePath = "data/hongloumeng.txt";
        newWordDetection.process();

    }

}
