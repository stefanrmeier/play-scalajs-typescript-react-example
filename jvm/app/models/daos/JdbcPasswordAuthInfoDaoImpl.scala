package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.concurrent.Future
import models.db.{ LoginInfo => DbLoginInfo, PasswordInfo => DbPasswordInfo }
import org.joda.time.DateTime
import scalikejdbc.sqls
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by stefanrmeier 2017/02/27.
 */
class JdbcPasswordAuthInfoDaoImpl extends DelegableAuthInfoDAO[PasswordInfo] {

  /**
   * Finds the auth info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
   */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    for {
      dbli <- DbLoginInfo.findBy(sqls.eq(DbLoginInfo.li.providerKey, loginInfo.providerKey).and.eq(DbLoginInfo.li.providerId, loginInfo.providerID))
      dbpi <- DbPasswordInfo.findBy(sqls.eq(DbPasswordInfo.pi.loginInfoId, dbli.id))
    } yield PasswordInfo(hasher = dbpi.hasher, password = dbpi.password, salt = dbpi.salt)
  }

  /**
   * Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo The auth info to add.
   * @return The added auth info.
   */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    for {
      dbli <- DbLoginInfo.findBy(sqls.eq(DbLoginInfo.li.providerKey, loginInfo.providerKey).and.eq(DbLoginInfo.li.providerId, loginInfo.providerID))
    } yield DbPasswordInfo.create(
      loginInfoId = dbli.id,
      hasher = authInfo.hasher, password = authInfo.password, salt = authInfo.salt,
      delFlg = false, insTm = DateTime.now(), updTm = DateTime.now()
    )
    authInfo
  }

  /**
   * Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo The auth info to update.
   * @return The updated auth info.
   */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    for {
      dbli <- DbLoginInfo.findBy(sqls.eq(DbLoginInfo.li.providerKey, loginInfo.providerKey).and.eq(DbLoginInfo.li.providerId, loginInfo.providerID))
      dbpi <- DbPasswordInfo.findBy(sqls.eq(DbPasswordInfo.pi.loginInfoId, dbli.id))
    } yield {
      DbPasswordInfo.save(
        dbpi.copy(hasher = authInfo.hasher, password = authInfo.password, salt = authInfo.salt)
      )
    }
    authInfo
  }

  /**
   * Saves the auth info for the given login info.
   *
   * This method either adds the auth info if it doesn't exists or it updates the auth info
   * if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo The auth info to save.
   * @return The saved auth info.
   */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, authInfo)
      case None => add(loginInfo, authInfo)
    }
  }

  /**
   * Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(loginInfo: LoginInfo): Future[Unit] = Future {
    for {
      dbli <- DbLoginInfo.findBy(sqls.eq(DbLoginInfo.li.providerKey, loginInfo.providerKey).and.eq(DbLoginInfo.li.providerId, loginInfo.providerID))
      dbpi <- DbPasswordInfo.findBy(sqls.eq(DbPasswordInfo.pi.loginInfoId, dbli.id))
    } yield {
      DbPasswordInfo.destroy(dbpi)
    }
  }
}
