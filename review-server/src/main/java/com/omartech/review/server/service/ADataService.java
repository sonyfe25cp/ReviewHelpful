package com.omartech.review.server.service;

import com.omartech.review.gen.*;
import org.apache.thrift.TException;

/**
 * Created by omar on 14-12-6.
 */
public class ADataService extends AIndexSearcher implements DataService.Iface {

    @Override
    public TFIDFResponse tfidf(SentenceRequest req) throws TException {
        throw new TException();
    }

    @Override
    public TFIDFStatusResponse tfidfStatus(ServerStatusRequest req) throws TException {
        throw new TException();
    }

    @Override
    public SentenceResponse sendSentence(SentenceRequest req) throws TException {
        throw new TException();
    }

    @Override
    public ReviewFeatureResponse findFeatures(SentenceRequest req) throws TException {
        throw new TException();
    }

    @Override
    public ReviewFeatureResponse fetchWholeFeatures() throws TException {
        throw new TException();
    }

    @Override
    public ExtralTFResponse findExtralTF(ExtralTFRequest req) throws TException {
        throw new TException();
    }

    @Override
    public FeatureResponse extractFeature(FeatureRequest req) throws TException {
        throw new TException();
    }
}
