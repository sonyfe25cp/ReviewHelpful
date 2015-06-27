package com.omartech.review.server.app;

import com.omartech.review.gen.ReviewFeatureResponse;
import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.gen.SentenceResponse;
import com.omartech.review.server.service.ADataService;
import com.omartech.utils.SimpleTrie;
import com.omartech.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by OmarTech on 15-6-8.
 */
public class CommentObjectServer extends ADataService {
    static Logger logger = LoggerFactory.getLogger(CommentObjectServer.class);

    Map<String, CXBZ> cxbzMap = new HashMap<>();
    List<String> sentencePool = new ArrayList<>();

    boolean available = true;

    private void reinit() {
        nMap = new HashMap<>();
        aMap = new HashMap<>();
        nTrie = new SimpleTrie();
        aTrie = new SimpleTrie();
        available = true;
        sentencePool = new ArrayList<>();
    }

    @Override
    protected void after() {
        File file = new File("cxbz.txt");
        List<CXBZ> cxbzs = new ArrayList<>();
        try {
            cxbzs = cxbzRead(file);
            for (CXBZ tmp : cxbzs) {
                String sentence = tmp.sentence;
                cxbzMap.put(sentence, tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SentenceResponse sendSentence(SentenceRequest req) throws TException {
        String sentence = req.getSentence();
        List<String> sentences = req.getSentences();
        boolean over = req.isOver();
        boolean clear = req.isClear();
        if (clear) {
            reinit();
        }
        if (available) {
            if (!StringUtils.isEmpty(sentence)) {
                sentencePool.add(sentence);
            }
            if (sentences != null && sentences.size() > 0) {
                for (String string : sentences) {
                    sentencePool.add(string);
                }
            }
            if (over) {
                available = false;
                computeFeaturesWithPool();
            }
        } else {
            logger.info("the pool is not available");
        }

        SentenceResponse response = new SentenceResponse();
        return response;
    }


    Map<String, Integer> nMap = new HashMap<>();
    Map<String, Integer> aMap = new HashMap<>();
    SimpleTrie nTrie = new SimpleTrie();
    SimpleTrie aTrie = new SimpleTrie();


    private void computeFeaturesWithPool() {
        int an_one = 0;
        int an_multi = 0;
        for (String sentence : sentencePool) {
            CXBZ cxbzObject = cxbzMap.get(sentence);
            if (cxbzObject == null) {
                logger.error("cant find the cxbz result about: {}", sentence);
            } else {
                String cxbz = cxbzObject.cxbz;
                List<String> cxbzArray = cxbzObject.cxbzArray;
                if (cxbz.contains("_a") && cxbz.contains("_n")) {
                    logger.info("sentence:{}, czbz:{}", sentence, cxbz);

                    int end_n = 0;
                    int end_a = 0;
                    for (String str : cxbzArray) {
                        if (str.endsWith("_a")) {
                            end_a++;
                        }
                        if (str.endsWith("_n")) {
                            end_n++;
                        }
                    }
                    if (end_a == 1 && end_a == 1) {//an_one :24667, an_multi:2785
                        for (String str : cxbzArray) {
                            if (str.endsWith("_a")) {
                                String s = removePOS(str);
                                Integer integer = aMap.get(s);
                                if (integer == null) {
                                    integer = 0;
                                }
                                integer++;
                                aMap.put(s, integer);
                            }
                            if (str.endsWith("_n")) {
                                String s = removePOS(str);
                                Integer integer = nMap.get(s);
                                if (integer == null) {
                                    integer = 0;
                                }
                                integer++;
                                nMap.put(s, integer);
                            }
                        }
                        an_one++;
                    } else {
                        an_multi++;
                    }
                }
            }
        }
        logger.info("an_one:{}, an_multi:{}", an_one, an_multi);

        List<Map.Entry<String, Integer>> amapList = new ArrayList<>(aMap.entrySet());
        List<Map.Entry<String, Integer>> nmapList = new ArrayList<>(nMap.entrySet());

        Utils.sortMapStringAndInteger(amapList, true);
        Utils.sortMapStringAndInteger(nmapList, true);

        for (Map.Entry<String, Integer> entry : amapList) {
            aTrie.add(entry.getKey());
            logger.info("key:{}, value:{}", entry.getKey(), entry.getValue());
        }
        logger.info("********************************************************");
        for (Map.Entry<String, Integer> entry : nmapList) {
            nTrie.add(entry.getKey());
            logger.info("key:{}, value:{}", entry.getKey(), entry.getValue());
        }


    }

    @Override
    public ReviewFeatureResponse findFeatures(SentenceRequest req) throws TException {
        String sentence = req.getSentence();

        List<String> strings = nTrie.matchedFromTrie(sentence);

        List<String> strings1 = aTrie.matchedFromTrie(sentence);

        ReviewFeatureResponse response = new ReviewFeatureResponse();
        response.setWords(strings);
        response.setAdjs(strings1);

        return response;

    }

    @Override
    public ReviewFeatureResponse fetchWholeFeatures() throws TException {

        Set<String> strings = aMap.keySet();
        Set<String> strings1 = nMap.keySet();

        for (Map.Entry<String, Integer> entry : aMap.entrySet()) {
            logger.info("{} -- {}", entry.getKey(), entry.getValue());
        }
        logger.info("**************************************");
        for (Map.Entry<String, Integer> entry : nMap.entrySet()) {
            logger.info("{} -- {}", entry.getKey(), entry.getValue());
        }

        ReviewFeatureResponse response = new ReviewFeatureResponse();
        response.setWords(new ArrayList<String>(strings1));
        response.setAdjs(new ArrayList<String>(strings));
        return response;
    }

    public static void main(String[] args) {
        CommentObjectServer commentObjectServer = new CommentObjectServer();
        commentObjectServer.notSearch = true;
        commentObjectServer.port = 8124;
        commentObjectServer.parseArgsAndRun(args);
    }


    public static String removePOS(String string) {
        String s2 = string.replaceAll("(_.+)", "");
        return s2;
    }

    public static List<CXBZ> cxbzRead(File file) throws IOException {
        List<CXBZ> list = new ArrayList<>();
        List<String> lines = FileUtils.readLines(file);
        for (String line : lines) {
            boolean valid = valid(line);
            if (valid) {

                String[] split = line.split("\t");
                String sentence = split[0];
                String cxbz = split[1];
                String[] tmps = cxbz.split(" ");
                List<String> tmpList = new ArrayList<>(tmps.length);
                for (int i = 0; i < tmps.length; i++) {
                    tmpList.add(tmps[i]);
                }

                CXBZ obj = new CXBZ();
                obj.cxbz = cxbz;
                obj.sentence = sentence;
                obj.cxbzArray = tmpList;

                list.add(obj);
            }
        }
        return list;
    }

    //有的记录是错的
    //1.含error_message
    //2.前后长度不同
    public static boolean valid(String line) {
        if (line.contains("error_message")) {
            return false;
        }
        String[] split = line.split("\t");
        if (split.length == 2) {
            String s = split[0];
            String s1 = split[1];

            String[] array = s1.split(" ");
            String last = array[0];
            int i = 0;
            for (String a : array) {
                String s2 = a.replaceAll("(_.+)", "");
                i = i + s2.length();
                last = s2;
            }

            if (s.length() == i && s.contains(last)) {
                return true;
            }
        }
        return false;
    }
}

class CXBZ {
    String sentence;
    String cxbz;
    List<String> cxbzArray;
}
