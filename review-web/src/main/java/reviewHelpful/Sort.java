package reviewHelpful;

import reviewHelpful.model.Review;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by OmarTech on 15-5-6.
 */
public class Sort {

    public static void sortByDefaultWeight(List<Review> reviews) {
        Collections.sort(reviews, new Comparator<Review>() {
            @Override
            public int compare(Review o1, Review o2) {
                return o2.getWeight() - o1.getWeight();


            }
        });

    }
}
