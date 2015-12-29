package com.omartech.review.server.tools;

import com.omartech.review.client.ClientException;
import com.omartech.review.client.DataClients;
import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.gen.TFIDFResponse;

import java.util.Map;

/**
 * Created by omar on 15/12/25.
 */
public class FeaturesTool {

    DataClients clients = new DataClients("127.0.0.1:8123,127.0.0.1:8123");

    public void tF() throws ClientException {
        SentenceRequest request = new SentenceRequest();
        request.sentence = "这个公司的工作效率很低";

        TFIDFResponse tfidf = clients.tfidf(request);
        Map<Integer, Double> positionMap = tfidf.getPositionMap();

        for (Map.Entry<Integer, Double> entry : positionMap.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

    }

    public void surface() {

    }

    public static void main(String[] args) throws ClientException {
        FeaturesTool ft = new FeaturesTool();
        ft.tF();
    }

    这个公司的工作效率很低，
    面试流程很好。
    工作-低，
    面试-好

    这个公司的工作很好，
    面试流程效率很低。
    工作 好，面试 低


}
