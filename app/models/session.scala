package models
import java.security.SecureRandom

import com.mongodb.DBObject

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject

import play.api.Play.current
import se.radley.plugin.salat._
import sun.misc.BASE64Encoder

trait SessionDAOComponent {

  def sessionDAO: SessionDAO

  trait SessionDAO {

    def startSession(username: String): String

    def endSession(sessionId: String): Unit

    def getSession(sessionId: String): Option[DBObject]

    def findUserNameBySessionId(sessionId: String): Option[String]

  }

}

trait MongoSessionDAOComponent extends SessionDAOComponent {

  override val sessionDAO = new MongoSessionDAO(mongoCollection("sessions"))

  class MongoSessionDAO(sessions: MongoCollection) extends SessionDAO {

    override def startSession(username: String): String = {
      // get 32 byte random number. that's a lot of bits.
      val generator = new SecureRandom()
      val randomBytes = new Array[Byte](32)
      generator.nextBytes(randomBytes)

      val encoder = new BASE64Encoder()

      val sessionId = encoder.encode(randomBytes)

      // build the BSON object
      sessions += MongoDBObject("_id" -> sessionId, "username" -> username)
      sessionId
    }

    override def endSession(sessionId: String): Unit = sessions -= MongoDBObject("_id" -> sessionId)

    override def getSession(sessionId: String): Option[DBObject] = sessions.findOne(MongoDBObject("_id" -> sessionId))

    override def findUserNameBySessionId(sessionId: String): Option[String] = getSession(sessionId).map(_.get("username").toString)

  }

}