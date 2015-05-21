package reviewHelpful;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import reviewHelpful.model.ReviewObject;
import reviewHelpful.model.StatReviewCount;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-5-6.
 */
public class HtmlProducer {


    public static void produceDetailsPage(ReviewObject reviewObject, String prefix) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(
                SystemVar.templateFolder));
        Template template = cfg.getTemplate("details.ftl");
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> args = new HashMap<>();
        args.put("reviewObject", reviewObject);
        try {
            template.process(args, stringWriter);
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        File file = new File(SystemVar.htmlOutputFolder + reviewObject.getId() + "-" + prefix + "-details.html");
        if (file.exists()) {
            file.delete();
        }
        FileUtils.write(file, stringWriter.toString());
        stringWriter.close();
    }

    public static void produceStatPage(List<StatReviewCount> statReviewCounts) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(
                SystemVar.templateFolder));
        Template template = cfg.getTemplate("review_stat.ftl");
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> args = new HashMap<>();
        args.put("statReviewCounts", statReviewCounts);
        try {
            template.process(args, stringWriter);
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        File file = new File(SystemVar.htmlOutputFolder + "index.html");
        if (file.exists()) {
            file.delete();
        }
        FileUtils.write(file, stringWriter.toString());
        stringWriter.close();
    }


}
