package com.omartech.review.server.app;

import com.omartech.review.gen.*;
import com.omartech.review.server.service.ADataService;
import com.omartech.utils.vocabulary.Vocabulary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.thrift.TException;
import org.kohsuke.args4j.Option;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-8.
 */
public class TFIDFServer extends ADataService {

    private Vocabulary vocabulary = new Vocabulary();

    private static int num = 0;

    @Option(name = "-filter", usage = "set the autoFilter")
    private boolean autoFilter = false;

    @Override
    protected void after() {
        vocabulary.setFilterSingleWord(false);
        vocabulary.setAutoFilter(autoFilter);
        vocabulary.setDebug(debug);
    }

    @Override
    public SentenceResponse sendSentence(SentenceRequest req) throws TException {
        String sentence = req.getSentence();
        List<String> sentences = req.getSentences();
        boolean over = req.isOver();
        if (!StringUtils.isEmpty(sentence)) {
            vocabulary.addText(sentence);
        }
        if (sentences != null && sentences.size() > 0) {
            for (String s : sentences) {
                vocabulary.addText(s);
            }
        }
        if (over) {
            vocabulary.addOver();
            vocabulary.saveToDisk(DateFormatUtils.format(new Date(), "yyyy-MM-dd") + "-" + num);
        }
        SentenceResponse response = new SentenceResponse();
        response.setReq(req);
        return response;
    }

    @Override
    public TFIDFResponse tfidf(SentenceRequest req) throws TException {
        String sentence = req.getSentence();
        Map<Integer, Double> integerDoubleMap = vocabulary.generatePositionVectionMap(sentence);
        Map<String, Double> stringDoubleMap = vocabulary.generateStringVectorMap(sentence);


        TFIDFResponse response = new TFIDFResponse();
        response.setReq(req);
        response.setLexiconSize(vocabulary.size());
        response.setPositionMap(integerDoubleMap);
        response.setStringMap(stringDoubleMap);

        return response;
    }

    @Override
    public TFIDFStatusResponse tfidfStatus(ServerStatusRequest req) throws TException {
        Map<String, Integer> posMap = vocabulary.getPosMap();
        Map<Integer, String> map = new HashMap<>(posMap.size());
        for (Map.Entry<String, Integer> entry : posMap.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        TFIDFStatusResponse response = new TFIDFStatusResponse();
        response.setWordsPositionMap(map);
        response.setTotalWords(map.size());
        return response;
    }

    public static void main(String[] args) {
        TFIDFServer tfidfServer = new TFIDFServer();
        tfidfServer.notSearch = true;
        tfidfServer.port = 8123;
        tfidfServer.parseArgsAndRun(args);
    }
}
