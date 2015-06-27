package com.omartech.review.server.dataprepare;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-15.
 */
public class LabeledDataBias {

    public static void updateAuditUid(Connection connection, int audit, int review_id) throws SQLException {
        String sql = "update reviews_task set audit_uid = ? where id = ?";
        try (PreparedStatement pmt = connection.prepareStatement(sql)) {
            pmt.setInt(1, audit);
            pmt.setInt(2, review_id);
            pmt.executeUpdate();
        }
    }

    public static Map<Integer, Integer> fetchAuditMap(Connection connection) throws SQLException {
        Map<Integer, Integer> map = new HashMap<>();
        String sql = "select id, audit_uid from company_review";
        try (PreparedStatement psmt = connection.prepareStatement(sql);
             ResultSet resultSet = psmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int audit_uid = resultSet.getInt("audit_uid");
                map.put(id, audit_uid);
            }
        }
        return map;
    }

    public static void main(String[] args) {
        try (Connection conn = newsCon.get()) {
            Map<Integer, Integer> integerIntegerMap = fetchAuditMap(conn);
            for (Map.Entry<Integer, Integer> entry : integerIntegerMap.entrySet()) {
                Integer id = entry.getKey();
                Integer uid = entry.getValue();
                updateAuditUid(conn, uid, id);
                System.out.println("id: " + id + " uid:" + uid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static ThreadLocal<Connection> newsCon = new InheritableThreadLocal<Connection>() {
        @Override
        protected Connection initialValue() {
            Connection conn = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/kanzhun", "root", "");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    };
}
