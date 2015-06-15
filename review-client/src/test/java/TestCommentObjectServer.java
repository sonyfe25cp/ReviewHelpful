
import com.omartech.review.client.ClientException;
import com.omartech.review.client.DataClients;
import com.omartech.review.gen.ReviewFeatureResponse;
import com.omartech.review.gen.SentenceRequest;

import java.util.Arrays;
import java.util.List;

/**
 * Created by OmarTech on 15-6-8.
 */
public class TestCommentObjectServer {
    public static void main(String[] args) {
        DataClients clients = new DataClients("127.0.0.1:8124,127.0.0.1:8124");

        List<String> strings = Arrays.asList(new String[]{"对新人的培训不够", "压力偏大", "电脑程序太繁琐"});
        for (String s : strings) {
            SentenceRequest sr = new SentenceRequest();
            sr.setSentence(s);
            try {
                clients.sendSentence(sr);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }

        SentenceRequest srover = new SentenceRequest();
        srover.setOver(true);
        try {
            clients.sendSentence(srover);
        } catch (ClientException e) {
            e.printStackTrace();
        }


        String sentence = "压力偏大";

        SentenceRequest req = new SentenceRequest();
        req.setSentence(sentence);

        try {
            ReviewFeatureResponse features = clients.findFeatures(req);

            List<String> adjs = features.getAdjs();

            System.out.println(adjs);

            List<String> words = features.getWords();
            System.out.println(words);
        } catch (ClientException e) {
            e.printStackTrace();
        }


    }
}
