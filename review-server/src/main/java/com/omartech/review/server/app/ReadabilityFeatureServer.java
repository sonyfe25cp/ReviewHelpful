package com.omartech.review.server.app;

import com.omartech.review.gen.FeatureRequest;
import com.omartech.review.gen.FeatureResponse;
import com.omartech.review.gen.Review;
import com.omartech.review.server.service.ADataService;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OmarTech on 15-7-7.
 */
public class ReadabilityFeatureServer extends ADataService {


//    1 The number of paragraphs in the review.
//    2 The average length of paragraphs in the review
//    3 The number of paragraph separators in the review


    @Override
    public FeatureResponse extractFeature(FeatureRequest req) throws TException {
        FeatureResponse response = new FeatureResponse();

        Review review = req.getReview();
        String body = review.getBody();

        List<Double> vector = new ArrayList<>();

        List<String> paragraphs = cutParagraph(body);

        //1
        vector.add((double) paragraphs.size());

        //2
        double avg = 0;
        for (String tmp : paragraphs) {
            avg += tmp.length();
        }
        avg = (double) avg / paragraphs.size();
        vector.add(avg);

        //3
        double numberOfSeparators = numberOfSeparators(body);
        vector.add(numberOfSeparators);

        response.setVector(vector);
        response.setLength(3);

        return response;
    }


    public List<String> cutParagraph(String body) {
        String[] split = body.split("\n");
        List<String> strings = new ArrayList<>();
        for (String tmp : split) {
            strings.add(tmp);
        }
        return strings;
    }

    public double numberOfSeparators(String body) {
        return 0;
    }

}
