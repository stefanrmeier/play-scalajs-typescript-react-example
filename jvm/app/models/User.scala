package models

import java.util.UUID

import be.objectify.deadbolt.scala.models.Subject
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 */
case class User(
  userID: UUID,
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String],
  activated: Boolean,
  roles: List[Role],
  permissions: List[Permission]) extends Identity with Subject {

  override def identifier: String = name.getOrElse(userID.toString) //TODO Just so that it runs. fix specs here

  /**
   * Tries to construct a name.
   *
   * @return Maybe a name.
   */
  def name: Option[String] = fullName.orElse {
    firstName -> lastName match {
      case (Some(f), Some(l)) => Some(f + " " + l)
      case (Some(f), None) => Some(f)
      case (None, Some(l)) => Some(l)
      case _ => None
    }
  }
}

//TODO Just so that it runs. implement this properly
object User {
  def apply(name: String): User = User(UUID.randomUUID(), null, Some(name), Some(name), Some(name), Some(name),
    Some(name), activated = true, Nil, Nil)
}
