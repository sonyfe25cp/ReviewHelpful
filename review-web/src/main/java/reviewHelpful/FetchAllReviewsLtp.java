package reviewHelpful;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-5-28.
 */
public class FetchAllReviewsLtp {
    static Logger logger = LoggerFactory.getLogger(FetchAllReviewsLtp.class);

    void fetchAll() {

        File file = new File("cxbz.txt");
        Map<String, String> cache = new HashMap<>();
        try {
            List<String> tmplist = FileUtils.readLines(file);
            for (String str : tmplist) {
                boolean valid = valid(str);
                if (valid) {
                    String[] split = str.split("\t");
                    if (split.length == 2) {
                        cache.put(split[0], split[1]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file.exists()) {
            file.delete();
        }
        for (Map.Entry<String, String> entry : cache.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String kv = key + "\t" + value + "\n";
            try {
                FileUtils.write(file, kv, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> strings = new ArrayList<>();
        try (Connection connection = DBService.fetchConnection();) {
            strings = DBService.fetchAllReviews(connection, 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String str : strings) {
            str = str.replaceAll("\n", " ");
            List<String> cuts = CutSentenceService.cut(str);
            for (String s : cuts) {
                s = s.replaceAll("\n", " ");
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                if (cache.containsKey(s)) {
                    continue;
                }
                String url = "http://ltpapi.voicecloud.cn/analysis/";
                String result = LTPService.fetchResponse(url, s);
                logger.info("{} -> {}", s, result);
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    String s1 = s + "\t" + result + "\n";
                    cache.put(s, result);
                    FileUtils.write(file, s1, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //有的记录是错的
    //1.含error_message
    //2.前后长度不同
    public static boolean valid(String line) {
        if (line.contains("error_message")) {
            return false;
        }
        String[] split = line.split("\t");
        if (split.length == 2) {
            String s = split[0];
            String s1 = split[1];

            String[] array = s1.split(" ");
            String last = array[0];
            int i = 0;
            for (String a : array) {
                String s2 = a.replaceAll("(_.+)", "");
                i = i + s2.length();
                last = s2;
            }

            if (s.length() == i && s.contains(last)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        FetchAllReviewsLtp fetchAllReviewsLtp = new FetchAllReviewsLtp();
        fetchAllReviewsLtp.fetchAll();
    }

}
