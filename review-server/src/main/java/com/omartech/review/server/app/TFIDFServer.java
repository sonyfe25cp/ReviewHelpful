package com.omartech.review.server.app;

import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.gen.SentenceResponse;
import com.omartech.review.gen.TFIDFResponse;
import com.omartech.review.server.service.ADataService;
import com.omartech.utils.vocabulary.Vocabulary;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-8.
 */
public class TFIDFServer extends ADataService {

    Vocabulary vocabulary = new Vocabulary();

    @Override
    protected void after() {
        vocabulary.setFilterSingleWord(true);
        vocabulary.setAutoFilter(false);
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

    public static void main(String[] args) {
        TFIDFServer tfidfServer = new TFIDFServer();
        tfidfServer.notSearch = true;
        tfidfServer.port = 8123;
        tfidfServer.parseArgsAndRun(args);
    }
}
