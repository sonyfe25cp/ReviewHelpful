# -*- coding:utf-8 -*-  
import re
import numpy as np
import nltk
from nltk.stem.snowball import SnowballStemmer
from sklearn.cluster import KMeans
from sklearn import metrics
from scipy.spatial import distance

stemmer = SnowballStemmer("english")

# dunnscore的评估函数，输入点集的矩阵表示vectors、已经分好类的标记tag
# 输出评分值
def dunnscore(vectors, tag):
    ncluster = max(tag) + 1
    clusters = [[] for x in range(ncluster)]
    #将输入的稀疏矩阵转化为紧密矩阵
    vectors = vectors.todense()
    for i in range(0, vectors.shape[0]):
        clusters[tag[i]].append(vectors[i])
    #聚类中心
    cent = []
    for a in clusters:
        cent.append(np.mean(a, axis=0))
    #各个簇的直径
    diam = []

    for a in clusters:
        big = []
        for i in range(0, len(a)):
            for j in range(0, len(a)):
                big.append(distance.euclidean(a[i], a[j]))
        diam.append(max(big))

    cross = []
    for i in range(ncluster):
        for j in range(i + 1, ncluster):
            cross.append(distance.euclidean(cent[i], cent[j]))
    return min(cross) / max(diam)


#davies boulding的评分值
#输入、输出同上
def davies(vectors, tag):
    ncluster = max(tag) + 1
    clusters = [[] for x in range(ncluster)]
    vectors = vectors.todense()
    for i in range(vectors.shape[0]):
        clusters[tag[i]].append(vectors[i])
    cent = []

    for a in clusters:
        cent.append(np.mean(a, axis=0))

    diam = []
    for c in clusters:
        tp = []
        for node1 in c:
            for node2 in c:
                tp.append(distance.euclidean(node1, node2))
        diam.append(max(tp))

    for a in clusters:
        tp = []
        for node1 in c:
            for node2 in c:
                tp.append(distance.euclidean(node1, node2))
    cros = []

    res = 0
    for i in range(ncluster):
        tp = []
        for j in range(ncluster):
            if i == j:
                continue
            tp.append((diam[i] + diam[j]) / (distance.euclidean(cent[i], cent[j])))
        res += max(tp)
    return res / ncluster


#去掉标点符号
def tokenize_and_stem(text):
    tokens = [word for sent in nltk.sent_tokenize(text) for word in nltk.word_tokenize(sent)]
    filtered = []
    for token in tokens:
        if re.search('[a-zA-Z]', token):
            filtered.append(token)
    stems = []
    for t in filtered:
        tt = stemmer.stem(t)
        if tt not in stems:
            stems.append(tt)
    return stems

#存储文档
synopses = []

with open("mydata") as m:
    synopses = m.readlines()

from sklearn.feature_extraction.text import TfidfVectorizer

#tfidf化，maxdf表示出现频率大于0.98的词不用，mindf卡下界
#去停词
tfidf = TfidfVectorizer(max_df=0.98, max_features=200000,
                        min_df=0.06, stop_words='english',
                        use_idf=True, tokenizer=tokenize_and_stem, ngram_range=(1, 3))

tfidf_mat = tfidf.(synopses)

print tfidf_mat.shape

for i in range(2, 20):
    km = KMeans(i)
    #调用聚类函数聚类，得到一个打好标签的labels
    km.fit(tfidf_mat)
    tag = km.labels_.tolist()

    #silhouette评分是系统自带的

    print "dunnscore", dunnscore(tfidf_mat, tag), "davies", davies(tfidf_mat, tag), "silhouette", metrics.silhouette_score(
        tfidf_mat, km.labels_, metric="euclidean")
