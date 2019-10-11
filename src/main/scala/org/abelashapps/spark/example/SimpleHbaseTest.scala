package org.abelashapps.spark.example

import org.apache.hadoop.hbase.{HBaseConfiguration, MasterNotRunningException}
import org.apache.hadoop.hbase.client.HBaseAdmin

object SimpleHbaseTest {
  val config = HBaseConfiguration.create()
  var hbaseRunning = false;

  config.clear
  config.set("hbase.zookeeper.quorum", "172.18.0.2");  // This ip Address was retrieved by doing a docker inspect on the docker container
  config.set("hbase.zookeeper.property.clientPort","2181")
  config.set("hbase.master", "172.18.0.2:60000");
  config.setInt("timeout", 25000);

  try{
    println(" Check if HBase is Running")
    //HBaseAdmin.checkHBaseAvailable(config);   // This line throws the exception
    println(" HBase is Running")              // Because this line doesnt print.
    hbaseRunning = true
  }catch{
    case ex: MasterNotRunningException => {
      println(" MasterNotRunningException |" + ex.getMessage)  // This is the exception I get
    }
  }
  finally println(" HBase is NOT Running")

}
