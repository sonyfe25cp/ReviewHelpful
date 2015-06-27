package com.omartech.review.server.dataprepare;

import com.omartech.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-15.
 */
public class LabeledDataBias2 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Utils.getResourceList("bias-2");

        Map<Integer, List<Triple>> map = new HashMap<>();
        for (String line : lines) {
            String[] strings = line.split("\\|");
            int uid = clear(strings[0]);
            int weight = clear(strings[1]);
            int count = clear(strings[2]);

            List<Triple> triples = map.get(uid);
            if (triples == null) {
                triples = new ArrayList<>();
            }
            triples.add(new Triple(uid, weight, count));
            map.put(uid, triples);
        }

        for (Map.Entry<Integer, List<Triple>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            List<Triple> value = entry.getValue();
            System.out.println("name:'" + key + "',");
            System.out.print("data:[");
            for (Triple triple : value) {
                System.out.print(triple.count + ", ");
            }
            System.out.println("]\n},{");
        }
    }

    public static int clear(String tmp) {
        tmp = StringUtils.deleteWhitespace(tmp);
        int num = Integer.parseInt(tmp);
        return num;
    }
}


class Triple {
    int uid;
    int weight;
    int count;

    public Triple(int uid, int weight, int count) {
        this.uid = uid;
        this.weight = weight;
        this.count = count;
    }
}
