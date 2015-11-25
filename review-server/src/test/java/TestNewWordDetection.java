import com.omartech.review.modules.app.NewWordDetection;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by work on 15-11-25.
 */
public class TestNewWordDetection extends TestCase{
    @Test
    public void testBinarySearch(){
        NewWordDetection nwd = new NewWordDetection();
//        List<String> lines = Arrays.asList(new String[]{
//                "a","abc", "ababa", "ac", "ac1", "ac2", "ac3", "ad", "add", "addd", "ae", "aff"
//        });
        List<String> lines = Arrays.asList(new String[]{
                "究研局察监","立了行进题问","站网厅察监委","等","籍党除开宏明","索线及题问罪","纪党产共国中","纪市芜莱","纪市芜莱经","纪省东山据","纪违重严宏明","纪违重严成构","线及题问罪犯","终序程定规按","经","经","给定决","网厅察监委纪","网民人","罪犯嫌涉其将","罪犯嫌涉题问","职公除开","芜莱","芜莱","芜莱经","苗区城莱任担","莱","莱","莱","莱任担在宏明","莱市该对委纪","莱经","行","行进题问法违","表代大人市的","规按已会委常","规等","记书副委党镇","记书委党","该对委纪市芜","调案立了行进","财区城莱","财区城莱市该"
        });
        String arg = "芜莱";
        int result = 0;
        for(int i = 0 ; i < lines.size(); i ++){
            if(lines.get(i).equals(arg)){
                result = i;
            }
        }
        System.out.println(result);
        List<String> strings = nwd.findLineswithWord(lines, arg);
        for(String line : strings){
            System.out.println(line);
        }
    }
}
