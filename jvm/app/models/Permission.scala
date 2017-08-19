package models

import be.objectify.deadbolt.scala.models.{ Permission => DeadboltPermission }

/**
 * Created by stefanrmeier 2017/03/01.
 */
abstract class Permission(val value: String, val permissionType: String) extends DeadboltPermission

abstract class UserPermission(override val value: String) extends Permission(value, "user")

object Permission {
  def apply(value: String): Option[Permission] = Permissions.listAll.find(_.value == value)
}

object Permissions {

  def listAll: List[Permission] = List()

}
