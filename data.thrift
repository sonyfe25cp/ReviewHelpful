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


struct ReviewFeatureResponse{
  1: SentenceRequest req,
  2: list<string> words,
  3: list<string> adjs,
}



service DataService{

  SentenceResponse sendSentence(1: SentenceRequest req)//发送句子到server

  TFIDFResponse tfidf(1: SentenceRequest req)//获取句子的tfidf

  ReviewFeatureResponse findFeatures(1: SentenceRequest req)//发送句子到server

  ReviewFeatureResponse fetchWholeFeatures()//返回当前server中所有features

}


