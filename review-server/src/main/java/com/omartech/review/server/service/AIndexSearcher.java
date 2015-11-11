package com.omartech.review.server.service;

import com.omartech.review.gen.DataService;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 14-12-24.
 */
public abstract class AIndexSearcher<T> extends AIndexService implements DataService.Iface {
    static Logger logger = LoggerFactory.getLogger(AIndexSearcher.class);

    @Option(name = "-port", usage = "the port")
    protected int port = 5678;

    @Option(name = "-ns", usage = "whether this is a search service, if not, no index is need")
    protected boolean notSearch = false;

    @Option(name = "-debug", usage = "set the debug info")
    protected boolean debug = false;

    private void bindAndListen() throws TTransportException {
        if (this.port < 0) {
            return;
        }
        DataService.Processor<AIndexSearcher<T>> processor = new DataService.Processor<>(this);
        TNonblockingServerSocket socket = new TNonblockingServerSocket(this.port);
        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(socket)
                .processor(processor).protocolFactory(new TBinaryProtocol.Factory());

        args.selectorThreads(1);
        args.workerThreads(this.cpu);

        TThreadedSelectorServer server = new TThreadedSelectorServer(args);
        logger.info("{} listen on {}", this.getClass().getSimpleName(), this.port);
        server.serve();
    }

    //用于被override
    //在所有事情最前面执行
    public void prepare() {

    }

    public void parseArgsAndRun(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(120);

        try {
            parser.parseArgument(args);
            if (this.help) {
                System.err.println("java {{cp}} " + this.getClass().getCanonicalName() + " [options...] arguments...");
                parser.printUsage(System.err);
                System.exit(1);
            } else {
                prepare();
                if (!notSearch) {
                    Directory dir = NIOFSDirectory.open(new File(getIndexPath()));
                    searcher = new IndexSearcher(DirectoryReader.open(dir));
                    reader = searcher.getIndexReader();
                    atomicReader = SlowCompositeReaderWrapper.wrap(reader);
                } else {
                    logger.info("this service is not based on search");
                }
                after();
                this.bindAndListen();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void after() {

    }


    protected IndexSearcher searcher;
    protected IndexReader reader;
    protected AtomicReader atomicReader;

    protected List<String> cutWords(String string) throws IOException {
        if (StringUtils.isEmpty(string)) {
            return null;
        } else {//todo: remove stopwords
            TokenStream tokenStream = analyzer.tokenStream("", string);
            List<String> strings = new ArrayList<>();
            while (tokenStream.incrementToken()) {
                CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
                String s = attribute.toString();
                if (s.length() > 1) {
                    strings.add(s);
                }
            }
            return strings;
        }
    }

}
