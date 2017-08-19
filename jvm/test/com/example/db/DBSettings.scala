package com.example.db

/**
 * Created by stefan.meier on 25-05-2017.
 */
import scalikejdbc._
import scalikejdbc.config._

trait DBSettings {
  DBSettings.initialize()
}

object DBSettings {

  private var isInitialized = false

  def initialize(): Unit = this.synchronized {
    if (isInitialized) return
    DBs.setupAll()
    GlobalSettings.loggingSQLErrors = true
    GlobalSettings.sqlFormatter = SQLFormatterSettings("utils.MySQLFormatter")
    isInitialized = true
  }
}
