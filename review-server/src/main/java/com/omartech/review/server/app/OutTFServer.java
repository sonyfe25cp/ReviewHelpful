package com.omartech.review.server.app;

import com.omartech.review.gen.ExtralTFRequest;
import com.omartech.review.gen.ExtralTFResponse;
import com.omartech.review.server.service.ADataService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by OmarTech on 15-6-23.
 */
public class OutTFServer extends ADataService {

    static Map<String, Integer> map = new HashMap<>();

    void run() {
        File file = new File("extralData");
        try {
            List<String> lines = FileUtils.readLines(file);
            for (String line : lines) {
                if (!StringUtils.isEmpty(line)) {
                    String[] strings = line.split(" ");
                    int num = Integer.parseInt(strings[1]);
                    map.put(strings[0], num);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExtralTFResponse findExtralTF(ExtralTFRequest req) throws TException {
        String word = req.getWord();
        Integer integer = map.get(word);
        ExtralTFResponse response = new ExtralTFResponse();
        response.setCount(integer);
        return response;
    }

    public static void main(String[] args) {
        OutTFServer outServer = new OutTFServer();
        outServer.notSearch = true;
        outServer.port = 8125;
        outServer.parseArgsAndRun(args);
    }

}
