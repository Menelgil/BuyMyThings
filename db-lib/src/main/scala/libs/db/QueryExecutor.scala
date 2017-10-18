package libs.db

import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.{ExecutionContext, Future}

case class QueryExecutor(dbConnection: DBConnection)(collectionName: String)(implicit ec: ExecutionContext) {
  private val promisedCollection = dbConnection(collectionName)

  def apply[T](query: BSONCollection => T): Future[T] = {
    promisedCollection.map(query)
  }

  def async[T](query: BSONCollection => Future[T]): Future[T] = {
    promisedCollection.flatMap(query)
  }
}
