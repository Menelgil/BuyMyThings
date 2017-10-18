package controllers

import javax.inject._

import play.api.Logger._
import play.api.libs.json._
import play.api.mvc._
import services._

import scala.concurrent._

class AuthenticationApi @Inject()(cc: ControllerComponents, accountService: AccountServiceApi)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def authenticate(email: String, password: String) = Action.async { implicit request: Request[AnyContent] =>
    accountService.authenticate(email, password)
      .map {
        case true => Ok
        case false => NotFound
      }
      .recoverWith {
        case e: Throwable =>
          logger.error(s"Account authentication failed for $email", e)
          Future.successful(InternalServerError)
      }
  }

  def createAccount() = Action.async { implicit request: Request[AnyContent] =>
    implicit val requestReader = new AccountCreationRequestReader()

    request.body.asJson.fold({
      logger.error(s"Account creation failed: creation request does not have a json body")
      Future.successful[Result](BadRequest)
    })({ jsBody: JsValue =>
      jsBody.validate[AccountCreationRequest]
        .map { creationRequest =>
          accountService.createAccount(creationRequest.email, creationRequest.password)
            .map(_ => Ok) // account created
            .recoverWith {
              case _: InvalidEmailException =>
                logger.warn(s"Account creation failed for ${creationRequest.email}: invalid email")
                Future.successful(BadRequest("invalid.email"))

              case _: InvalidPasswordException =>
                logger.warn(s"Account creation failed for ${creationRequest.email}: invalid password")
                Future.successful(BadRequest("invalid.password"))

              case _: EmailAlreadyExistsException =>
                logger.info(s"Account creation failed for ${creationRequest.email}: email already exists")
                Future.successful(Conflict)

              case e: Throwable =>
                logger.error(s"Account creation failed for ${creationRequest.email}", e)
                Future.successful(InternalServerError)
            }
        }
        .recover { case _ => Future.successful(BadRequest) }
        .get
    })
  }
}

case class AccountCreationRequest(email: String, password: String)
class AccountCreationRequestReader extends Reads[AccountCreationRequest] {
  override def reads(json: JsValue): JsResult[AccountCreationRequest] = {
    (json \ "email").asOpt[String] match {
      case None => JsError("missing.email")
      case Some(email) => (json \ "password").asOpt[String] match {
        case None => JsError("missing.password")
        case Some(password) => JsSuccess(AccountCreationRequest(email, password))
      }
    }
  }
}
