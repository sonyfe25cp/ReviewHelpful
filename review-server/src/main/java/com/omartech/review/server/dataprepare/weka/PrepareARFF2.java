package com.omartech.review.server.dataprepare.weka;

import com.omartech.review.client.ClientException;
import com.omartech.review.client.DataClients;
import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.gen.TFIDFResponse;
import com.omartech.review.gen.TFIDFStatusResponse;
import com.omartech.review.server.dataprepare.DBService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by OmarTech on 15-6-18.
 */
public class PrepareARFF2 {
    DataClients tfidfClient = new DataClients("127.0.0.1:8123,127.0.0.1:8123");

    public static void main(String[] args) throws IOException {
        PrepareARFF2 prepareARFF2 = new PrepareARFF2();
        prepareARFF2.run();

    }

    void run() throws IOException {

//        Map<Integer, Integer> features = readSelectedFeatures();
        Map<Integer, Integer> features = new HashMap<>();

        Map<String, Integer> map = new HashMap<>();
        try (Connection connection = DBService.reviewCon.get();) {
            map = DBService.fetchAllReviewsMap(connection, 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        File arff = new File("data2.arff");
        if (arff.exists()) {
            arff.delete();
        }

        int classIndex = 0;

        FileUtils.write(arff, "@relation reviews\n", true);

        try {
            TFIDFStatusResponse statusResponse = tfidfClient.tfidfStatus(null);
            Map<Integer, String> wordsPositionMap = statusResponse.getWordsPositionMap();
            classIndex = statusResponse.getTotalWords();
            System.out.println("classIndex:" + classIndex);
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
                Integer key = entry.getKey();
                if (features.size() > 0) {
                    if (!features.containsKey(key)) {
                        continue;
                    }
                }
                String value = entry.getValue();
                value = "'" + value.replaceAll("['}{\"]", "‘") + "'";
                FileUtils.write(arff, "@attribute " + value + " numeric\n", true);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        FileUtils.write(arff, "@attribute Class {100, 400, 600, 800, 1200}\n", true);
        FileUtils.write(arff, "@data\n", true);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Integer value = entry.getValue();
            String key = entry.getKey();
            SentenceRequest sentenceRequest = new SentenceRequest();
            sentenceRequest.setSentence(key);
            System.out.println(key);
            try {
                TFIDFResponse tfidf = tfidfClient.tfidf(sentenceRequest);
                Map<Integer, Double> positionMap = tfidf.getPositionMap();

                List<Map.Entry<Integer, Double>> posEntryList = new ArrayList<>(positionMap.entrySet());
                Collections.sort(posEntryList, new Comparator<Map.Entry<Integer, Double>>() {
                    @Override
                    public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                        Integer key1 = o1.getKey();
                        Integer key2 = o2.getKey();
                        if (key1 > key2) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

                StringBuilder sb = new StringBuilder();
                if (features.size() > 0) {
                    Map<Integer, Double> tmpMap = new HashMap<>();
                    for (Map.Entry<Integer, Double> pentry : posEntryList) {
                        System.out.println(pentry.getKey() + " ** " + pentry.getValue());
                        if (!features.containsKey(pentry.getKey())) {
                            continue;
                        }
                        tmpMap.put(features.get(pentry.getKey()), pentry.getValue());
                    }
                    List<Map.Entry<Integer, Double>> tmpEntryList = new ArrayList<>(tmpMap.entrySet());
                    Collections.sort(tmpEntryList, new Comparator<Map.Entry<Integer, Double>>() {
                        @Override
                        public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                            Integer key1 = o1.getKey();
                            Integer key2 = o2.getKey();
                            if (key1 > key2) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                    for (Map.Entry<Integer, Double> pentry : tmpEntryList) {
                        String tmp = pentry.getKey() + " " + pentry.getValue() + ", ";
                        sb.append(tmp);
                    }
                } else {
                    for (Map.Entry<Integer, Double> pentry : posEntryList) {
                        System.out.println(pentry.getKey() + " ** " + pentry.getValue());
                        String tmp = pentry.getKey() + " " + pentry.getValue() + ", ";
                        sb.append(tmp);
                    }
                }
                if (sb.length() > 0) {
                    if (features.size() > 0) {
                        classIndex = features.size();
                    }
                    sb.append(classIndex + " \"" + value + "\"");
                    System.out.println(sb.toString());
                    FileUtils.write(arff, "{" + sb.toString() + "}\n", true);
                }

            } catch (ClientException e) {
                e.printStackTrace();
            }

            System.out.println(classIndex + ":" + value);

//            break;
        }

    }


    static Map<Integer, Integer> readSelectedFeatures() {
        Map<Integer, Integer> map = new HashMap<>();
        try {
            List<String> lines = FileUtils.readLines(new File("featuresSelection"));
            int index = 0;
            for (String line : lines) {
                line = line.replaceAll("[ ]+", " ");
                String[] split = line.split(" ");
                int i = Integer.parseInt(split[1]);
                System.out.println(i);
                map.put(i, index);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}
