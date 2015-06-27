package com.omartech.review.server.dataprepare;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-17.
 */
public class TextForLDA {

    public static Map<String, String> fetchAll(Connection connection) {
        String sql = "select pros, advice, cons, company_id from reviews_task";
        Map<String, String> map = new HashMap<>();
        try (PreparedStatement psmt = connection.prepareStatement(sql);
             ResultSet resultSet = psmt.executeQuery()) {
            while (resultSet.next()) {
                String pros = resultSet.getString("pros");
                String cons = resultSet.getString("cons");
                String advice = resultSet.getString("advice");
                String company_id = resultSet.getString("company_id");

                String tmp = pros + " " + advice + " " + cons;
                tmp = tmp.replaceAll("[\r|\n]", " ");

                String s = map.get(company_id);
                if (s == null) {
                    s = "";
                }
                s += tmp;

                map.put(company_id, s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }


    public static void main(String[] args) {
        File file = new File("perComPerLine");
        if (file.exists()) {
            file.delete();
        }
        try (Connection connection = newsCon.get()) {
            Map<String, String> map = fetchAll(connection);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                FileUtils.write(file, key + "\t" + value + "\n", true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
