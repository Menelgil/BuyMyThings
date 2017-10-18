package libs.db

import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.Future


trait DBConnection {
  def apply(collectionName: String): Future[BSONCollection]
}
