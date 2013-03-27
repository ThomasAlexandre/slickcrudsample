package models

import scala.slick.driver.H2Driver.simple._
import scala.reflect.runtime.{ universe => ru }

case class Coffee(
  //id: Option[Int],
  //name: String,
  name: Option[String],
  supID: Int,
  price: Long,
  sales: Int,
  total: Int)

// Definition of the COFFEES table
object Coffees extends Table[Coffee]("COFFEES") {
  //def id = column[Int]("COF_ID", O.PrimaryKey, O AutoInc) // This is the primary key column
  def name = column[String]("COF_NAME", O.PrimaryKey) // This is the primary key column
  def supID = column[Int]("SUP_ID")
  def price = column[Long]("PRICE")
  def sales = column[Int]("SALES")
  def total = column[Int]("TOTAL")
  //def * = id.? ~ name ~ supID ~ price ~ sales ~ total <> (Coffee.apply _, Coffee.unapply _)
  def * = name.? ~ supID ~ price ~ sales ~ total <> (Coffee.apply _, Coffee.unapply _)
  //def autoInc = id.? ~ name ~ supID ~ price ~ sales ~ total <> (Coffee, Coffee.unapply _) returning id
  // A reified foreign key relation that can be navigated to create a join
  def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.supId)

  def findAll(filter: String = "%") = {
    for {
      c <- Coffees
      s <- c.supplier
      if (c.name like ("%" + filter))
    } yield (c, s)
  }

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%") = {
    val members = ru.typeOf[Coffee].members.filter(m => m.isTerm && !m.isMethod).toList
    val fields = members.map(_.name).reverse.zipWithIndex
    println("Fields of Coffee: " + fields) // List((id ,0), (name ,1), (supID ,2), (price ,3), (sales ,4), (total ,5))
    //findAll(filter).sortBy(_._1.name).drop(page * pageSize).take(pageSize)
    findAll(filter).sortBy(_._1.price.asc).drop(page * pageSize).take(pageSize)
  }

  def findByPK(pk: String) =
    for (c <- Coffees if c.name === pk) yield c
}


