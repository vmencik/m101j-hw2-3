package models

import com.mongodb.casbah.Imports._
import play.api.Play.current
import se.radley.plugin.salat._
import com.mongodb.DBObject
import java.security.MessageDigest
import sun.misc.BASE64Encoder
import java.security.SecureRandom
import com.mongodb.MongoException
import com.mongodb.casbah.commons.MongoDBObject

trait UserDAOComponent {

  def userDAO: UserDAO

  trait UserDAO {

    def addUser(username: String, password: String, email: String): Boolean

    def validateLogin(username: String, password: String): Option[DBObject]

  }

}

trait MongoUserDAOComponent extends UserDAOComponent {

  override val userDAO = new MongoUserDAO(mongoCollection("users"))

  class MongoUserDAO(users: MongoCollection) extends UserDAO {

    private val random = new SecureRandom()

    override def addUser(username: String, password: String, email: String): Boolean = {
      val passwordHash = makePasswordHash(password, Integer.toString(random.nextInt()))

      // XXX WORK HERE
      // create an object suitable for insertion into the user collection
      // be sure to add username and hashed password to the document. problem instructions
      // will tell you the schema that the documents must follow.

      if (email != "") {
        // XXX WORK HERE
        // if there is an email address specified, add it to the document too.
      }

      try {
        // XXX WORK HERE
        // insert the document into the user collection here
        true
      } catch {
        case e: MongoException.DuplicateKey =>
          false
      }
    }

    override def validateLogin(username: String, password: String): Option[DBObject] = {
      // XXX look in the user collection for a user that has this username
      // assign the result to the maybeUser value.

      val maybeUser: Option[DBObject] = ???

      maybeUser match {
        case Some(user) =>
          val hashedAndSalted = user.get("password").toString
          val salt = hashedAndSalted.split(",")(1)
          if (hashedAndSalted == makePasswordHash(password, salt)) maybeUser
          else None
        case _ => None
      }
    }

    private def makePasswordHash(password: String, salt: String) = {
      val saltedAndHashed = password + "," + salt
      val digest = MessageDigest.getInstance("MD5")
      digest.update(saltedAndHashed.getBytes())
      val encoder = new BASE64Encoder()
      val hashedBytes = (new String(digest.digest(), "UTF-8")).getBytes()
      encoder.encode(hashedBytes) + "," + salt
    }

  }

}