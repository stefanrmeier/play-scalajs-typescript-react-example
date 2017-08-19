package models.db

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import org.joda.time.{ DateTime }

class PermissionSpec extends Specification {

  "Permission" should {

    val p = Permission.syntax("p")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Permission.find(1L)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Permission.findBy(sqls.eq(p.id, 1L))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Permission.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Permission.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Permission.findAllBy(sqls.eq(p.id, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Permission.countBy(sqls.eq(p.id, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Permission.create(permissionType = "MyString", value = "MyString", delFlg = false, insTm = DateTime.now, updTm = DateTime.now)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Permission.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Permission.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Permission.findAll().head
      val deleted = Permission.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Permission.find(1L)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Permission.findAll()
      entities.foreach(e => Permission.destroy(e))
      val batchInserted = Permission.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
