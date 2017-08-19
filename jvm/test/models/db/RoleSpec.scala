package models.db

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import org.joda.time.{ DateTime }

class RoleSpec extends Specification {

  "Role" should {

    val r = Role.syntax("r")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Role.find(1L)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Role.findBy(sqls.eq(r.id, 1L))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Role.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Role.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Role.findAllBy(sqls.eq(r.id, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Role.countBy(sqls.eq(r.id, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Role.create(roleType = "MyString", name = "MyString", delFlg = false, insTm = DateTime.now, updTm = DateTime.now)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Role.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Role.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Role.findAll().head
      val deleted = Role.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Role.find(1L)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Role.findAll()
      entities.foreach(e => Role.destroy(e))
      val batchInserted = Role.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
