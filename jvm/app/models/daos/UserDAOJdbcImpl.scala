package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.{ Permission, Role, User }

import scala.concurrent.Future
import models.db.{ LoginInfo => DbLoginInfo, Permission => DbPermission, Role => DbRole, User => DbUser, UserHasLoginInfo => DbUserLoginInfo }
import org.joda.time.DateTime
import scalikejdbc.sqls

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Give access to the user object.
 */
class UserDAOJdbcImpl extends UserDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = Future {
    for {
      dbli <- DbLoginInfo.findBy(sqls.eq(DbLoginInfo.li.providerId, loginInfo.providerID).and.eq(DbLoginInfo.li.providerKey, loginInfo.providerKey))
      dbuli <- DbUserLoginInfo.findBy(sqls.eq(DbUserLoginInfo.uhli.loginInfoId, dbli.id))
      dbu <- DbUser.find(dbuli.userId)
      dbrs <- Some(DbRole.findAllBy(sqls.eq(DbRole.r.userId, dbu.id)))
      dbps <- Some(DbPermission.findAllBy(sqls.eq(DbPermission.p.userId, dbu.id)))
    } yield User(
      userID = UUID.fromString(dbu.id),
      loginInfo = LoginInfo(dbli.providerId, dbli.providerKey),
      firstName = None,
      lastName = None,
      fullName = dbu.username,
      email = dbu.email,
      avatarURL = None,
      activated = dbu.activated,
      roles = dbrs.map(dbr => Role(dbr.name)).collect { case Some(r: Role) => r },
      permissions = dbps.map(dbp => Permission(dbp.value)).collect { case Some(r: Permission) => r }
    )
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID) = Future {
    for {
      dbu <- DbUser.find(userID.toString)
      dbuli <- DbUserLoginInfo.findBy(sqls.eq(DbUserLoginInfo.uhli.userId, dbu.id))
      dbli <- DbLoginInfo.findBy(sqls.eq(DbLoginInfo.li.id, dbuli.loginInfoId))
      dbrs <- Some(DbRole.findAllBy(sqls.eq(DbRole.r.userId, dbu.id)))
      dbps <- Some(DbPermission.findAllBy(sqls.eq(DbPermission.p.userId, dbu.id)))
    } yield User(
      userID = userID,
      loginInfo = LoginInfo(dbli.providerId, dbli.providerKey),
      firstName = None,
      lastName = None,
      fullName = dbu.username,
      email = dbu.email,
      avatarURL = None,
      activated = dbu.activated,
      roles = dbrs.map(dbr => Role(dbr.name)).collect { case Some(r: Role) => r },
      permissions = dbps.map(dbp => Permission(dbp.value)).collect { case Some(r: Permission) => r }
    )
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = Future {
    DbUser.find(user.userID.toString).map(dbu => DbUser.save(
      dbu.copy(
        username = user.fullName,
        email = user.email,
        activated = user.activated,
        updTm = DateTime.now()
      )
    // TODO What should we do here about updating users login info? Probably unnecessary for now...
    // TODO also about roles and permissions
    )).getOrElse({
      val dbu = DbUser.create(
        id = user.userID.toString,
        username = user.fullName,
        email = user.email,
        activated = user.activated,
        testFlg = false, //TODO fix later
        delFlg = false,
        insTm = DateTime.now(),
        updTm = DateTime.now()
      )
      val dbli = DbLoginInfo.create(
        providerId = user.loginInfo.providerID,
        providerKey = user.loginInfo.providerKey,
        delFlg = false,
        insTm = DateTime.now(),
        updTm = DateTime.now()
      )
      val dbuli = DbUserLoginInfo.create(
        userId = dbu.id,
        loginInfoId = dbli.id
      )
      val dbrs = DbRole.batchInsert(user.roles.map(r => DbRole(id = -1L, userId = Option(user.userID.toString), roleType = r.roleType, name = r.name, delFlg = false, insTm = DateTime.now(), updTm = DateTime.now())))
      val dbps = DbPermission.batchInsert(user.permissions.map(p => DbPermission(id = -1L, permissionType = p.permissionType, value = p.value, delFlg = false, insTm = DateTime.now(), updTm = DateTime.now())))
      dbu
    })
    user
  }
}

