name := "machineLearningWithSpark"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  ("org.apache.spark" %% "spark-core" % "1.0.2").
    exclude("org.mortbay.jetty", "servlet-api").
    exclude("commons-beanutils", "commons-beanutils-core").
    exclude("commons-collections", "commons-collections").
    exclude("com.esotericsoftware.minlog", "minlog").
    exclude("org.eclipse.jetty.orbit", "javax.activation").
    exclude("commons-logging", "commons-logging").
    exclude("org.apache.hadoop", "hadoop-yarn-common").
    exclude("org.eclipse.jetty.orbit", "javax.transaction").
    exclude("org.eclipse.jetty.orbit", "javax.mail.glassfish"),
  ("org.apache.hadoop" % "hadoop-client" % "2.2.0").
    exclude("commons-beanutils", "commons-beanutils-core").
    exclude("commons-collections", "commons-collections").
    exclude("commons-logging", "commons-logging").
    exclude("org.apache.hadoop", "hadoop-yarn-common").
    exclude("org.eclipse.jetty.orbit", "javax.transaction").
    exclude("org.eclipse.jetty.orbit", "javax.servlet").
    exclude("org.eclipse.jetty.orbit", "javax.mail.glassfish"),
  "org.scalaj" % "scalaj-http_2.10" % "0.3.16",
  "com.fasterxml.jackson" % "jackson-parent" % "2.5-rc1",
  "org.scalaj" % "scalaj-http_2.10" % "0.3.16",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "org.apache.spark" % "spark-mllib_2.10" % "1.1.0"
)

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/"
)
