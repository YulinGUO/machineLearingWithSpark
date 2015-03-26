/**
 * Created by yulinguo on 3/12/15.
 */

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.regression._
import org.apache.spark.mllib.evaluation._
import org.apache.spark.mllib.tree._
import org.apache.spark.mllib.tree.model._
import org.apache.spark.mllib.tree.DecisionTree._

object DecisionTree {

  def main(args: Array[String]) {

    val conf = new SparkConf()
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.storage.memoryFraction", "0.5")
    conf.set("spark.core.connection.ack.wait.timeout", "600")
    conf.set("spark.akka.frameSize", "50")

    val taskName = "decision tree"
    val sc = new SparkContext(conf)

    val rawdataPath = "your-raw-data-path"

    //my raw data
    // line -> user,featureCategoricalOne,fTwo,featureCategoricalThree,label
    val rawData = sc.textFile(rawdataPath)
                    .map(line =>{
                      val values = line.split("\t")
                      val featureVector = Vectors.dense(values.slice(1,values.length-1).map(_ .toDouble))
                      val label = values(values.length-1).toDouble
                      LabeledPoint(label, featureVector)
                    })

    //train data for training set, test data for valuating the model
    val Array(trainData, testData) = rawData.randomSplit(Array(0.8, 0.2))
    trainData.cache()
    testData.cache()

    //tell DecisionTree the number of values of categorical feature
    val featureMap = Map(0 -> 2,2 ->2)
    
    val model = org.apache.spark.mllib.tree.DecisionTree.trainClassifier(trainData, 2, featureMap, "gini", 10, 100)
    val metrics = getMetrics(model, testData)


    //decision tree model
    println("model:::::"+model.toString())

    println("precision:0:::::::::::::::"+metrics.precision(0))
    println("recall pre:0::::::::::"+metrics.recall(0))

    println("precision:1:::::::::::::::"+metrics.precision(1))
    println("recall pre:1:::::::::::::::"+metrics.recall(1))

    println("confusionMatrix:::::::::::::::"+metrics.confusionMatrix)

    sc.stop()
 

    sc.stop()
  }

  def getMetrics(model: DecisionTreeModel, data: RDD[LabeledPoint]): MulticlassMetrics = {
    val predictionsAndLabels = data.map(example => (model.predict(example.features), example.label)
    )
    new MulticlassMetrics(predictionsAndLabels)
  }

}
