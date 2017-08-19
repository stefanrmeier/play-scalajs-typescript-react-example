package models.daos

import java.util.UUID

import models.AuthToken
import org.joda.time.DateTime

import models.db.{ AuthToken => DbAuthToken }
import scala.concurrent.Future
import scalikejdbc.sqls
import scala.concurrent.ExecutionContext.Implicits.global

class AuthTokenDAOJdbcImpl extends AuthTokenDAO {

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def find(id: UUID) = Future {
    for {
      dbat <- DbAuthToken.find(id.toString)
    } yield AuthToken(id, userID = UUID.fromString(dbat.userUserId), expiry = dbat.expiry)
  }

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: DateTime) = Future {
    DbAuthToken.findAllBy(sqls.le(DbAuthToken.at.expiry, dateTime))
      .map(dbat => AuthToken(id = UUID.fromString(dbat.id), userID = UUID.fromString(dbat.userUserId), expiry = dbat.expiry))
  }

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken) = Future {
    DbAuthToken.create(
      id = token.id.toString,
      userUserId = token.userID.toString,
      expiry = token.expiry,
      insTm = DateTime.now(),
      updTm = DateTime.now()
    )
    token
  }

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: UUID) = Future {
    DbAuthToken.find(id.toString).foreach(_.destroy())
  }
}

