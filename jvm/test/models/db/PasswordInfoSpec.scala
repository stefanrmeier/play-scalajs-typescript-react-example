package models.db

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import org.joda.time.{ DateTime }

class PasswordInfoSpec extends Specification {

  "PasswordInfo" should {

    val pi = PasswordInfo.syntax("pi")

    "find by primary keys" in new AutoRollback {
      val maybeFound = PasswordInfo.find(1L, 1L)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = PasswordInfo.findBy(sqls.eq(pi.loginInfoId, 1L))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = PasswordInfo.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = PasswordInfo.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = PasswordInfo.findAllBy(sqls.eq(pi.loginInfoId, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = PasswordInfo.countBy(sqls.eq(pi.loginInfoId, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = PasswordInfo.create(loginInfoId = 1L, hasher = "MyString", password = "MyString", delFlg = false, insTm = DateTime.now, updTm = DateTime.now)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = PasswordInfo.findAll().head
      // TODO modify something
      val modified = entity
      val updated = PasswordInfo.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = PasswordInfo.findAll().head
      val deleted = PasswordInfo.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = PasswordInfo.find(1L, 1L)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = PasswordInfo.findAll()
      entities.foreach(e => PasswordInfo.destroy(e))
      val batchInserted = PasswordInfo.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
