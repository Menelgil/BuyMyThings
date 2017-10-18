package libs.tests

import javax.inject.Inject

import libs.db.{DBConfig, MongoDBConnection}

import scala.concurrent.ExecutionContext

class TestMongoDBConnection @Inject()(ec: ExecutionContext) extends MongoDBConnection(DBConfig(uri = "mongodb://localhost:27017", name = "test"))(ec) {
  def teardown(): Unit = {
    dbConnection.flatMap(_.drop()(ec))(ec)
  }
}
