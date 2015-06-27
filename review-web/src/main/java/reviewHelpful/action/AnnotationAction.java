package reviewHelpful.action;

import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import reviewHelpful.ClientsFetcher;
import reviewHelpful.DBService;
import reviewHelpful.model.Review;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by OmarTech on 15-6-18.
 */
@Controller
@RequestMapping("/ann")
public class AnnotationAction {


    @RequestMapping("/next")
    public ModelAndView show() {
        String ann = "";
        Connection connection = DBService.fetchConnection();
        List<Review> reviews = null;
        try {
            reviews = DBService.fetchNextWithoutAnn(connection, ann);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ModelAndView("/ann/show").addObject("reviews", reviews);
    }

    public String post(@RequestParam int id,
                       @RequestParam int proScore,
                       @RequestParam int conScore,
                       @RequestParam int advScore,
                       HttpRequest request){
        String ann = "";
        Connection connection = DBService.fetchConnection();
        try {
            DBService.insertAnnotate(connection, id, proScore, conScore, advScore, ann);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  "redirect:/ann/next";

    }
}
