package com.omartech.review.server.app;

import com.omartech.review.gen.FeatureRequest;
import com.omartech.review.gen.FeatureResponse;
import com.omartech.review.gen.Review;
import com.omartech.review.server.service.ADataService;
import com.omartech.utils.SimpleTrie;
import com.omartech.utils.Utils;
import org.apache.thrift.TException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OmarTech on 15-6-27.
 */
public class InformationFeatureServer extends ADataService {

//    1 The number of sentences in the review √
//    2 The average length of sentences
//    3 The number of sentences with product features
//    4 The number of words in the review
//    5 The number of products in the review
//    6 The number of products in the title of a review
//    7 The number of brand names
//    8 The number of brand names in the title of a review
//    9 The total frequency of product features in the review
//    10 The average frequency of product features in the review
//    11 The number of product features in the title of a review
//    12 The total frequency of product features in the title of a review
//    13 The ratio of uppercase to lowercase characters in the review text


    @Override
    public FeatureResponse extractFeature(FeatureRequest req) throws TException {
        FeatureResponse response = new FeatureResponse();

        List<Double> vector = new ArrayList<>();

        Review review = req.getReview();

        String body = review.getBody();

        List<String> sentences = cutSentence(body);
        vector.add((double) sentences.size());//1


        double averageLength = averageLength(sentences);//2
        vector.add(averageLength);

        double withProductFeatures = withProductFeatures(sentences);//3
        vector.add(withProductFeatures);

        double numberOfWords = numberOfWords(sentences);//4
        vector.add(numberOfWords);

        vector.add(0.0);//5
        vector.add(0.0);//6
        vector.add(0.0);//7
        vector.add(0.0);//8

        double totalProductFeature = totalProductFeature(sentences);//9
        vector.add(totalProductFeature);

        double averageProductFeature = averageProductFeature(sentences);
        vector.add(averageProductFeature);//10

        double numberOfProductFeaturesInTitle = numberOfProductFeaturesInTitle(review.getTitle());//11
        vector.add(numberOfProductFeaturesInTitle);

        double totalProductFeaturesInTitle = totalProductFeaturesInTitle(review.getTitle());//12 --- 暂时和11一样
        vector.add(totalProductFeaturesInTitle);

        vector.add(0.0);//13 -- 中文无此项


        response.setVector(vector);
        response.setLength(13);

        return response;
    }

    public List<String> cutSentence(String article) {
        List<String> list = new ArrayList<>();
        String[] strings = article.split(".");
        for (String tmp : strings) {
            list.add(tmp);
        }
        return list;
    }

    public double averageLength(List<String> sentences) {
        int avg = 0;
        for (String tmp : sentences) {
            avg += tmp.length();
        }
        return (double) (avg / sentences.size());
    }


    public double withProductFeatures(List<String> sentences) {
        int wpf = 0;
        for (String tmp : sentences) {
            List<String> strings = featureTrie.matchedFromTrie(tmp);
            if (strings != null && strings.size() > 0) {
                wpf++;
            }
        }
        return wpf;
    }

    public double numberOfWords(List<String> sentences) {
        int now = 0;
        for (String tmp : sentences) {
            try {
                List list = cutWords(tmp);
                now += list.size();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return now;
    }

    public double totalProductFeature(List<String> sentences) {
        int tpf = 0;
        for (String tmp : sentences) {
            List<String> strings = featureTrie.matchedFromTrie(tmp);
            tpf += strings.size();
        }
        return tpf;
    }

    public double averageProductFeature(List<String> sentences) {
        double totalProductFeature = totalProductFeature(sentences);
        double v = totalProductFeature / sentences.size();
        return v;
    }

    public double numberOfProductFeaturesInTitle(String title) {
        List<String> strings = featureTrie.matchedFromTrie(title);
        return strings.size();
    }

    public double totalProductFeaturesInTitle(String title) {
        List<String> strings = featureTrie.matchedFromTrie(title);
        return strings.size();
    }

    public static SimpleTrie featureTrie = new SimpleTrie();

    @Override
    public void prepare() {
        //prepare featureTrie
        List<String> featureList = Utils.getResourceList("featureList");
        for (String tmp : featureList) {
            featureTrie.add(tmp);
        }
    }
}
