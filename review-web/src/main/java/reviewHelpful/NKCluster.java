package reviewHelpful;

import com.omartech.utils.vocabulary.Distance;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reviewHelpful.model.Review;
import reviewHelpful.model.ReviewObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by OmarTech on 15-5-22.
 */
public class NKCluster {
    static Logger logger = LoggerFactory.getLogger(NKCluster.class);

    private Map<Integer, Double> faiMap = new HashMap<>();
    private Map<Integer, Integer> rouMap = new HashMap<>();

    private DistanceEnum distanceType;

    public void setDistanceType(DistanceEnum distanceType) {
        this.distanceType = distanceType;
    }

    public Map<Integer, List<Review>> fetchCluster(List<Integer> seeds) {
        Map<Integer, List<Review>> cluster = new HashMap<>();
        for (int i = 0; i < originReviews.size(); i++) {

            double tmpMinDis = maxDis + 1;
            int centroid = -1;
            for (Integer seed : seeds) {
                String key = i + "-" + seed.intValue();
                Double diss = distanceMap.get(key);
                if (diss == null) {
                    key = seed.intValue() + "-" + i;
                    diss = distanceMap.get(key);
                }
                logger.info("key:{}, distance:{}", key, diss);
                if (tmpMinDis > diss) {
                    tmpMinDis = diss;
                    centroid = seed;
                }
            }

            Review review = originReviews.get(i);

            if (centroid == i) {
                review.setCentroid(true);
            }

            List<Review> reviews = cluster.get(centroid);
            if (reviews == null) {
                reviews = new ArrayList<>();
            }
            reviews.add(review);

            logger.info("{} belongs to {}", review.getText(), centroid);

            cluster.put(centroid, reviews);
        }
        return cluster;
    }

    public enum DistanceEnum {
        Jaccard,
        Cosine,
        Customed,
        NounAndAdj,
        Length
    }

    List<Review> originReviews;

    private Map<String, Double> distanceMap = new HashMap<>();

    private double maxDis = -1;
    private double minDis = Double.MAX_VALUE;

    private int maxRou = -1;
    private int minRou = Integer.MAX_VALUE;

    public void cluster(double dc) {


        switch (distanceType) {
            case Jaccard:
                logger.info("use Jaccard distance");
                break;
            case Cosine:
                logger.info("use Cosine distance");
                break;
            case Customed:
                logger.info("use Customed distance");
                break;
            case Length:
                logger.info("use Length distance");
            default:
                break;
        }


        for (int i = 0; i < originReviews.size(); i++) {
            Review review = originReviews.get(i);
            for (int j = i; j < originReviews.size(); j++) {
                Review review2 = originReviews.get(j);
                double dis = distance(review, review2, distanceType);
                maxDis = maxDis > dis ? maxDis : dis;
                minDis = minDis > dis ? dis : minDis;
                String key = i + "-" + j;
                distanceMap.put(key, dis);
                logger.info("distance key :{}, distance:{}", key, dis);
            }
        }

        logger.info("最大距离:{}, 最小距离:{}", maxDis, minDis);

        if (dc == 0) {//自动生成dc
            double step = (maxDis - minDis + 0.0) / originReviews.size();
            dc = minDis + step;
            int avaNeibor = -1;
            int low = Math.round(originReviews.size() / 100.0f);
            int high = Math.round(originReviews.size() / 100.0f * 3);
            logger.info("dc_low:{}, dc_high:{}", low, high);
            int round = 0;
            do {
                int totalNei = 0;
                for (int i = 0; i < originReviews.size(); i++) {
                    int tempNei = 0;
                    for (int j = 0; j < originReviews.size(); j++) {
                        String key = i + "-" + j;
                        Double aDouble = distanceMap.get(key);
                        if (aDouble == null) {
                            key = j + "-" + i;
                            aDouble = distanceMap.get(key);
                        }
                        if (aDouble <= dc) {
                            tempNei++;
                        }

                    }
                    totalNei += tempNei;
                    logger.info("round:{}, dc:{}, i:{}, nei:{}", new String[]{round + "", dc + "", i + "", tempNei + ""});
                }
                avaNeibor = Math.round(totalNei / (originReviews.size() + 0.0f));
                logger.info("round:{}, dc:{}, totalNei:{}, avaNeibo:{}", new String[]{round + "", dc + "", totalNei + "", avaNeibor + ""});
                round++;
                if (avaNeibor > high) {
                    break;
                } else {
                    dc = dc + step;
                }
            } while (!(avaNeibor > low && avaNeibor < high));
            logger.info("best dc: {}", dc);
        }


        for (int i = 0; i < originReviews.size(); i++) {
            int rou = 0;
            for (Map.Entry<String, Double> entry : distanceMap.entrySet()) {
                String key = entry.getKey();
                double value = entry.getValue();
                if (key.startsWith(i + "-") || (key.startsWith("-" + i))) {
                    if (value <= dc) {
                        rou++;
                    }
                }
            }
            maxRou = Math.max(maxRou, rou);
            minRou = Math.min(minRou, rou);
            rouMap.put(i, rou);
        }
        logger.info("最大密度:{}, 最小密度:{}", maxRou, minDis);

        for (int i = 0; i < originReviews.size(); i++) {
            int rou = rouMap.get(i);
//            logger.info("i:{}, rou:{}", i, rou);
            Map<Integer, Integer> smap = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : rouMap.entrySet()) {
                Integer pos = entry.getKey();
                Integer rouV = entry.getValue();
                if (rouV > rou) {
                    smap.put(pos, rouV);
                }
            }

            logger.info("*************");
            logger.info("for point:{}, there are {} points' rou bigger than it", i, smap.size());
            for (Map.Entry<Integer, Integer> entry : smap.entrySet()) {
                logger.info("point : {}, rou: {}", entry.getKey(), entry.getValue());
            }
            Double fai = maxDis;
            if (smap.size() > 0) {

                Set<Integer> biggerRous = new HashSet<>();
                for (Map.Entry<Integer, Integer> entry : smap.entrySet()) {
                    Integer srou = entry.getValue();
                    Integer spoint = entry.getKey();
                    if (srou > rou) {
                        biggerRous.add(spoint);
                    }
                }


                //要找bigger rou 中 distance 最小的
                for (Integer bigger : biggerRous) {
                    String key = i + "-" + bigger;
                    Double tmpFai = distanceMap.get(key);
                    if (tmpFai == null) {
                        key = bigger + "-" + i;
                        tmpFai = distanceMap.get(key);
                    }
                    if (tmpFai < fai) {
                        fai = tmpFai;
                    }
                }

                if (fai == null) {
                    logger.error("not fai, key:{}", i);
                }
            } else {
                logger.info("最大点没有fai了, i : {}", i);
                fai = maxDis;
            }
            logger.info("i:{}, fai:{}", i, fai);
            faiMap.put(i, fai);
        }

//        System.out.println("index | rou | fai");
//        for (int i = 0; i < originReviews.size(); i++) {
//            Integer rou = rouMap.get(i);
//            Double fai = faiMap.get(i);
//            System.out.println("[" + rou + "," + fai + "], ");
//        }

    }

    public static double distance(Review review1, Review review2, DistanceEnum distanceType) {
        String text1 = review1.getText();
        String text2 = review2.getText();

        double distance = 0;
        switch (distanceType) {
            case Jaccard:
                distance = Distance.jaccard(text1, text2);
                distance = 1 - distance;
//                logger.info("[{}] vs [{}] = {}", new String[]{StringUtils.deleteWhitespace(text1), StringUtils.deleteWhitespace(text2), distance + ""});
                break;
            case Cosine:
//                Vocabulary vtest = Vocabulary.loadFromDB("reviews_not_sep");
//                Map<Integer, Double> sampleMap = vtest.generateVectorMap("这家公司的环境不错");
//                for (Map.Entry<Integer, Double> entry : sampleMap.entrySet()) {
//                    System.out.println(entry.getKey() + " - " + entry.getValue());
//                }
//
//                Vocabulary vocabulary = Vocabulary.loadFromDB("reviews_sep");
////                Vocabulary vocabulary = Vocabulary.loadFromDB("reviews_not_sep");
//                double[] double1 = vocabulary.generateVector(text1);
//                double[] double2 = vocabulary.generateVector(text2);
//                distance = Distance.cosine(double1, double2);
                break;
            case Customed:
                int rating = review1.getRating();
                int recommend_friend = review1.getRecommend_friend();

                int rating1 = review2.getRating();
                int recommend_friend1 = review2.getRecommend_friend();

                distance = (rating1 - rating) * (rating1 - rating) + (recommend_friend1 - recommend_friend) * (recommend_friend1 - recommend_friend);
                break;

            case NounAndAdj:
                List<Term> terms1 = NlpAnalysis.parse(text1);


                List<Term> terms2 = NlpAnalysis.parse(text2);

                break;
            case Length:
                distance = Math.abs(text1.length() - text2.length());
                break;
        }
        return distance;
    }


    public String getData(List<Review> originReviews) {
        StringBuilder dataBuilder = new StringBuilder();
        for (int i = 0; i < originReviews.size(); i++) {
            Integer rou = rouMap.get(i);
            Double fai = faiMap.get(i);
            Review review = originReviews.get(i);

            dataBuilder.append("{x:" + rou + ",y:" + fai + ",extra:'" + StringUtils.deleteWhitespace(review.getText()).replaceAll("\n", "").replaceAll("['}]", "") + "'},");
        }
        dataBuilder.setLength(dataBuilder.length() - 1);
        return dataBuilder.toString();
    }

    public static void main(String[] args) {

        int id = 3131;
        try (Connection connection = DBService.fetchConnection()) {
            ReviewObject goodReviews = DBService.findReviewsByCompanyId(connection, id, 1);
            NKCluster nkCluster = new NKCluster();
            List<Review> originReviews1 = goodReviews.getOriginReviews();
            nkCluster.setOriginReviews(originReviews1);
            nkCluster.cluster(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Double> getFaiMap() {
        return faiMap;
    }

    public Map<Integer, Integer> getRouMap() {
        return rouMap;
    }

    public double getMaxDis() {
        return maxDis;
    }


    public double getMinDis() {
        return minDis;
    }

    public int getMaxRou() {
        return maxRou;
    }

    public void setOriginReviews(List<Review> originReviews) {
        this.originReviews = originReviews;
    }
}
