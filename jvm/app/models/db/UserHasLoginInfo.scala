package models.db

import scalikejdbc._

case class UserHasLoginInfo(
  userId: String,
  loginInfoId: Long) {

  def save()(implicit session: DBSession = UserHasLoginInfo.autoSession): UserHasLoginInfo = UserHasLoginInfo.save(this)(session)

  def destroy()(implicit session: DBSession = UserHasLoginInfo.autoSession): Int = UserHasLoginInfo.destroy(this)(session)

}

object UserHasLoginInfo extends SQLSyntaxSupport[UserHasLoginInfo] {

  override val schemaName = Some("example")

  override val tableName = "USER_has_LOGIN_INFO"

  override val columns = Seq("USER_id", "LOGIN_INFO_id")

  def apply(uhli: SyntaxProvider[UserHasLoginInfo])(rs: WrappedResultSet): UserHasLoginInfo = apply(uhli.resultName)(rs)
  def apply(uhli: ResultName[UserHasLoginInfo])(rs: WrappedResultSet): UserHasLoginInfo = new UserHasLoginInfo(
    userId = rs.get(uhli.userId),
    loginInfoId = rs.get(uhli.loginInfoId)
  )

  val uhli = UserHasLoginInfo.syntax("uhli")

  override val autoSession = AutoSession

  def find(loginInfoId: Long, userId: String)(implicit session: DBSession = autoSession): Option[UserHasLoginInfo] = {
    withSQL {
      select.from(UserHasLoginInfo as uhli).where.eq(uhli.loginInfoId, loginInfoId).and.eq(uhli.userId, userId)
    }.map(UserHasLoginInfo(uhli.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[UserHasLoginInfo] = {
    withSQL(select.from(UserHasLoginInfo as uhli)).map(UserHasLoginInfo(uhli.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(UserHasLoginInfo as uhli)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[UserHasLoginInfo] = {
    withSQL {
      select.from(UserHasLoginInfo as uhli).where.append(where)
    }.map(UserHasLoginInfo(uhli.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[UserHasLoginInfo] = {
    withSQL {
      select.from(UserHasLoginInfo as uhli).where.append(where)
    }.map(UserHasLoginInfo(uhli.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(UserHasLoginInfo as uhli).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    userId: String,
    loginInfoId: Long)(implicit session: DBSession = autoSession): UserHasLoginInfo = {
    withSQL {
      insert.into(UserHasLoginInfo).namedValues(
        column.userId -> userId,
        column.loginInfoId -> loginInfoId
      )
    }.update.apply()

    UserHasLoginInfo(
      userId = userId,
      loginInfoId = loginInfoId)
  }

  def batchInsert(entities: Seq[UserHasLoginInfo])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'userId -> entity.userId,
        'loginInfoId -> entity.loginInfoId))
    SQL("""insert into USER_has_LOGIN_INFO(
        USER_id,
        LOGIN_INFO_id
      ) values (
        {userId},
        {loginInfoId}
      )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: UserHasLoginInfo)(implicit session: DBSession = autoSession): UserHasLoginInfo = {
    withSQL {
      update(UserHasLoginInfo).set(
        column.userId -> entity.userId,
        column.loginInfoId -> entity.loginInfoId
      ).where.eq(column.loginInfoId, entity.loginInfoId).and.eq(column.userId, entity.userId)
    }.update.apply()
    entity
  }

  def destroy(entity: UserHasLoginInfo)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(UserHasLoginInfo).where.eq(column.loginInfoId, entity.loginInfoId).and.eq(column.userId, entity.userId) }.update.apply()
  }

}
