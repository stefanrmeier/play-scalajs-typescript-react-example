package models.es

/**
 * Created by stefanrmeier 2017/05/29.
 */
case class QueryParams(
  keywords: List[String] = List(),
  industries: List[String] = List(),
  skills: List[String] = List(),
  p: Int = 0,
  ps: Int = 30) {
  def from: Int = ps * p
  def size: Int = ps

  def noMustTerms = keywords.isEmpty
  def noFilterTerms = (industries ::: skills).isEmpty
}