import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.recommendation._
import org.apache.spark.SparkContext._

/**
 * Created by yulinguo on 4/7/15.
 */
object ALSRecom {

  def main(args: Array[String]) {

    val conf = new SparkConf()
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.storage.memoryFraction", "0.5")
    conf.set("spark.core.connection.ack.wait.timeout", "600")
    conf.set("spark.akka.frameSize", "50")

    val taskName = "alternative least square recommend algo"
    val sc = new SparkContext(conf)

    val rawUserArtistPath = "/tmp/yulin/mldata/user_artist_data.txt"
    val rawArtistPath = "/tmp/yulin/mldata/artist_data.txt"
    val rawArtistAliasPath = "/tmp/yulin/mldata/artist_alias.txt"

    val rawUserArtistData = sc.textFile(rawUserArtistPath)
    val rawArtistData = sc.textFile(rawArtistPath)

    val artistByID = rawArtistData.flatMap {line =>
      val (id, name) = line.span(_ != '\t')
      if (name.isEmpty) {
        None
      }else{
        try {
          Some((id.toInt, name.trim))
        } catch {
          case e: NumberFormatException => None }
      }
    }

    val rawArtistAlias = sc.textFile(rawArtistAliasPath)

    val artistAlias = rawArtistAlias.flatMap { line =>
      val tokens = line.split('\t')
      if (tokens(0).isEmpty) {
        None
      }else{
        Some((tokens(0).toInt, tokens(1).toInt))
      }
    }.collectAsMap()

    val bArtistAlias = sc.broadcast(artistAlias)

    val trainData = rawUserArtistData.map { line =>
      val Array(userID, artistID, count) = line.split(' ').map(_.toInt)
      val finalArtistID =bArtistAlias.value.getOrElse(artistID, artistID)
      Rating(userID, finalArtistID, count)
    }.cache()

    val model = ALS.trainImplicit(trainData, 10, 5, 0.01, 1.0)

    model.userFeatures.mapValues(_.mkString(", ")).first()


    //result for 2093760
    val recommendations = model.recommendProducts(2093760, 5)
    recommendations.foreach(println)

    val recommendedProductIDs = recommendations.map(_.product).toSet
    artistByID.filter {
      case (id, name) => recommendedProductIDs.contains(id)
    }.values.collect().foreach(println)

    sc.stop()
  }

}
