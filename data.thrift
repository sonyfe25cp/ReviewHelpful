namespace java com.omartech.review.gen

// thrift -gen python data.thrift
// thrift -gen java data.thrift

struct SentenceRequest{
  1: string sentence,
  2: list<string> sentences,
  3: bool over,//是否结束发送文件
  4: bool clear,//是否全部清空
}
struct SentenceResponse{
  1: SentenceRequest req,
}

struct TFIDFResponse{
  1: SentenceRequest req,
  2: map<string, double> stringMap,//句子对应的tfidf
  3: map<i32, double> positionMap,//句子对应的tfidf
  4: i32 lexiconSize,//总词数
}

struct TFIDFStatusResponse{
  1: i32 totalWords,
  2: map<i32, string> wordsPositionMap,//词序map
}

struct ServerStatusRequest{
  1: string ip,
}


struct ReviewFeatureResponse{
  1: SentenceRequest req,
  2: list<string> words,
  3: list<string> adjs,
}

struct ExtralTFRequest{
  1: string word
}

struct ExtralTFResponse{
  1: i32 count
}

struct Review{
  1: string title,
  2: string body,
}

struct FeatureRequest{
  1: Review review,
}

struct FeatureResponse{
  1: FeatureRequest req,
  2: i32 length,
  3: list<double> vector,
}


service DataService{

  SentenceResponse sendSentence(1: SentenceRequest req)//发送句子到server

  TFIDFResponse tfidf(1: SentenceRequest req)//获取句子的tfidf

  TFIDFStatusResponse tfidfStatus(1: ServerStatusRequest req)//查询server的状态

  ReviewFeatureResponse findFeatures(1: SentenceRequest req)//发送句子到server

  ReviewFeatureResponse fetchWholeFeatures()//返回当前server中所有features

  ExtralTFResponse findExtralTF(1: ExtralTFRequest req)//返回外部词典中的tf

  FeatureResponse extractFeature(1: FeatureRequest req)//返回特征

}


