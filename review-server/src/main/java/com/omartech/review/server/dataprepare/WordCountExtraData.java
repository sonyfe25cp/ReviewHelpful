package com.omartech.review.server.dataprepare;

import com.omartech.utils.Utils;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by OmarTech on 15-6-19.
 */
public class WordCountExtraData {

    Map<String, Integer> countMap = new HashMap<>();

    public void run() throws IOException {
        int max = 103611;
        int limit = 1000;
        Set<String> stopWords = new HashSet<>();
        try (Connection connection = reviewCon.get()) {
            int begin = 0;
            do {
                System.out.println("begin : " + begin);
                Map<Integer, String> map = find(connection, begin, limit);
                for (Map.Entry<Integer, String> entry : map.entrySet()) {
                    String line = entry.getValue();
                    Integer key = entry.getKey();
                    List<Term> termList = ToAnalysis.parse(line);
                    for (Term term : termList) {
                        String name = term.getName();
                        if (name.length() > 1 && (!stopWords.contains(name))) {
                            Integer integer = countMap.get(name);
                            if (integer == null) {
                                integer = 0;
                            }
                            integer++;
                            countMap.put(name, integer);
                        }
                    }
                    begin = Math.max(key, begin);
                }
            } while (begin < max);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Set<Map.Entry<String, Integer>> entries = countMap.entrySet();

        List<Map.Entry<String, Integer>> entryList = new ArrayList(entries);
        Utils.sortMapStringAndInteger(entryList, true);
        File file = new File("extralData");
        if (file.exists()) {
            file.delete();
        }

        for (Map.Entry<String, Integer> entry : entryList) {

            FileUtils.write(file, entry.getKey() + " " + entry.getValue() + "\n", true);
        }

    }


    public Map<Integer, String> find(Connection connection, int begin, int pageSize) throws SQLException {
        Map<Integer, String> map = new HashMap<>();
        String sql = "select id, content from articles where id > " + begin + " limit " + pageSize;
        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    String content = rs.getString("content");
                    int id = rs.getInt("id");
                    map.put(id, content);
                }
            }
        }
        return map;
    }


    public static void main(String[] args) {
        WordCountExtraData wordCountExtraData = new WordCountExtraData();
        try {
            wordCountExtraData.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ThreadLocal<Connection> reviewCon = new InheritableThreadLocal<Connection>() {
        @Override
        protected Connection initialValue() {
            Connection conn = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/webrisk", "root", "");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    };

}
