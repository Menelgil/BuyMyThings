package controllers

import javax.inject.Inject

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

class Authentication @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  //-------------------------------------
  //    SIGNUP
  //-------------------------------------

  private case class SignupData(email: String, password: String, passwordConfirmation: String)

  private val signupForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "confirm_password" -> nonEmptyText
    )(SignupData.apply)(SignupData.unapply)
  )

  def signupPage() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signup())
  }

  def createAccount() = Action { implicit request: Request[AnyContent] =>
    signupForm.bindFromRequest().fold(
      hasErrors = errorForm => {
        BadRequest(views.html.signup(Some("error happens")))
      },
      success = signupData => {

        Ok
      }
    )
  }

  //-------------------------------------
  //    LOGIN
  //-------------------------------------
}
