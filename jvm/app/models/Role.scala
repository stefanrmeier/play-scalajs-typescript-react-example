package models

import be.objectify.deadbolt.scala.models.{ Role => DeadboltRole }

/**
 * Created by stefanrmeier 2017/03/01.
 */
abstract class Role(val name: String, val roleType: String) extends DeadboltRole

abstract class SecurityRole(override val name: String) extends Role(name, "sec")

object Role {
  def apply(name: String): Option[Role] = Roles.listAll.find(_.name == name)
}

object Roles {

  case object Admin extends SecurityRole("admin")
  case object User extends SecurityRole("user")

  def listAll: List[Role] = List(Admin, User)

}
