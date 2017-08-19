package models.db

import scalikejdbc._
import org.joda.time.{ DateTime }

case class User(
  id: String,
  username: Option[String] = None,
  email: Option[String] = None,
  activated: Boolean,
  testFlg: Boolean,
  delFlg: Boolean,
  insTm: DateTime,
  updTm: DateTime) {

  def save()(implicit session: DBSession = User.autoSession): User = User.save(this)(session)

  def destroy()(implicit session: DBSession = User.autoSession): Int = User.destroy(this)(session)

}

object User extends SQLSyntaxSupport[User] {

  override val schemaName = Some("example")

  override val tableName = "USER"

  override val columns = Seq("id", "username", "email", "activated", "test_flg", "del_flg", "ins_tm", "upd_tm")

  def apply(u: SyntaxProvider[User])(rs: WrappedResultSet): User = apply(u.resultName)(rs)
  def apply(u: ResultName[User])(rs: WrappedResultSet): User = new User(
    id = rs.get(u.id),
    username = rs.get(u.username),
    email = rs.get(u.email),
    activated = rs.get(u.activated),
    testFlg = rs.get(u.testFlg),
    delFlg = rs.get(u.delFlg),
    insTm = rs.get(u.insTm),
    updTm = rs.get(u.updTm)
  )

  val u = User.syntax("u")

  override val autoSession = AutoSession

  def find(id: String)(implicit session: DBSession = autoSession): Option[User] = {
    withSQL {
      select.from(User as u).where.eq(u.id, id)
    }.map(User(u.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[User] = {
    withSQL(select.from(User as u)).map(User(u.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(User as u)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[User] = {
    withSQL {
      select.from(User as u).where.append(where)
    }.map(User(u.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[User] = {
    withSQL {
      select.from(User as u).where.append(where)
    }.map(User(u.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(User as u).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    id: String,
    username: Option[String] = None,
    email: Option[String] = None,
    activated: Boolean,
    testFlg: Boolean,
    delFlg: Boolean,
    insTm: DateTime,
    updTm: DateTime)(implicit session: DBSession = autoSession): User = {
    withSQL {
      insert.into(User).namedValues(
        column.id -> id,
        column.username -> username,
        column.email -> email,
        column.activated -> activated,
        column.testFlg -> testFlg,
        column.delFlg -> delFlg,
        column.insTm -> insTm,
        column.updTm -> updTm
      )
    }.update.apply()

    User(
      id = id,
      username = username,
      email = email,
      activated = activated,
      testFlg = testFlg,
      delFlg = delFlg,
      insTm = insTm,
      updTm = updTm)
  }

  def batchInsert(entities: Seq[User])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'id -> entity.id,
        'username -> entity.username,
        'email -> entity.email,
        'activated -> entity.activated,
        'testFlg -> entity.testFlg,
        'delFlg -> entity.delFlg,
        'insTm -> entity.insTm,
        'updTm -> entity.updTm))
    SQL("""insert into USER(
        id,
        username,
        email,
        activated,
        test_flg,
        del_flg,
        ins_tm,
        upd_tm
      ) values (
        {id},
        {username},
        {email},
        {activated},
        {testFlg},
        {delFlg},
        {insTm},
        {updTm}
      )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: User)(implicit session: DBSession = autoSession): User = {
    withSQL {
      update(User).set(
        column.id -> entity.id,
        column.username -> entity.username,
        column.email -> entity.email,
        column.activated -> entity.activated,
        column.testFlg -> entity.testFlg,
        column.delFlg -> entity.delFlg,
        column.insTm -> entity.insTm,
        column.updTm -> entity.updTm
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: User)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(User).where.eq(column.id, entity.id) }.update.apply()
  }

}
