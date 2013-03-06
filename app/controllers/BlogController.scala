package controllers

import play.api._
import play.api.mvc._
import models.UserDAOComponent
import models.SessionDAOComponent
import models.MongoUserDAOComponent
import models.MongoSessionDAOComponent

import play.api.data._
import play.api.data.Forms._

trait BlogControllerApi extends Controller {
  self: UserDAOComponent with SessionDAOComponent =>

  def homePage = Action {
    Ok(views.html.blog())
  }

  def showSignup = Action {
    Ok(views.html.signup())
  }

  def processSignup = Action { implicit request =>
    signupForm.bindFromRequest.fold(
      withErrors => BadRequest(views.html.signup()),
      signup => {
        val validated = signup.validated
        if (validated.errors.isEmpty) {
          // good user
          if (!userDAO.addUser(signup.username, signup.password, signup.email))
            // duplicate user
            Ok(views.html.signup(validated.copy(errors = SignupErrors(username = "Username already in use, Please choose another"))))
          else {
            // good user, let's start a session
            val sessionId = sessionDAO.startSession(signup.username)
            Redirect(routes.BlogController.welcome).withCookies(
              Cookie("session", sessionId))
          }
        } else Ok(views.html.signup(validated)) // invalid signup
      })
  }

  def welcome = Action { implicit request =>
    val maybeUsername = for {
      sessionCookie <- request.cookies.get("session")
      username <- sessionDAO.findUserNameBySessionId(sessionCookie.value)
    } yield username

    maybeUsername match {
      case Some(username) => Ok(views.html.welcome(username))
      case _ => Redirect(routes.BlogController.showSignup)
    }
  }

  def showLogin = Action {
    Ok(views.html.login())
  }

  def processLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      withErrors => BadRequest(views.html.login()),
      {
        case (username, password) => userDAO.validateLogin(username, password) match {
          case Some(user) =>
            // valid user, let's log them in
            val sessionId = sessionDAO.startSession(user.get("_id").toString)
            Redirect(routes.BlogController.welcome).withCookies(
              Cookie("session", sessionId))
          case _ => Ok(views.html.login(username, "Invalid Login"))
        }
      })
  }

  def logout = Action { implicit request =>
    request.cookies.get("session") match {
      case Some(sessionCookie) =>
        sessionDAO.endSession(sessionCookie.value)
        Redirect(routes.BlogController.showLogin).discardingCookies(DiscardingCookie("session"))
      case _ => Redirect(routes.BlogController.showLogin)
    }
  }

  val signupForm = Form(
    mapping(
      "email" -> text,
      "username" -> text,
      "password" -> text,
      "verify" -> text) {
        case (email, username, password, verify) =>
          Signup(email, username, password, verify)
      } {
        s => Some((s.email, s.username, s.password, s.verify))
      })

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text))

}

object BlogController extends BlogControllerApi with MongoUserDAOComponent with MongoSessionDAOComponent

case class Signup(email: String = "", username: String = "", password: String = "", verify: String = "", errors: SignupErrors = SignupErrors()) {

  def validated: Signup = {
    import Signup._
    if ((UserRe findFirstIn username).isEmpty) copy(errors = SignupErrors(username = "invalid username. try just letters and numbers"))
    else if ((PassRe findFirstIn password).isEmpty) copy(errors = SignupErrors(password = "invalid password."))
    else if (password != verify) copy(errors = SignupErrors(verify = "password must match"))
    else if (email != "" && (EmailRe findFirstIn email).isEmpty) copy(errors = SignupErrors(email = "Invalid Email Address"))
    else this
  }

}

object Signup {
  val UserRe = "^[a-zA-Z0-9_-]{3,20}$".r
  val PassRe = "^.{3,20}$".r
  val EmailRe = "^[\\S]+@[\\S]+\\.[\\S]+$".r
}

case class SignupErrors(password: String = "", username: String = "", email: String = "", verify: String = "") {

  def isEmpty = (password + username + email + verify).isEmpty

}

