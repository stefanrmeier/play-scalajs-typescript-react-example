package models.db

import scalikejdbc._
import org.joda.time.{ DateTime }

case class Permission(
  id: Long,
  permissionType: String,
  userId: Option[String] = None,
  value: String,
  delFlg: Boolean,
  insTm: DateTime,
  updTm: DateTime) {

  def save()(implicit session: DBSession = Permission.autoSession): Permission = Permission.save(this)(session)

  def destroy()(implicit session: DBSession = Permission.autoSession): Int = Permission.destroy(this)(session)

}

object Permission extends SQLSyntaxSupport[Permission] {

  override val schemaName = Some("example")

  override val tableName = "PERMISSION"

  override val columns = Seq("id", "permission_type", "USER_id", "value", "del_flg", "ins_tm", "upd_tm")

  def apply(p: SyntaxProvider[Permission])(rs: WrappedResultSet): Permission = apply(p.resultName)(rs)
  def apply(p: ResultName[Permission])(rs: WrappedResultSet): Permission = new Permission(
    id = rs.get(p.id),
    permissionType = rs.get(p.permissionType),
    userId = rs.get(p.userId),
    value = rs.get(p.value),
    delFlg = rs.get(p.delFlg),
    insTm = rs.get(p.insTm),
    updTm = rs.get(p.updTm)
  )

  val p = Permission.syntax("p")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Permission] = {
    withSQL {
      select.from(Permission as p).where.eq(p.id, id)
    }.map(Permission(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Permission] = {
    withSQL(select.from(Permission as p)).map(Permission(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Permission as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Permission] = {
    withSQL {
      select.from(Permission as p).where.append(where)
    }.map(Permission(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Permission] = {
    withSQL {
      select.from(Permission as p).where.append(where)
    }.map(Permission(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Permission as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    permissionType: String,
    userId: Option[String] = None,
    value: String,
    delFlg: Boolean,
    insTm: DateTime,
    updTm: DateTime)(implicit session: DBSession = autoSession): Permission = {
    val generatedKey = withSQL {
      insert.into(Permission).namedValues(
        column.permissionType -> permissionType,
        column.userId -> userId,
        column.value -> value,
        column.delFlg -> delFlg,
        column.insTm -> insTm,
        column.updTm -> updTm
      )
    }.updateAndReturnGeneratedKey.apply()

    Permission(
      id = generatedKey,
      permissionType = permissionType,
      userId = userId,
      value = value,
      delFlg = delFlg,
      insTm = insTm,
      updTm = updTm)
  }

  def batchInsert(entities: Seq[Permission])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'permissionType -> entity.permissionType,
        'userId -> entity.userId,
        'value -> entity.value,
        'delFlg -> entity.delFlg,
        'insTm -> entity.insTm,
        'updTm -> entity.updTm))
    SQL("""insert into PERMISSION(
        permission_type,
        USER_id,
        value,
        del_flg,
        ins_tm,
        upd_tm
      ) values (
        {permissionType},
        {userId},
        {value},
        {delFlg},
        {insTm},
        {updTm}
      )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Permission)(implicit session: DBSession = autoSession): Permission = {
    withSQL {
      update(Permission).set(
        column.id -> entity.id,
        column.permissionType -> entity.permissionType,
        column.userId -> entity.userId,
        column.value -> entity.value,
        column.delFlg -> entity.delFlg,
        column.insTm -> entity.insTm,
        column.updTm -> entity.updTm
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Permission)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Permission).where.eq(column.id, entity.id) }.update.apply()
  }

}
