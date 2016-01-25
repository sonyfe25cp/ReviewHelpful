package com.omartech.review.english.dataprepare;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by omar on 16/1/25.
 */
public class CutIntoTokens {


    int cpu = 32;
    String filepath = "/tmp";

    public void doit() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(cpu, cpu, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(cpu * 2));

        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
            while ((line = br.readLine()) != null) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String str = "I lived in beijing. i want to eat an apple! what about you?";
        Worker w = new Worker(str, null);
        w.test();
    }
}

class Worker implements Runnable {

    String line;
    FileWriter fileWriter;

    public Worker(String line, FileWriter fileWriter) {
        this.line = line;
        this.fileWriter = fileWriter;
    }

    public void test(){
        List<Term> terms = ToAnalysis.parse(line);
        for (Term term : terms) {
            String name = term.getName();
            System.out.println(name);
        }
    }

    @Override
    public void run() {
        String[] strings = line.split("\t");

        if (strings.length == 8) {
            int hfd = Integer.parseInt(strings[3]);
            if (hfd > 1) {
                String content = strings[7];
                List<Term> terms = ToAnalysis.parse(content);
                for (Term term : terms) {
                    String name = term.getName();
                    System.out.println(name);
                }
            }
        }
    }
}
