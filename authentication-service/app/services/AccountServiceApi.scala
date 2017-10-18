package services

import scala.concurrent.Future

trait AccountServiceApi {
  def createAccount(email: String, password: String): Future[Unit]
  def authenticate(email: String, password: String): Future[Boolean]
}
