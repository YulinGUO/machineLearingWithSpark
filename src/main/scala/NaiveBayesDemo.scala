import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.classification.{NaiveBayesModel, NaiveBayes}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

/**
 * Created by yulinguo on 3/26/15.
 */
object NaiveBayesDemo {

  def main(args: Array[String]) {

    val conf = new SparkConf()
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.storage.memoryFraction", "0.5")
    conf.set("spark.core.connection.ack.wait.timeout", "600")
    conf.set("spark.akka.frameSize", "50")

    val taskName = "naive bayes"
    val sc = new SparkContext(conf)

    //my raw data
    val rawdataPath = "your raw data path"

    // line -> user,featureCategoricalOne,fTwo,featureCategoricalThree,label

    val rawData = sc.textFile(rawdataPath)
      .map(line =>{
      val values = line.split("\t")
      val featureVector = Vectors.dense(values.slice(1,values.length-1).map(_ .toDouble))
      val label = values(values.length-1).toDouble
      LabeledPoint(label, featureVector)
    })

    val Array(trainData, testData) = rawData.randomSplit(Array(0.8, 0.2))

    val model = NaiveBayes.train(trainData, lambda = 1.0)
    val metrics = getMetrics(model, testData)

    //metrics
    println("model:::::"+model.toString())

    println("precision:0:::::::::::::::"+metrics.precision(0))
    println("recall pre:0::::::::::"+metrics.recall(0))

    println("precision:1:::::::::::::::"+metrics.precision(1))
    println("recall pre:1:::::::::::::::"+metrics.recall(1))

    println("confusionMatrix:::::::::::::::"+metrics.confusionMatrix)

    sc.stop()

  }

  def getMetrics(model: NaiveBayesModel, data: RDD[LabeledPoint]): MulticlassMetrics = {
    val predictionsAndLabels = data.map(example => (model.predict(example.features), example.label)
    )
    new MulticlassMetrics(predictionsAndLabels)
  }

}
