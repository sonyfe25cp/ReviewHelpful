package reviewHelpful.localtools;

import com.omartech.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reviewHelpful.FetchAllReviewsLtp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-3.
 */
public class PrepareObjSentiments {
    static Logger logger = LoggerFactory.getLogger(PrepareObjSentiments.class);

    public void run() {
        File file = new File("cxbz.txt");
        List<CXBZ> cxbzs = new ArrayList<>();
        try {
            cxbzs = cxbzRead(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int an_one = 0;
        int an_multi = 0;
        Map<String, Integer> nMap = new HashMap<>();
        Map<String, Integer> aMap = new HashMap<>();
        for (CXBZ cx : cxbzs) {
            String cxbz = cx.cxbz;
            String sentence = cx.sentence;
            List<String> cxbzArray = cx.cxbzArray;
            if (cxbz.contains("_a") && cxbz.contains("_n")) {
                logger.info("sentence:{}, czbz:{}", sentence, cxbz);

                int end_n = 0;
                int end_a = 0;
                for (String str : cxbzArray) {
                    if (str.endsWith("_a")) {
                        end_a++;
                    }
                    if (str.endsWith("_n")) {
                        end_n++;
                    }
                }

                if (end_a == 1 && end_a == 1) {//an_one :24667, an_multi:2785
                    for (String str : cxbzArray) {
                        if (str.endsWith("_a")) {
                            String s = removePOS(str);
                            Integer integer = aMap.get(s);
                            if (integer == null) {
                                integer = 0;
                            }
                            integer++;
                            aMap.put(s, integer);
                        }
                        if (str.endsWith("_n")) {
                            String s = removePOS(str);
                            Integer integer = nMap.get(s);
                            if (integer == null) {
                                integer = 0;
                            }
                            integer++;
                            nMap.put(s, integer);
                        }
                    }
                    an_one++;
                } else {
                    an_multi++;
                }
            }
        }
        logger.info("an_one:{}, an_multi:{}", an_one, an_multi);

        List<Map.Entry<String, Integer>> amapList = new ArrayList<>(aMap.entrySet());
        List<Map.Entry<String, Integer>> nmapList = new ArrayList<>(nMap.entrySet());

        Utils.sortMapStringAndInteger(amapList, true);
        Utils.sortMapStringAndInteger(nmapList, true);

        for (Map.Entry<String, Integer> entry : amapList) {
            logger.info("key:{}, value:{}", entry.getKey(), entry.getValue());
        }
        logger.info("********************************************************");
        for (Map.Entry<String, Integer> entry : nmapList) {
            logger.info("key:{}, value:{}", entry.getKey(), entry.getValue());
        }


    }

    public static String removePOS(String string) {
        String s2 = string.replaceAll("(_.+)", "");
        return s2;
    }

    public static List<CXBZ> cxbzRead(File file) throws IOException {
        List<CXBZ> list = new ArrayList<>();
        List<String> lines = FileUtils.readLines(file);
        for (String line : lines) {
            boolean valid = FetchAllReviewsLtp.valid(line);
            if (valid) {

                String[] split = line.split("\t");
                String sentence = split[0];
                String cxbz = split[1];
                String[] tmps = cxbz.split(" ");
                List<String> tmpList = new ArrayList<>(tmps.length);
                for (int i = 0; i < tmps.length; i++) {
                    tmpList.add(tmps[i]);
                }

                CXBZ obj = new CXBZ();
                obj.cxbz = cxbz;
                obj.sentence = sentence;
                obj.cxbzArray = tmpList;

                list.add(obj);
            }
        }
        return list;
    }


    public static void main(String[] args) {
        PrepareObjSentiments pos = new PrepareObjSentiments();
        pos.run();
    }
}

class CXBZ {
    String sentence;
    String cxbz;
    List<String> cxbzArray;
}
