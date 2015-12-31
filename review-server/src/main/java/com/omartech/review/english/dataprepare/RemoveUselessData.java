package com.omartech.review.english.dataprepare;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by omar on 15/12/30.
 */
public class RemoveUselessData {

    public boolean isUseless(String line) {
        String[] strings = line.split("\t");

        //rule1: helpful feedback < 10
        //rule2: feedback < 10
        if (strings.length == 8) {
            int hfd = Integer.parseInt(strings[3]);
            int fd = Integer.parseInt(strings[4]);
//            if (hfd < 20) {
//                return true;
//            }
            if (fd < 20) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    String path = "/Users/omar/data/opinion_spam/reviewsNew.txt";
    int cpu = 8;
    String output = "/Users/omar/data/opinion_spam/reviewsNewClean20.txt";

    public void action() {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(cpu, cpu, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(2 * cpu), new ThreadPoolExecutor.CallerRunsPolicy());

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            FileWriter bw = new FileWriter(new File(output));
            String line = br.readLine();
            int c = 0;
            while (line != null) {
                executor.submit(new Worker(line, bw));
                line = br.readLine();
                c++;
                if (c % 10000 == 0) {
                    System.out.println(c + " lines over");
                }
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
            bw.flush();
            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Worker implements Runnable {
        String line;
        FileWriter bw;

        public Worker(String line, FileWriter bw) {
            this.line = line;
            this.bw = bw;
        }

        @Override
        public void run() {
            boolean useless = isUseless(line);
            if (!useless) {
                try {
                    bw.write(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        RemoveUselessData rud = new RemoveUselessData();
        rud.action();
    }
}
