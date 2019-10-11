package org.abelashapps.spark.example

import java.io.FileOutputStream
import java.nio.file.Path

import org.apache.spark.sql.{SQLContext, _}
import org.apache.spark.sql.execution.datasources.hbase._
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.conf.Configuration



/**
 * Hello world!
 *
 */
object HbaseTest extends App with ResourceAware {
  println( "Hello World!" )
  //println(resourceFromClasspath("hbase-site.xml"))
  //https://docs.microsoft.com/en-us/azure/hdinsight/hdinsight-using-spark-query-hbase
  //Or suggestion from https://github.com/nerdammer/spark-hbase-connector/blob/master/README.md
  // sparkContext.hadoopConfiguration.set("spark.hbase.host", "thehost")
  println(resourceFromClasspath("hbase-site.xml"))
 val spark = SparkSession
    .builder
    .appName("Spark Hbase Test")
    .config("spark.master", "local[8]")
    //.config("spark.driver.extraClassPath",resourceFromClasspath("hbase-site.xml"))
    //.config("spark.files","D:\\hbase-1.2.6\\conf\\hbase-site.xml"/*resourceFromClasspath("hbase-site.xml")*/)
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")
  //spark.sparkContext.hadoopConfiguration.set("spark.hbase.host", "localhost")
  //spark.sparkContext.hadoopConfiguration.set("spark.hbase.port", "16000")
  println("spark.files = " + spark.sparkContext.getConf.getOption("spark.files"))


 //println(resourceFromClasspath("hbase-site.xml"))
 //Spark Config
 //println("spark.driver.extraClassPat = " + spark.sparkContext.getConf.getOption("spark.driver.extraClassPath"))

 //spark.sparkContext.hadoopConfiguration.addResource(resourceFromClasspath("hbase-site.xml"))
 //val  hadoopConf  = spark.sparkContext.hadoopConfiguration
 //val  iterator = hadoopConf.iterator()
  //while (iterator.hasNext)   println(iterator.next())
 //val outXML  = new FileOutputStream("hconf.xml")
 //hadoopConf.writeXml(outXML)


  import spark.sqlContext.implicits._
  //define a catalog for the Contacts table you created in HBase:
  def catalog = s"""{
                   |"table":{"namespace":"default", "name":"Contacts"},
                   |"rowkey":"key",
                   |"columns":{
                   |"rowkey":{"cf":"rowkey", "col":"key", "type":"string"},
                   |"officeAddress":{"cf":"Office", "col":"Address", "type":"string"},
                   |"officePhone":{"cf":"Office", "col":"Phone", "type":"string"},
                   |"personalName":{"cf":"Personal", "col":"Name", "type":"string"},
                   |"personalPhone":{"cf":"Personal", "col":"Phone", "type":"string"}
                   |}
                   |}""".stripMargin

  //define a method that provides a DataFrame around your Contacts table in HBase:
  def withCatalog(cat: String): DataFrame = {
    spark.sqlContext
      .read
      .options(Map(HBaseTableCatalog.tableCatalog->cat))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .load()
   }

  val df = withCatalog(catalog)
  println("Count:")
  println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaa = "+df.count())
  df.show()
  df.createTempView("contacts")
  val query = spark.sqlContext.sql("select personalName, officeAddress from contacts")
  println("Query:")
  query.show()


  //Insert new data
  case class ContactRecord(
                            rowkey: String,
                            officeAddress: String,
                            officePhone: String,
                            personalName: String,
                            personalPhone: String
                          )

  val newContact = ContactRecord("16891", "40 Ellis St.", "674-555-0110", "John Jackson","230-555-0194")

  var newData = new Array[ContactRecord](1)
  newData(0) = newContact
  //Save the array of new data to HBase:
  println("Saving.....................")
  spark.sparkContext
    .parallelize(newData).toDF.write
    .options(Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "5"))
    .format("org.apache.spark.sql.execution.datasources.hbase").save()

  df.show()
}

