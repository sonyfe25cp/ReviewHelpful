package reviewHelpful;

import com.omartech.utils.DBUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reviewHelpful.model.ReviewObject;
import reviewHelpful.model.Review;
import reviewHelpful.model.StatReviewCount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OmarTech on 15-5-6.
 */
public class DBService {

    static Logger logger = LoggerFactory.getLogger(DBService.class);

    //sep 0:每条好评差评建议为单独一条数据
    //sep 1:每条好评+差评+建议为单独一条数据
    public static List<String> fetchAllReviews(Connection conn, int sep) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "select pros, cons, advice from reviews_task";
        try (PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet resultSet = psmt.executeQuery()) {
            while (resultSet.next()) {
                String pros = resultSet.getString("pros");
                String cons = resultSet.getString("cons");
                String advice = resultSet.getString("advice");
                if (sep == 0) {
                    list.add(pros);
                    list.add(cons);
                    list.add(advice);
                } else if (sep == 1) {
                    list.add(pros + cons + advice);
                }
            }
        }
        return list;
    }

    public static ReviewObject findReviewsByCompanyId(Connection conn, int companyId, int emotion) throws SQLException {
        ReviewObject ro = new ReviewObject();
        String sql = "select id, rating, recommend_friend, weight, pros, cons, advice,name from reviews_task where company_id = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql);) {
            psmt.setInt(1, companyId);
            try (ResultSet resultSet = psmt.executeQuery();) {
                List<Review> reviews = new ArrayList<>();
                while (resultSet.next()) {
                    Review review = new Review();
                    int id = resultSet.getInt("id");
                    review.setId(id);
                    int rating = resultSet.getInt("rating");
                    review.setRating(rating);
                    int recommend_friend = resultSet.getInt("recommend_friend");
                    review.setRecommend_friend(recommend_friend);
                    String pros = resultSet.getString("pros");
                    String cons = resultSet.getString("cons");
                    String advice = resultSet.getString("advice");
                    if (emotion == 1) {
                        review.setText(pros);
                    } else if (emotion == 2) {
                        review.setText(cons);
                    } else if (emotion == 3) {
                        review.setText(advice);
                    }
                    int weight = resultSet.getInt("weight");
                    review.setWeight(weight);
                    String name = resultSet.getString("name");
                    ro.setCompanyName(name);
                    reviews.add(review);
                }
                ro.setOriginReviews(reviews);
            }
            ro.setId(companyId);
        }
        if (StringUtils.isEmpty(ro.getCompanyName())) {
            logger.error("error about this");
            return null;
        } else {
            return ro;
        }
    }

    public static List<StatReviewCount> statReviewCounts(Connection conn) throws SQLException {
        String sql = "select company_id, name, count(1) c from reviews_task group by company_id";
        List<StatReviewCount> list = new ArrayList<>();
        try (PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet resultSet = psmt.executeQuery();) {
            while (resultSet.next()) {
                StatReviewCount statReviewCount = new StatReviewCount();
                int id = resultSet.getInt("company_id");
                String name = resultSet.getString("name");
                int c = resultSet.getInt("c");

                statReviewCount.setId(id);
                statReviewCount.setName(name);
                statReviewCount.setCount(c);
                list.add(statReviewCount);
            }
        }
        return list;
    }


    public static Connection fetchConnection() {
        Connection connection = null;
        boolean flag = false;
        do {
            connection = newsCon.get();
            flag = DBUtils.verifyConnection(connection, "select id from reviews_task limit 1");
            if (!flag) {
                newsCon.remove();
            }
        } while (!flag);
        return connection;
    }

    static ThreadLocal<Connection> newsCon = new InheritableThreadLocal<Connection>() {
        @Override
        protected Connection initialValue() {
            Connection conn = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/kanzhun", "root", "");
                logger.info("new connection to webrisk");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    };
}
