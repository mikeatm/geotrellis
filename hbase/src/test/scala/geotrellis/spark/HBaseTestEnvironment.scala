/*
 * Copyright (c) 2014 DigitalGlobe.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.spark

import geotrellis.spark.io.hbase._

import org.apache.spark.SparkConf
import org.apache.zookeeper.client.FourLetterWordMain
import org.scalatest._

trait HBaseTestEnvironment extends TestEnvironment { self: Suite =>
  override def setKryoRegistrator(conf: SparkConf) =
    conf.set("spark.kryo.registrator", "geotrellis.spark.io.kryo.KryoRegistrator")
      .set("spark.kryo.registrationRequired","false")

  override def beforeAll = {
    super.beforeAll
    try {
      // check zookeeper availability
      FourLetterWordMain.send4LetterWord("localhost", 2181, "srvr")
    } catch {
      case e: java.net.ConnectException => {
        println("\u001b[0;33mA script for setting up the HBase environment necessary to run these tests can be found at scripts/hbaseTestDB.sh - requires a working docker setup\u001b[m")
        cancel
      }
    }

    try {
      val instance = HBaseInstance(Seq("localhost"), "localhost")
      instance.getAdmin.tableExists("tiles")
      instance.getAdmin.close()
      instance.getAdmin.getConnection.close()
    } catch {
      case e: Exception => {
        println("\u001b[0;33mA script for setting up the HBase environment necessary to run these tests can be found at scripts/hbaseTestDB.sh - requires a working docker setup\u001b[m")
        cancel
      }
    }
  }

  beforeAll()
}
