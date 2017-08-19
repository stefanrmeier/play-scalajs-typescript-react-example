package models.db

import scalikejdbc._
import org.joda.time.{ DateTime }

case class Role(
  id: Long,
  userId: Option[String] = None,
  roleType: String,
  name: String,
  delFlg: Boolean,
  insTm: DateTime,
  updTm: DateTime) {

  def save()(implicit session: DBSession = Role.autoSession): Role = Role.save(this)(session)

  def destroy()(implicit session: DBSession = Role.autoSession): Int = Role.destroy(this)(session)

}

object Role extends SQLSyntaxSupport[Role] {

  override val schemaName = Some("example")

  override val tableName = "ROLE"

  override val columns = Seq("id", "USER_id", "role_type", "name", "del_flg", "ins_tm", "upd_tm")

  def apply(r: SyntaxProvider[Role])(rs: WrappedResultSet): Role = apply(r.resultName)(rs)
  def apply(r: ResultName[Role])(rs: WrappedResultSet): Role = new Role(
    id = rs.get(r.id),
    userId = rs.get(r.userId),
    roleType = rs.get(r.roleType),
    name = rs.get(r.name),
    delFlg = rs.get(r.delFlg),
    insTm = rs.get(r.insTm),
    updTm = rs.get(r.updTm)
  )

  val r = Role.syntax("r")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Role] = {
    withSQL {
      select.from(Role as r).where.eq(r.id, id)
    }.map(Role(r.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Role] = {
    withSQL(select.from(Role as r)).map(Role(r.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Role as r)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Role] = {
    withSQL {
      select.from(Role as r).where.append(where)
    }.map(Role(r.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Role] = {
    withSQL {
      select.from(Role as r).where.append(where)
    }.map(Role(r.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Role as r).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    userId: Option[String] = None,
    roleType: String,
    name: String,
    delFlg: Boolean,
    insTm: DateTime,
    updTm: DateTime)(implicit session: DBSession = autoSession): Role = {
    val generatedKey = withSQL {
      insert.into(Role).namedValues(
        column.userId -> userId,
        column.roleType -> roleType,
        column.name -> name,
        column.delFlg -> delFlg,
        column.insTm -> insTm,
        column.updTm -> updTm
      )
    }.updateAndReturnGeneratedKey.apply()

    Role(
      id = generatedKey,
      userId = userId,
      roleType = roleType,
      name = name,
      delFlg = delFlg,
      insTm = insTm,
      updTm = updTm)
  }

  def batchInsert(entities: Seq[Role])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'userId -> entity.userId,
        'roleType -> entity.roleType,
        'name -> entity.name,
        'delFlg -> entity.delFlg,
        'insTm -> entity.insTm,
        'updTm -> entity.updTm))
    SQL("""insert into ROLE(
        USER_id,
        role_type,
        name,
        del_flg,
        ins_tm,
        upd_tm
      ) values (
        {userId},
        {roleType},
        {name},
        {delFlg},
        {insTm},
        {updTm}
      )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Role)(implicit session: DBSession = autoSession): Role = {
    withSQL {
      update(Role).set(
        column.id -> entity.id,
        column.userId -> entity.userId,
        column.roleType -> entity.roleType,
        column.name -> entity.name,
        column.delFlg -> entity.delFlg,
        column.insTm -> entity.insTm,
        column.updTm -> entity.updTm
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Role)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Role).where.eq(column.id, entity.id) }.update.apply()
  }

}
