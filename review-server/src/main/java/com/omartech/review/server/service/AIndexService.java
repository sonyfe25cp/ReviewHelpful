package com.omartech.review.server.service;

import org.ansj.lucene4.AnsjAnalysis;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.lucene.analysis.Analyzer;
import org.kohsuke.args4j.Option;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by omar on 15-1-5.
 */
public class AIndexService {
    @Option(name = "-cpu", usage = "-cpu the num of cpu")
    protected int cpu = Runtime.getRuntime().availableProcessors();

    @Option(name = "-p", usage = "-p index path")
    protected String indexPath;

    @Option(name = "-help", usage = "show the help")
    protected boolean help = false;

    protected Analyzer analyzer = new AnsjAnalysis();

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String APPNAME = "appName";
    public static final String PUBLISHDATE = "publishDate";
    public static final String URL = "url";
    public static final String SUBTYPE = "subtype";
    public static final String HOT = "hot";
    public static final String SENTITIVEWORDS="sentitiveWords";
    public static final String SOURCE="source";
    public static final String EMOTION="emotion";
    public static final String LOCATION="location";

    public static final String NEWS = "news";
    public static final String WEIXIN = "weixin";
    public static final String WEIBO = "weibo";
    public static final String WEIXINNUM = "weixinnum";
    public static final String WEIXINNAME = "weixinname";
    public static final String ISOFFICAL = "isoffical";
    public static final String ISLIVE = "islive";
    public static final String OPENID = "openid";


    public static String formatTime(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }

    public static Date parseTime(String time) {
        try {
            return DateUtils.parseDate(time, "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }
}
