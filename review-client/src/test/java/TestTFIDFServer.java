
import com.omartech.review.client.ClientException;
import com.omartech.review.client.DataClients;
import com.omartech.review.gen.SentenceRequest;
import com.omartech.review.gen.TFIDFResponse;

import java.util.Map;

/**
 * Created by OmarTech on 15-6-8.
 */
public class TestTFIDFServer {

    public static void main(String[] args) throws ClientException {
        DataClients clients = new DataClients("127.0.0.1:8123,127.0.0.1:8123");

        SentenceRequest clearReq = new SentenceRequest();
        clearReq.setClear(true);
        clients.sendSentence(clearReq);

        SentenceRequest sr1 = new SentenceRequest();
        sr1.setSentence("我爱北京天安门");
        SentenceRequest sr2 = new SentenceRequest();
        sr2.setSentence("天安门上太阳升");
        sr2.setOver(true);

        clients.sendSentence(sr1);
        clients.sendSentence(sr2);

        String sentence = "北京天安门真好看";

        SentenceRequest sr3 = new SentenceRequest();
        sr3.setSentence(sentence);
        TFIDFResponse tfidfResponse = clients.tfidf(sr3);
        Map<String, Double> stringMap = tfidfResponse.getStringMap();
        for (Map.Entry<String, Double> entry : stringMap.entrySet()) {
            System.out.println(entry.getKey() + " -- " + entry.getValue());
        }
        Map<Integer, Double> positionMap = tfidfResponse.getPositionMap();
        for (Map.Entry<Integer, Double> entry : positionMap.entrySet()) {
            System.out.println("位置:"+entry.getKey() + "  -- " + entry.getValue());
        }

    }

}
