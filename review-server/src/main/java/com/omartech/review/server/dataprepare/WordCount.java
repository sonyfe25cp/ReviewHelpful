package com.omartech.review.server.dataprepare;

import com.omartech.utils.Utils;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-17.
 */
public class WordCount {
    public static void main(String[] args) throws IOException {
        List<String> lines = FileUtils.readLines(new File("perComPerLine"));
        Map<String, Integer> map = new HashMap<>();
        for (String line : lines) {
            List<Term> terms = ToAnalysis.parse(line);
            for (Term term : terms) {
                String name = term.getName();
                Integer integer = map.get(name);
                if (integer == null) {
                    integer = 0;
                }
                integer = integer + 1;
                map.put(name, integer);
            }
        }
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
        Utils.sortMapStringAndInteger(entryList, true);
        for (Map.Entry<String, Integer> entry : entryList) {
            if(entry.getKey().length() > 1) {
                System.out.println(entry.getKey() + " -- " + entry.getValue());
            }
        }
    }
}
