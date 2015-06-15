package reviewHelpful.action;

import com.omartech.review.client.ClientException;
import com.omartech.review.client.DataClients;
import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.gen.TFIDFResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import reviewHelpful.ClientsFetcher;
import reviewHelpful.DBService;
import reviewHelpful.NKCluster;
import reviewHelpful.model.Review;
import reviewHelpful.model.ReviewObject;
import reviewHelpful.model.StatReviewCount;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by OmarTech on 15-5-24.
 */
@Controller
public class CompanyAction {
    static Logger logger = LoggerFactory.getLogger(CompanyAction.class);

    static DataClients tfidfClient = ClientsFetcher.tfidfClient;
    static DataClients commentObjectClient = ClientsFetcher.commentObjectClient;

    @RequestMapping("/pp")
    public String test() throws ClientException {
        DataClients clients = new DataClients("127.0.0.1:8123,127.0.0.1:8123");

        SentenceRequest clearReq = new SentenceRequest();
        clearReq.setClear(true);
        clients.sendSentence(clearReq);

        SentenceRequest sr1 = new SentenceRequest();
        sr1.setSentence("我爱北京天安门");
        SentenceRequest sr2 = new SentenceRequest();
        sr2.setSentence("天安门上太阳升");
        sr2.setOver(true);

        clients.sendSentence(sr1);
        clients.sendSentence(sr2);

        String sentence = "北京天安门真好看";

        SentenceRequest sr3 = new SentenceRequest();
        sr3.setSentence(sentence);
        TFIDFResponse tfidfResponse = clients.tfidf(sr3);
        Map<String, Double> stringMap = tfidfResponse.getStringMap();
        for (Map.Entry<String, Double> entry : stringMap.entrySet()) {
            System.out.println(entry.getKey() + " -- " + entry.getValue());
        }
        Map<Integer, Double> positionMap = tfidfResponse.getPositionMap();
        for (Map.Entry<Integer, Double> entry : positionMap.entrySet()) {
            System.out.println(entry.getKey() + "  -- " + entry.getValue());
        }
        return "11";

    }


    @RequestMapping("/prepare")
    public ModelAndView prepare(@RequestParam(defaultValue = "0", required = false) int sep) {
        Connection connection = DBService.fetchConnection();
        try {
            List<String> strings = DBService.fetchAllReviews(connection, sep);
            SentenceRequest clearReq = new SentenceRequest();
            clearReq.setClear(true);
            tfidfClient.sendSentence(clearReq);

            for (String str : strings) {
                SentenceRequest req = new SentenceRequest();
                req.setSentence(str);
                tfidfClient.sendSentence(req);
            }
            SentenceRequest overReq = new SentenceRequest();
            overReq.setOver(true);
            tfidfClient.sendSentence(overReq);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return new ModelAndView("prepareOver");
    }

    @RequestMapping("/")
    public ModelAndView list() {
        Connection connection = DBService.fetchConnection();
        List<StatReviewCount> statReviewCounts = null;
        try {
            statReviewCounts = DBService.statReviewCounts(connection);
            Collections.sort(statReviewCounts, new Comparator<StatReviewCount>() {
                @Override
                public int compare(StatReviewCount o1, StatReviewCount o2) {
                    return o2.getCount() - o1.getCount();
                }
            });
            logger.info("list page is over");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ModelAndView("home").addObject("statReviewCounts", statReviewCounts);
    }


    @RequestMapping("/showDetails")
    public ModelAndView show(@RequestParam int id,
                             @RequestParam int goodOrBad,
                             @RequestParam String method,
                             @RequestParam(defaultValue = "5", required = false) int minRou) {
        ReviewObject reviewObject = null;
        try (Connection connection = DBService.fetchConnection();) {
            reviewObject = DBService.findReviewsByCompanyId(connection, id, goodOrBad);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String companyName = reviewObject.getCompanyName();

        NKCluster.DistanceEnum distanceType = NKCluster.DistanceEnum.valueOf(method);

        List<Review> originReviews = reviewObject.getOriginReviews();
        NKCluster nkCluster = new NKCluster();
        nkCluster.setDistanceType(distanceType);
        nkCluster.setOriginReviews(originReviews);
        nkCluster.cluster(0);
        String data = nkCluster.getData(originReviews);
        int maxRou = nkCluster.getMaxRou();

        logger.info("最大的密度:{}", maxRou);

        Map<Integer, Double> faiMap = nkCluster.getFaiMap();
        Map<Integer, Integer> rouMap = nkCluster.getRouMap();

        Map<Integer, Double> potentialRous = new HashMap<>();
        for (Map.Entry<Integer, Integer> rouEntry : rouMap.entrySet()) {
            Integer value = rouEntry.getValue();
            Integer pos = rouEntry.getKey();
            if (value >= minRou) {
                double newWeight = (value + 0.0) / maxRou;
                logger.info("potential pos:{}, rou:{}", pos, value);
                potentialRous.put(pos, newWeight);
            }
        }

        Map<Integer, Double> weightForSort = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : potentialRous.entrySet()) {
            Integer key = entry.getKey();

            Double fai = faiMap.get(key);
            Double normalRou = entry.getValue();

            if (fai == null || normalRou == null) {
                logger.error("key:{}, fai:{}, normalRou:{}", new String[]{key + "", fai + "", normalRou + ""});
            }

            double squre = fai * fai + normalRou * normalRou;

            logger.info("point:{}, squre:{}", key, squre);

            weightForSort.put(key, squre);
        }
        logger.info("weight for sort is over");

        List<Map.Entry<Integer, Double>> weightListForSort = new ArrayList<>(weightForSort.entrySet());
        Collections.sort(weightListForSort, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                Double value = o1.getValue();
                Double value1 = o2.getValue();
                if (value == null || value1 == null) {
                    logger.error("为什么有空的？{}, {}", o1.getKey(), o2.getKey());
                }
                if (value > value1) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        logger.info("sort is over");

        List<Integer> preseeds = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : weightListForSort) {
            Integer key = entry.getKey();
            Double value = entry.getValue();
            System.out.println(key + " -- " + value);
            preseeds.add(key);
        }

        List<Integer> seeds = new ArrayList<>();
        double previous = 0;
        Integer first = weightListForSort.get(0).getKey();
        seeds.add(first);
        for (int i = 1; i < weightListForSort.size(); i++) {
            Map.Entry<Integer, Double> entry = weightListForSort.get(i);
            Integer key = entry.getKey();
            Double value = entry.getValue();
            if (previous == 0) {
                previous = value;
                seeds.add(key);
            } else {
                double aim = previous * 0.5;
                if (aim < value) {
                    logger.info("point:{}, value:{} is ok", key, value);
                    seeds.add(key);
                } else {
                    break;
                }
            }
        }


        Map<String, List<Review>> clusterResultFtl = new HashMap<>();
        if (seeds.size() != 0) {
            Map<Integer, List<Review>> clusterResult = nkCluster.fetchCluster(seeds);
            for (Map.Entry<Integer, List<Review>> entry : clusterResult.entrySet()) {
                Integer key = entry.getKey();
                clusterResultFtl.put(key.intValue() + "", entry.getValue());
            }
            for (Map.Entry<Integer, List<Review>> entry : clusterResult.entrySet()) {
                Integer key = entry.getKey();
                System.out.println(key);
                Review review1 = originReviews.get(key);
                logger.info("Centroid： {}", review1.getText());
                for (Review review : entry.getValue()) {
                    System.out.println(StringUtils.deleteWhitespace(review.getText()));
                }
                System.out.println("**********************************");
            }

        }

        return new ModelAndView("showDetails")
                .addObject("data", data)
                .addObject("companyName", companyName)
                .addObject("cluster", clusterResultFtl)
                .addObject("minRou", minRou)
                .addObject("method", method);
    }

    @RequestMapping("/show")
    public ModelAndView show(
            @RequestParam int id,
            @RequestParam int goodOrBad,
            @RequestParam(defaultValue = "1", required = false) int global) {


        ReviewObject reviewObject = null;
        try (Connection connection = DBService.fetchConnection();) {
            reviewObject = DBService.findReviewsByCompanyId(connection, id, goodOrBad);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String companyName = reviewObject.getCompanyName();

        List<Review> originReviews = reviewObject.getOriginReviews();


        try {
            if (global == 1) {//采用全局数据
                List<String> wholeReviews = new ArrayList<>();
                try (Connection connection = DBService.fetchConnection();) {
                    wholeReviews = DBService.fetchAllReviews(connection, 0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                SentenceRequest clearReq = new SentenceRequest();
                clearReq.setClear(true);
                commentObjectClient.sendSentence(clearReq);
                for (String text : wholeReviews) {
                    SentenceRequest request = new SentenceRequest();
                    request.setSentence(text);
                    commentObjectClient.sendSentence(request);
                }
                SentenceRequest overReq = new SentenceRequest();
                overReq.setOver(true);
                commentObjectClient.sendSentence(overReq);
            } else {
                SentenceRequest clearReq = new SentenceRequest();
                clearReq.setClear(true);
                commentObjectClient.sendSentence(clearReq);
                for (Review review : originReviews) {
                    SentenceRequest request = new SentenceRequest();
                    String text = review.getText();
                    request.setSentence(text);
                    commentObjectClient.sendSentence(request);
                }
                SentenceRequest overReq = new SentenceRequest();
                overReq.setOver(true);
                commentObjectClient.sendSentence(overReq);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }


        NKCluster.DistanceEnum[] enums = NKCluster.DistanceEnum.values();
        Map<String, String> differentResults = new HashMap<>();
        for (NKCluster.DistanceEnum distanceEnum : enums) {
            NKCluster nkCluster = new NKCluster();
            nkCluster.setDistanceType(distanceEnum);
            nkCluster.setOriginReviews(originReviews);
            nkCluster.cluster(0);
            String data = nkCluster.getData(originReviews);

            differentResults.put(distanceEnum.toString(), data);
        }


        return new ModelAndView("show")
                .addObject("companyName", companyName)
                .addObject("id", id)
                .addObject("goodOrBad", goodOrBad)
                .addObject("differentResults", differentResults);
    }
}
