package reviewHelpful;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by OmarTech on 15-5-27.
 */
public class CutSentenceService {

    public static List<String> cut(String sentence) {

        String[] strings = sentence.split("[ ,.。，！；、]");

        List<String> list = Arrays.asList(strings);

        return list;
    }

    public static void main(String[] args) {
        String text = "1、世界500强公司2、公司福利待遇不错 培训机会很多；";
        List<String> cut = cut(text);
        System.out.println(cut.size());
        for (String s : cut) {
            System.out.println(s);
        }
    }
}
