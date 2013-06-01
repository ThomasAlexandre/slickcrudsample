package util

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._
import models._

import play.api.db.DB
import play.api.Play.current

// Use H2Driver to connect to an H2 database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import scala.language.dynamics
import shapeless._
import models._
import util._
//import Record._
import Suppliers._

trait CRUD[T,K] {
  
  def index()
  def list(): Seq[T]
  def create(): T
  def save(t: T): Unit
  def edit(id: K): T
  def update(id: K, version: Long)
  def delete(id: K)
  
}