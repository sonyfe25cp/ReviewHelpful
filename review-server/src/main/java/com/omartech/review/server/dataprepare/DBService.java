package com.omartech.review.server.dataprepare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by OmarTech on 15-6-18.
 */
public class DBService {
    static Logger logger = LoggerFactory.getLogger(DBService.class);
    public static ThreadLocal<Connection> reviewCon = new InheritableThreadLocal<Connection>() {
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

    //sep 0:每条好评差评建议为单独一条数据
    //sep 1:每条好评+差评+建议为单独一条数据
    public static Map<String, Integer> fetchAllReviewsMap(Connection conn, int sep) throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sql = "select weight, pros, cons, advice from reviews_task";
        try (PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet resultSet = psmt.executeQuery()) {
            while (resultSet.next()) {
                String pros = resultSet.getString("pros");
                String cons = resultSet.getString("cons");
                String advice = resultSet.getString("advice");
                int weight = resultSet.getInt("weight");
                if (sep == 0) {
                    map.put(pros, weight);
                    map.put(cons, weight);
                    map.put(advice, weight);
                } else if (sep == 1) {
                    String tmp = pros + cons + advice;
                    map.put(tmp, weight);
                }
            }
        }
        return map;
    }

    public static List<String> fetchAllReviews(Connection conn, int sep) throws SQLException {
        Map<String, Integer> stringIntegerMap = fetchAllReviewsMap(conn, sep);
        Set<String> strings = stringIntegerMap.keySet();
        return new ArrayList<>(strings);
    }
}
