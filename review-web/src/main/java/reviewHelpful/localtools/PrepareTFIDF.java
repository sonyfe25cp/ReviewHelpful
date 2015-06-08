package reviewHelpful.localtools;

import com.omartech.utils.vocabulary.Vocabulary;
import reviewHelpful.DBService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-5-24.
 */
public class PrepareTFIDF {

    public void work() {
        try (Connection connection = DBService.fetchConnection()) {
            List<String> reviews = DBService.fetchAllReviews(connection, 1);

            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setSingleWord(true);

            for (String review : reviews) {
                vocabulary.addText(review);
            }
            vocabulary.addOver();
            vocabulary.saveToDisk("reviews_not_sep");


            List<String> reviewsSep = DBService.fetchAllReviews(connection, 0);

            Vocabulary vocabulary2 = new Vocabulary();
            vocabulary2.setSingleWord(true);

            for (String review : reviewsSep) {
                vocabulary2.addText(review);
            }
            vocabulary2.addOver();
            vocabulary2.saveToDisk("reviews_sep");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void test(){
        Vocabulary vtest = Vocabulary.loadFromDB("reviews_not_sep");
        Map<Integer, Double> sampleMap = vtest.generateVectorMap("这家公司的环境不错");
        for (Map.Entry<Integer, Double> entry : sampleMap.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

    }

    public static void main(String[] args) {
        PrepareTFIDF prepareTFIDF = new PrepareTFIDF();
        prepareTFIDF.work();
        prepareTFIDF.test();
    }
}
