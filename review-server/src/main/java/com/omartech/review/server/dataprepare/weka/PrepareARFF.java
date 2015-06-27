package com.omartech.review.server.dataprepare.weka;

import com.omartech.review.client.ClientException;
import com.omartech.review.client.DataClients;
import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.gen.TFIDFResponse;
import com.omartech.review.gen.TFIDFStatusResponse;
import com.omartech.review.server.dataprepare.DBService;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by OmarTech on 15-6-18.
 */
public class PrepareARFF {
    DataClients tfidfClient = new DataClients("127.0.0.1:8123,127.0.0.1:8123");


    public static void main(String[] args) {
        PrepareARFF prepareARFF = new PrepareARFF();
        prepareARFF.run();
    }

    void run() {

        Map<String, Integer> map = new HashMap<>();
        try (Connection connection = DBService.reviewCon.get();) {
            map = DBService.fetchAllReviewsMap(connection, 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FastVector attributes = new FastVector();
        try {
            TFIDFStatusResponse statusResponse = tfidfClient.tfidfStatus(null);
            Map<Integer, String> wordsPositionMap = statusResponse.getWordsPositionMap();
            List<Map.Entry<Integer, String>> entryList = new ArrayList<>(wordsPositionMap.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<Integer, String>>() {
                @Override
                public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                    Integer key = o1.getKey();
                    Integer key1 = o2.getKey();
                    if (key > key1) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            for (Map.Entry<Integer, String> entry : entryList) {
//                System.out.println(entry.getKey() + " -- " + entry.getValue());
                Attribute numeric = new Attribute(entry.getValue());
                attributes.addElement(numeric);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        //0 - 11354

        FastVector labels = new FastVector();
        labels.addElement("100");
        labels.addElement("400");
        labels.addElement("600");
        labels.addElement("800");
        labels.addElement("1200");
        Attribute cls = new Attribute("Class", labels);
        attributes.addElement(cls);

        Instances data = new Instances("mydata", attributes, 2);

        System.out.println("attributes.size:" + attributes.size());

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            double[] values = new double[attributes.size()];
            Integer value = entry.getValue();
            String key = entry.getKey();
            SentenceRequest sentenceRequest = new SentenceRequest();
            sentenceRequest.setSentence(key);
            System.out.println(key);
            try {
                TFIDFResponse tfidf = tfidfClient.tfidf(sentenceRequest);
                Map<Integer, Double> positionMap = tfidf.getPositionMap();
                for (Map.Entry<Integer, Double> pentry : positionMap.entrySet()) {
                    System.out.println(pentry.getKey() + " ** " + pentry.getValue());
                    values[pentry.getKey()] = pentry.getValue();
                }
            } catch (ClientException e) {
                e.printStackTrace();
            }
//            values[attributes.size() - 1] = 100;

            for (int i = 0; i < values.length; i++) {
//                System.out.println(i + " ==  " + values[i]);
            }

            data.add(new Instance(1.0, values));

            System.out.println("class:" + value);
//            break;
        }

//        System.out.println(data);

        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        try {
            saver.setFile(new File("data.arff"));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
