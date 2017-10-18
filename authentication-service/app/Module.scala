import libs.db.{DBConfig, DBConnection, MongoDBConnection}
import play.api.inject.Binding
import play.api.{Configuration, Environment, Logger}
import services.{AccountService, AccountServiceApi}

import scala.util.Try


class Module extends play.api.inject.Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    val dbConfig = Try(configuration.get[Configuration](MongoDBConnection.ConfigPath))
      .map(_.underlying)
      .flatMap(MongoDBConnection.loadConfig)
      .get // throws any exception during db configuration parsing

    Seq(
      bind[DBConfig].toInstance(dbConfig),
      bind[DBConnection].to[MongoDBConnection],
      bind[AccountServiceApi].to[AccountService]
    )
  }
}