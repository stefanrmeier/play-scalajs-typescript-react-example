package models.db

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._

class UserHasLoginInfoSpec extends Specification {

  "UserHasLoginInfo" should {

    val uhli = UserHasLoginInfo.syntax("uhli")

    "find by primary keys" in new AutoRollback {
      val maybeFound = UserHasLoginInfo.find(1L, "MyString")
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = UserHasLoginInfo.findBy(sqls.eq(uhli.loginInfoId, 1L))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = UserHasLoginInfo.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = UserHasLoginInfo.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = UserHasLoginInfo.findAllBy(sqls.eq(uhli.loginInfoId, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = UserHasLoginInfo.countBy(sqls.eq(uhli.loginInfoId, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = UserHasLoginInfo.create(userId = "MyString", loginInfoId = 1L)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = UserHasLoginInfo.findAll().head
      // TODO modify something
      val modified = entity
      val updated = UserHasLoginInfo.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = UserHasLoginInfo.findAll().head
      val deleted = UserHasLoginInfo.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = UserHasLoginInfo.find(1L, "MyString")
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = UserHasLoginInfo.findAll()
      entities.foreach(e => UserHasLoginInfo.destroy(e))
      val batchInserted = UserHasLoginInfo.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
