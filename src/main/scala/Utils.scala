import org.apache.spark.mllib.linalg.{Vectors, Vector}
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext._

/**
 * Created by yulinguo on 4/24/15.
 */
object Utils {

  def entropy(counts: Iterable[Int]) = {
    val values = counts.filter(_ > 0)
    val n: Double = values.sum
    values.map { v =>
      val p = v / n
      -p * math.log(p)
    }.sum
  }

  def norrmalizeLabelWithVector(labelsAndData: RDD[(String,Vector)]) = {

    val data = labelsAndData.values
    val dataAsArray = data.map(_.toArray)
    val numCols = dataAsArray.first().length
    val n = dataAsArray.count()

    val sums = dataAsArray.reduce(
      (a,b) => a.zip(b).map(t => t._1 + t._2)
    )

    val sumSquares = dataAsArray.fold(
      new Array[Double](numCols)     ￼
    )(
        (a,b) => a.zip(b).map(t => t._1 + t._2 * t._2)
    )


    val stdevs = sumSquares.zip(sums).map {
      case(sumSq,sum) => math.sqrt(n*sumSq - sum*sum)/n
    }

    val means = sums.map(_ / n)

    def normalize(datum: Vector) = {

      val normalizedArray = (datum.toArray, means, stdevs).zipped.map(
        (value, mean, stdev) =>
          if (stdev <= 0)
            (value - mean)
          else (value - mean) / stdev
      )
      Vectors.dense(normalizedArray)
    }

    val normalizedLabelsAndData = labelsAndData.map{
      case (lable, data) => (lable,normalize(data))
    }

    normalizedLabelsAndData
  }

}
