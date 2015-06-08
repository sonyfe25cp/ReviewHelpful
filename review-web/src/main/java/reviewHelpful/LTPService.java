package reviewHelpful;

import com.omartech.utils.spider.DefetcherUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OmarTech on 15-5-27.
 */
public class LTPService {

    enum Type {
        FenCi,
        CiXingBiaoZhu,
        MingMingShiTi,
        YiCunJuFa,
        YuYiJueSe
    }

    static String api_key = "33G744x0ANkQtIruPIfSltjeLJSIJkErNpzNfLBC";


    public static String urlMaker(Type type, String text) {

        String pattern = "";
        switch (type) {
            case YiCunJuFa:
                pattern = "dp";
                break;
            case CiXingBiaoZhu:
                pattern = "pos";
                break;
            case FenCi:
                pattern = "ws";
                break;
            case MingMingShiTi:
                pattern = "ner";
                break;
            case YuYiJueSe:
                pattern = "srl";
                break;
        }
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://ltpapi.voicecloud.cn/analysis/");
        urlBuilder.append("?api_key" + api_key);
        urlBuilder.append("&text=" + text);
        urlBuilder.append("&pattern=" + pattern);
        urlBuilder.append("&format=plain");
        return urlBuilder.toString();
    }

    public static String fetchResponse(String url, String text) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
//        HttpGet get = new HttpGet(url);
        HttpPost post = new HttpPost(url);

        BasicNameValuePair bn1 = new BasicNameValuePair("api_key", api_key);
        BasicNameValuePair bn2 = new BasicNameValuePair("text", text);
        BasicNameValuePair bn3 = new BasicNameValuePair("pattern", "pos");
        BasicNameValuePair bn4 = new BasicNameValuePair("format", "plain");

        List<NameValuePair> nvps =new ArrayList<>();
        nvps.add(bn1);
        nvps.add(bn2);
        nvps.add(bn3);
        nvps.add(bn4);
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String result = "";
        try (CloseableHttpResponse response = client.execute(post);) {
            result = DefetcherUtils.toString(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
