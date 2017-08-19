package models.db

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import org.joda.time.{ DateTime }

class AuthTokenSpec extends Specification {

  "AuthToken" should {

    val at = AuthToken.syntax("at")

    "find by primary keys" in new AutoRollback {
      val maybeFound = AuthToken.find("MyString")
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = AuthToken.findBy(sqls.eq(at.id, "MyString"))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = AuthToken.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = AuthToken.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = AuthToken.findAllBy(sqls.eq(at.id, "MyString"))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = AuthToken.countBy(sqls.eq(at.id, "MyString"))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = AuthToken.create(id = "MyString", userUserId = "MyString", expiry = DateTime.now, insTm = DateTime.now, updTm = DateTime.now)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = AuthToken.findAll().head
      // TODO modify something
      val modified = entity
      val updated = AuthToken.save(modified)
      updated should not equalTo (entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = AuthToken.findAll().head
      val deleted = AuthToken.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = AuthToken.find("MyString")
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = AuthToken.findAll()
      entities.foreach(e => AuthToken.destroy(e))
      val batchInserted = AuthToken.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
