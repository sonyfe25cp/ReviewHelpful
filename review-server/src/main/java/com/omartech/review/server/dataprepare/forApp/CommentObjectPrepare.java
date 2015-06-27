package com.omartech.review.server.dataprepare.forApp;

import com.omartech.review.client.ClientException;
import com.omartech.review.client.DataClients;
import com.omartech.review.gen.ReviewFeatureResponse;
import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.server.dataprepare.DBService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by OmarTech on 15-6-23.
 */
public class CommentObjectPrepare {
    DataClients commentObjectClient = new DataClients("127.0.0.1:8124,127.0.0.1:8124");


    public static void main(String[] args) {
        CommentObjectPrepare commentObjectPrepare = new CommentObjectPrepare();
        try {
            commentObjectPrepare.prepareCommentObjectReviewsPerLinePerDocument();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }


    }


    //将每一个人的评论作为一个doc
    void prepareCommentObjectReviewsPerLinePerDocument() throws SQLException, ClientException {
        try (Connection connection = DBService.reviewCon.get()) {
            List<String> strings = DBService.fetchAllReviews(connection, 0);
            SentenceRequest clear = new SentenceRequest();
            clear.setClear(true);
            commentObjectClient.sendSentence(clear);
            for (String line : strings) {
                SentenceRequest request = new SentenceRequest();
                request.setSentence(line);
                commentObjectClient.sendSentence(request);
            }
            SentenceRequest close = new SentenceRequest();
            close.setOver(true);
            commentObjectClient.sendSentence(close);
        }
        ReviewFeatureResponse reviewFeatureResponse = commentObjectClient.fetchWholeFeatures();
        List<String> words = reviewFeatureResponse.getWords();
        System.out.println("begin noun");
        for (String word : words) {
            System.out.println(word);
        }
        System.out.println("********************************************");
        List<String> adjs = reviewFeatureResponse.getAdjs();
        for (String adj : adjs) {
            System.out.println(adj);
        }
        System.out.println("********************************************");
    }

}
