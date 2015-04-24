import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.clustering._
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._
import scala.util.Try

/**
 * Created by yulinguo on 4/23/15.
 */
object KMeasClustering {
  def main(args: Array[String]) {

    val conf = new SparkConf()
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.storage.memoryFraction", "0.5")
    conf.set("spark.core.connection.ack.wait.timeout", "600")
    conf.set("spark.akka.frameSize", "50")

    val taskName = "K-means clustering algo"
    val sc = new SparkContext(conf)

    val rawDataPath = "/tmp/yulin/mldata/kddcup.data.txt"
    val rawData = sc.textFile(rawDataPath)

    val labelsAndData = rawData.flatMap { line =>
      val buffer = line.split(',').toBuffer
      if (buffer.length == 42) {
        buffer.remove(1, 3)
        val label = buffer.remove(buffer.length - 1)
        val vector = Vectors.dense(buffer.map(_.toDouble).toArray)
        Some(label, vector)
      } else {
        None
      }
    }

    val data = labelsAndData.values.cache()
    evaluateByDistance(data)

    sc.stop()
  }

  def evaluateByDistance(data: RDD[Vector])={

    def distance(a: Vector, b: Vector) = {
      math.sqrt(a.toArray.zip(b.toArray).map(p => p._1 - p._2).map(p => p*p).sum)
    }

    def distToCentroid(datum: Vector, model: KMeansModel) = {
      val cluster = model.predict(datum)
      val centroid = model.clusterCenters(cluster)
      distance(centroid, datum)
    }

    def clusteringScore(data: RDD[Vector], k: Int) = {
      val kmeans = new KMeans()
      kmeans.setK(k)
      kmeans.setRuns(10)
      kmeans.setEpsilon(1.0e-6)
      val model = kmeans.run(data)
      data.map(datum => distToCentroid(datum, model)).mean()
    }


    (30 to 150 by 10 ).map(k => clusteringScore(data,k)).foreach(println)

  }

}
