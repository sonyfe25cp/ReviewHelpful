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
public class WritingStyleFeatureServer extends ADataService {

//    1 POS of the words in the review

    @Override
    public FeatureResponse extractFeature(FeatureRequest req) throws TException {
        FeatureResponse response = new FeatureResponse();
        Review review = req.getReview();

        String body = review.getBody();

        List<Double> vector = new ArrayList<>();
        vector.add(0.0);//暂时不管pos

        response.setVector(vector);
        response.setLength(1);
        return response;
    }
}
