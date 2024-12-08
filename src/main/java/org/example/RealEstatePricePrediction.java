package org.example;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.io.File;

public class RealEstatePricePrediction {

    public static void main(String[] args) throws Exception {
        // Load dataset to train the models
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File("src/main/resources/data.csv"));
        Instances data = loader.getDataSet();


        // File that will store the webscraping information
        CSVLoader loader2 = new CSVLoader();
        loader2.setSource(new File("src/main/resources/property_data.csv"));
        Instances data2 = loader2.getDataSet();

        // Delete the columns I don't need
        data.deleteAttributeAt(0);
        data.deleteAttributeAt(data.numAttributes() - 1);
        data.deleteAttributeAt(data.numAttributes() - 1);
        data.deleteAttributeAt(data.numAttributes() - 1);
        data.deleteAttributeAt(data.numAttributes() - 1);

        // Define price as the target variable
        data.setClassIndex(0);
        data2.setClassIndex(0);

        // Train Model
        Classifier model = new RandomForest();
        model.buildClassifier(data);


        Instance newInstance = data2.instance(0);


        double RF_prediction = model.classifyInstance(newInstance);


        System.out.println("Predicted Price with RandomForest Model : " + RF_prediction + "€");


        double realPrice = newInstance.classValue();  // get teh price
        System.out.println("Actual Price : " + realPrice + "€");





    }
}

