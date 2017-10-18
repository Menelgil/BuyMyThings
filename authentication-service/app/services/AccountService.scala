package services

import javax.inject.Inject

import libs.db._
import reactivemongo.bson._

import scala.concurrent._

class AccountService @Inject() protected(dbConnection: DBConnection)(implicit ec: ExecutionContext) extends AccountServiceApi {
  private object ACCOUNT {
    val EMAIL = "email"
    val PASSWORD = "password"
  }

  private val runQuery = QueryExecutor(dbConnection)("accounts")

  private def isEmailValid(email: String): Boolean = {
    email.nonEmpty
  }

  private def isPasswordValid(password: String): Boolean = {
    password.nonEmpty
  }

  private def accountExists(email: String): Future[Boolean] = {
    runQuery
      .async(_.find(BSONDocument(ACCOUNT.EMAIL -> email)).one)
      .map(_.nonEmpty)
  }

  private def assertEmailDoNotExists(email: String): Future[Unit] = {
    accountExists(email).flatMap {
      case true =>
        Future.failed(EmailAlreadyExistsException(email))
      case _ =>
        Future.unit
    }
  }

  def createAccount(email: String, password: String): Future[Unit] = {
    if (!isEmailValid(email)) {
      Future.failed(InvalidEmailException(email))
    } else if (!isPasswordValid(password)) {
      Future.failed(InvalidPasswordException())
    } else {
      assertEmailDoNotExists(email).flatMap { _ =>
        runQuery
          .async { collection =>
            collection.insert(document(
              ACCOUNT.EMAIL -> email,
              ACCOUNT.PASSWORD -> password
            ))
          }
      }.map(_ => {})
    }
  }

  def authenticate(email: String, password: String): Future[Boolean] = {
    runQuery
      .async(_.find(BSONDocument(ACCOUNT.EMAIL -> email, ACCOUNT.PASSWORD -> password)).one)
      .map(_.nonEmpty)
  }

}
