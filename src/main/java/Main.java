import model.Review;
import model.ReviewObject;
import model.StatReviewCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by OmarTech on 15-5-6.
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    void generateOriginPages() {
        Connection connection = DBService.fetchConnection();
        try {
            List<StatReviewCount> statReviewCounts = DBService.statReviewCounts(connection);
            Collections.sort(statReviewCounts, new Comparator<StatReviewCount>() {
                @Override
                public int compare(StatReviewCount o1, StatReviewCount o2) {
                    return o2.getCount() - o1.getCount();
                }
            });
            HtmlProducer.produceStatPage(statReviewCounts);
            logger.info("list page is over");

            for (StatReviewCount statReviewCount : statReviewCounts) {
                int id = statReviewCount.getId();
                ReviewObject goodReviewsByCompanyId = DBService.findReviewsByCompanyId(connection, id, 1);
                List<Review> originReviews = goodReviewsByCompanyId.getOriginReviews();
                Sort.sortByDefaultWeight(originReviews);
                goodReviewsByCompanyId.setOriginReviews(originReviews);
                logger.info("sort origin review order by weight");
                HtmlProducer.produceDetailsPage(goodReviewsByCompanyId, "good");

                ReviewObject badReviewsByCompanyId = DBService.findReviewsByCompanyId(connection, id, 2);
                List<Review> originReviews2 = badReviewsByCompanyId.getOriginReviews();
                Sort.sortByDefaultWeight(originReviews2);
                badReviewsByCompanyId.setOriginReviews(originReviews2);
                HtmlProducer.produceDetailsPage(badReviewsByCompanyId, "bad");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Main main = new Main();
        main.generateOriginPages();

    }

}
