package com.omartech.review.server.dataprepare.forApp;

import com.omartech.review.client.ClientException;
import com.omartech.review.client.DataClients;
import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.server.dataprepare.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 准备不同的tfidf server 和 coserver
 * Created by OmarTech on 15-6-18.
 */

public class TFIDFPrepare {
    static Logger logger = LoggerFactory.getLogger(TFIDFPrepare.class);

    DataClients tfidfClient = new DataClients("127.0.0.1:8123,127.0.0.1:8123");


    public static void main(String[] args) {
        TFIDFPrepare tfidfPrepare = new TFIDFPrepare();
        try {
            tfidfPrepare.prepareTFIDFReviewsPerLinePerDocument();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tfidfPrepare.prepareTFIDFNewsPerLinePerDocument();
    }


    //将每一个人的评论作为一个doc
    void prepareTFIDFReviewsPerLinePerDocument() throws SQLException {
        try (Connection connection = DBService.reviewCon.get()) {
            List<String> strings = DBService.fetchAllReviews(connection, 0);
            SentenceRequest clear = new SentenceRequest();
            clear.setClear(true);
            tfidfClient.sendSentence(clear);
            for (String line : strings) {
                SentenceRequest request = new SentenceRequest();
                request.setSentence(line);
                tfidfClient.sendSentence(request);
            }
            SentenceRequest close = new SentenceRequest();
            close.setOver(true);
            tfidfClient.sendSentence(close);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    //将每一个新闻作为一个doc
    void prepareTFIDFNewsPerLinePerDocument() {

    }


}
