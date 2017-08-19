package models.db

import scalikejdbc._
import org.joda.time.{ DateTime }

case class AuthToken(
  id: String,
  userUserId: String,
  expiry: DateTime,
  insTm: DateTime,
  updTm: DateTime) {

  def save()(implicit session: DBSession = AuthToken.autoSession): AuthToken = AuthToken.save(this)(session)

  def destroy()(implicit session: DBSession = AuthToken.autoSession): Int = AuthToken.destroy(this)(session)

}

object AuthToken extends SQLSyntaxSupport[AuthToken] {

  override val schemaName = Some("example")

  override val tableName = "AUTH_TOKEN"

  override val columns = Seq("id", "USER_user_id", "expiry", "ins_tm", "upd_tm")

  def apply(at: SyntaxProvider[AuthToken])(rs: WrappedResultSet): AuthToken = apply(at.resultName)(rs)
  def apply(at: ResultName[AuthToken])(rs: WrappedResultSet): AuthToken = new AuthToken(
    id = rs.get(at.id),
    userUserId = rs.get(at.userUserId),
    expiry = rs.get(at.expiry),
    insTm = rs.get(at.insTm),
    updTm = rs.get(at.updTm)
  )

  val at = AuthToken.syntax("at")

  override val autoSession = AutoSession

  def find(id: String)(implicit session: DBSession = autoSession): Option[AuthToken] = {
    withSQL {
      select.from(AuthToken as at).where.eq(at.id, id)
    }.map(AuthToken(at.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[AuthToken] = {
    withSQL(select.from(AuthToken as at)).map(AuthToken(at.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(AuthToken as at)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[AuthToken] = {
    withSQL {
      select.from(AuthToken as at).where.append(where)
    }.map(AuthToken(at.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[AuthToken] = {
    withSQL {
      select.from(AuthToken as at).where.append(where)
    }.map(AuthToken(at.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(AuthToken as at).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    id: String,
    userUserId: String,
    expiry: DateTime,
    insTm: DateTime,
    updTm: DateTime)(implicit session: DBSession = autoSession): AuthToken = {
    withSQL {
      insert.into(AuthToken).namedValues(
        column.id -> id,
        column.userUserId -> userUserId,
        column.expiry -> expiry,
        column.insTm -> insTm,
        column.updTm -> updTm
      )
    }.update.apply()

    AuthToken(
      id = id,
      userUserId = userUserId,
      expiry = expiry,
      insTm = insTm,
      updTm = updTm)
  }

  def batchInsert(entities: Seq[AuthToken])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'id -> entity.id,
        'userUserId -> entity.userUserId,
        'expiry -> entity.expiry,
        'insTm -> entity.insTm,
        'updTm -> entity.updTm))
    SQL("""insert into AUTH_TOKEN(
        id,
        USER_user_id,
        expiry,
        ins_tm,
        upd_tm
      ) values (
        {id},
        {userUserId},
        {expiry},
        {insTm},
        {updTm}
      )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: AuthToken)(implicit session: DBSession = autoSession): AuthToken = {
    withSQL {
      update(AuthToken).set(
        column.id -> entity.id,
        column.userUserId -> entity.userUserId,
        column.expiry -> entity.expiry,
        column.insTm -> entity.insTm,
        column.updTm -> entity.updTm
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: AuthToken)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(AuthToken).where.eq(column.id, entity.id) }.update.apply()
  }

}
