package models.db

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import org.joda.time.{ DateTime }

class LoginInfoSpec extends Specification {

  "LoginInfo" should {

    val li = LoginInfo.syntax("li")

    "find by primary keys" in new AutoRollback {
      val maybeFound = LoginInfo.find(1L)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = LoginInfo.findBy(sqls.eq(li.id, 1L))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = LoginInfo.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = LoginInfo.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = LoginInfo.findAllBy(sqls.eq(li.id, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = LoginInfo.countBy(sqls.eq(li.id, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = LoginInfo.create(providerId = "MyString", providerKey = "MyString", delFlg = false, insTm = DateTime.now, updTm = DateTime.now)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = LoginInfo.findAll().head
      // TODO modify something
      val modified = entity
      val updated = LoginInfo.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = LoginInfo.findAll().head
      val deleted = LoginInfo.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = LoginInfo.find(1L)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = LoginInfo.findAll()
      entities.foreach(e => LoginInfo.destroy(e))
      val batchInserted = LoginInfo.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
