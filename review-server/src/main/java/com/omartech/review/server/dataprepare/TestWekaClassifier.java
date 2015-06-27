package com.omartech.review.server.dataprepare;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.Random;

/**
 * Created by OmarTech on 15-6-17.
 */
public class TestWekaClassifier {
    public static void main(String[] args) throws Exception {
        Instances instances = ConverterUtils.DataSource.read("/Volumes/weka-3-6-12/weka-3-6-12/data/breast-cancer.arff");

//        for (int i = 0; i < instances.numInstances(); i++) {
//            Instance instance = instances.instance(i);
//            for (int j = 0; j < instance.numAttributes(); j++) {
//                System.out.println(instance.attribute(j));
//                double value = instance.value(instance.attribute(j));
//                System.out.println(value);
//            }
//            break;
//        }
//        int i = instances.numAttributes();
//        System.out.println(i);
//        instances.setClassIndex(i - 1);
//        String[] options = new String[1];
//        options[0] = "-U";
//
//
//        J48 tree = new J48();
//        tree.setOptions(options);
//        tree.buildClassifier(instances);
//        Evaluation eval = new Evaluation(instances);
//        eval.crossValidateModel(tree, instances, 10, new Random(1));
//        System.out.println(eval.toSummaryString("\nResults\n\n", false));
//        double v = eval.errorRate();
//        System.out.println("erroRate:" + v);

        String[] options = new String[2];
        options[0] = "-I"; // max. iterations
        options[1] = "100";

        EM cl = new EM();
        cl.buildClusterer(instances);
        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(cl);
        eval.evaluateClusterer(new Instances(instances));
        System.out.println(eval.clusterResultsToString());


    }
}
