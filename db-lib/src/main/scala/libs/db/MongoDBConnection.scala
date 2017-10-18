package libs.db

import javax.inject._

import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent._
import scala.util._

@Singleton
class MongoDBConnection @Inject()(db: DBConfig)(implicit ec: ExecutionContext) extends DBConnection {
  protected val logger = LoggerFactory.getLogger(getClass)

  protected val driver = MongoDriver()
  protected val connection: Future[MongoConnection] = Future.fromTry(driver.connection(db.uri))

  protected val dbConnection: Future[DefaultDB] = connection.flatMap(_.database(db.name))

  dbConnection.onComplete {
    case Success(_) =>
      logger.info(s"Connection to MongoDB ${db.uri}/${db.name} SUCCEED")
    case Failure(e) =>
      logger.error(s"Connection to MongoDB ${db.uri}/${db.name} FAILED", e)
  }

  def apply(collectionName: String): Future[BSONCollection] = {
    dbConnection.map(_.collection(collectionName))
  }
}

object MongoDBConnection {
  val ConfigPath: String = "db"

  private val mongoUriPath = "uri"
  private val dbNamePath = "name"

  def loadConfig(dbConfig: Config): Try[DBConfig] = {
    Try(dbConfig.getString(mongoUriPath))
      .flatMap { uri =>
        Try(dbConfig.getString(dbNamePath))
          .map { name =>
            DBConfig(uri = uri, name = name)
          }
      }
  }
}