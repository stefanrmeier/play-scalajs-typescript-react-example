package models.db

import scalikejdbc._
import org.joda.time.{ DateTime }

case class LoginInfo(
  id: Long,
  providerId: String,
  providerKey: String,
  delFlg: Boolean,
  insTm: DateTime,
  updTm: DateTime) {

  def save()(implicit session: DBSession = LoginInfo.autoSession): LoginInfo = LoginInfo.save(this)(session)

  def destroy()(implicit session: DBSession = LoginInfo.autoSession): Int = LoginInfo.destroy(this)(session)

}

object LoginInfo extends SQLSyntaxSupport[LoginInfo] {

  override val schemaName = Some("example")

  override val tableName = "LOGIN_INFO"

  override val columns = Seq("id", "provider_id", "provider_key", "del_flg", "ins_tm", "upd_tm")

  def apply(li: SyntaxProvider[LoginInfo])(rs: WrappedResultSet): LoginInfo = apply(li.resultName)(rs)
  def apply(li: ResultName[LoginInfo])(rs: WrappedResultSet): LoginInfo = new LoginInfo(
    id = rs.get(li.id),
    providerId = rs.get(li.providerId),
    providerKey = rs.get(li.providerKey),
    delFlg = rs.get(li.delFlg),
    insTm = rs.get(li.insTm),
    updTm = rs.get(li.updTm)
  )

  val li = LoginInfo.syntax("li")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[LoginInfo] = {
    withSQL {
      select.from(LoginInfo as li).where.eq(li.id, id)
    }.map(LoginInfo(li.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[LoginInfo] = {
    withSQL(select.from(LoginInfo as li)).map(LoginInfo(li.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(LoginInfo as li)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[LoginInfo] = {
    withSQL {
      select.from(LoginInfo as li).where.append(where)
    }.map(LoginInfo(li.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[LoginInfo] = {
    withSQL {
      select.from(LoginInfo as li).where.append(where)
    }.map(LoginInfo(li.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(LoginInfo as li).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    providerId: String,
    providerKey: String,
    delFlg: Boolean,
    insTm: DateTime,
    updTm: DateTime)(implicit session: DBSession = autoSession): LoginInfo = {
    val generatedKey = withSQL {
      insert.into(LoginInfo).namedValues(
        column.providerId -> providerId,
        column.providerKey -> providerKey,
        column.delFlg -> delFlg,
        column.insTm -> insTm,
        column.updTm -> updTm
      )
    }.updateAndReturnGeneratedKey.apply()

    LoginInfo(
      id = generatedKey,
      providerId = providerId,
      providerKey = providerKey,
      delFlg = delFlg,
      insTm = insTm,
      updTm = updTm)
  }

  def batchInsert(entities: Seq[LoginInfo])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'providerId -> entity.providerId,
        'providerKey -> entity.providerKey,
        'delFlg -> entity.delFlg,
        'insTm -> entity.insTm,
        'updTm -> entity.updTm))
    SQL("""insert into LOGIN_INFO(
        provider_id,
        provider_key,
        del_flg,
        ins_tm,
        upd_tm
      ) values (
        {providerId},
        {providerKey},
        {delFlg},
        {insTm},
        {updTm}
      )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: LoginInfo)(implicit session: DBSession = autoSession): LoginInfo = {
    withSQL {
      update(LoginInfo).set(
        column.id -> entity.id,
        column.providerId -> entity.providerId,
        column.providerKey -> entity.providerKey,
        column.delFlg -> entity.delFlg,
        column.insTm -> entity.insTm,
        column.updTm -> entity.updTm
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: LoginInfo)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(LoginInfo).where.eq(column.id, entity.id) }.update.apply()
  }

}
