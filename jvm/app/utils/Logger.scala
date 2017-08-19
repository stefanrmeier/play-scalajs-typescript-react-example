package utils

/**
 * Implement this to getById a named logger in scope.
 */
trait Logger {

  /**
   * A named logger instance.
   */
  val logger = play.api.Logger(this.getClass)
}
