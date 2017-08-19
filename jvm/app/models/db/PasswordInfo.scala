package models.db

import scalikejdbc._
import org.joda.time.{ DateTime }

case class PasswordInfo(
  id: Long,
  loginInfoId: Long,
  hasher: String,
  password: String,
  salt: Option[String] = None,
  delFlg: Boolean,
  insTm: DateTime,
  updTm: DateTime) {

  def save()(implicit session: DBSession = PasswordInfo.autoSession): PasswordInfo = PasswordInfo.save(this)(session)

  def destroy()(implicit session: DBSession = PasswordInfo.autoSession): Int = PasswordInfo.destroy(this)(session)

}

object PasswordInfo extends SQLSyntaxSupport[PasswordInfo] {

  override val schemaName = Some("example")

  override val tableName = "PASSWORD_INFO"

  override val columns = Seq("id", "LOGIN_INFO_id", "hasher", "password", "salt", "del_flg", "ins_tm", "upd_tm")

  def apply(pi: SyntaxProvider[PasswordInfo])(rs: WrappedResultSet): PasswordInfo = apply(pi.resultName)(rs)
  def apply(pi: ResultName[PasswordInfo])(rs: WrappedResultSet): PasswordInfo = new PasswordInfo(
    id = rs.get(pi.id),
    loginInfoId = rs.get(pi.loginInfoId),
    hasher = rs.get(pi.hasher),
    password = rs.get(pi.password),
    salt = rs.get(pi.salt),
    delFlg = rs.get(pi.delFlg),
    insTm = rs.get(pi.insTm),
    updTm = rs.get(pi.updTm)
  )

  val pi = PasswordInfo.syntax("pi")

  override val autoSession = AutoSession

  def find(loginInfoId: Long, id: Long)(implicit session: DBSession = autoSession): Option[PasswordInfo] = {
    withSQL {
      select.from(PasswordInfo as pi).where.eq(pi.loginInfoId, loginInfoId).and.eq(pi.id, id)
    }.map(PasswordInfo(pi.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[PasswordInfo] = {
    withSQL(select.from(PasswordInfo as pi)).map(PasswordInfo(pi.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(PasswordInfo as pi)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[PasswordInfo] = {
    withSQL {
      select.from(PasswordInfo as pi).where.append(where)
    }.map(PasswordInfo(pi.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[PasswordInfo] = {
    withSQL {
      select.from(PasswordInfo as pi).where.append(where)
    }.map(PasswordInfo(pi.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(PasswordInfo as pi).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    loginInfoId: Long,
    hasher: String,
    password: String,
    salt: Option[String] = None,
    delFlg: Boolean,
    insTm: DateTime,
    updTm: DateTime)(implicit session: DBSession = autoSession): PasswordInfo = {
    val generatedKey = withSQL {
      insert.into(PasswordInfo).namedValues(
        column.loginInfoId -> loginInfoId,
        column.hasher -> hasher,
        column.password -> password,
        column.salt -> salt,
        column.delFlg -> delFlg,
        column.insTm -> insTm,
        column.updTm -> updTm
      )
    }.updateAndReturnGeneratedKey.apply()

    PasswordInfo(
      id = generatedKey,
      loginInfoId = loginInfoId,
      hasher = hasher,
      password = password,
      salt = salt,
      delFlg = delFlg,
      insTm = insTm,
      updTm = updTm)
  }

  def batchInsert(entities: Seq[PasswordInfo])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'loginInfoId -> entity.loginInfoId,
        'hasher -> entity.hasher,
        'password -> entity.password,
        'salt -> entity.salt,
        'delFlg -> entity.delFlg,
        'insTm -> entity.insTm,
        'updTm -> entity.updTm))
    SQL("""insert into PASSWORD_INFO(
        LOGIN_INFO_id,
        hasher,
        password,
        salt,
        del_flg,
        ins_tm,
        upd_tm
      ) values (
        {loginInfoId},
        {hasher},
        {password},
        {salt},
        {delFlg},
        {insTm},
        {updTm}
      )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: PasswordInfo)(implicit session: DBSession = autoSession): PasswordInfo = {
    withSQL {
      update(PasswordInfo).set(
        column.id -> entity.id,
        column.loginInfoId -> entity.loginInfoId,
        column.hasher -> entity.hasher,
        column.password -> entity.password,
        column.salt -> entity.salt,
        column.delFlg -> entity.delFlg,
        column.insTm -> entity.insTm,
        column.updTm -> entity.updTm
      ).where.eq(column.loginInfoId, entity.loginInfoId).and.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: PasswordInfo)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(PasswordInfo).where.eq(column.loginInfoId, entity.loginInfoId).and.eq(column.id, entity.id) }.update.apply()
  }

}
