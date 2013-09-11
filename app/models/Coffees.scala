package models

import scala.slick.lifted.ForeignKeyAction
import scala.slick.driver.H2Driver.simple._
import scala.reflect.runtime.{ universe => ru }
import shapeless._
import HList._

case class Coffee(
  id:Option[Long],
  name: String,
  supID: Long,
  price: Long,
  sales: Int,
  total: Int)

// Definition of the COFFEES table
object Coffees extends Table[Coffee]("COFFEES") {
  
  def id = column[Long]("ID", O.PrimaryKey, O AutoInc) // This is the primary key column
  def name = column[String]("COF_NAME")
  def supID = column[Long]("SUP_ID")
  def price = column[Long]("PRICE")
  def sales = column[Int]("SALES")
  def total = column[Int]("TOTAL")
 
  def * = id.? ~ name ~ supID ~ price ~ sales ~ total <> (Coffee.apply _, Coffee.unapply _)
  //def autoInc = id.? ~ name ~ supID ~ price ~ sales ~ total <> (Coffee, Coffee.unapply _) returning id
  
  // A reified foreign key relation that can be navigated to create a join
  def supplier = foreignKey("SUP_FK", supID, Suppliers)(_.id, onDelete = ForeignKeyAction.Cascade)

  def findAll(filter: String = "%") = {
    for {
      c <- Coffees
      s <- c.supplier
      if (c.name like ("%" + filter))
    } yield (c, s)
  }
  
  val mirror = ru.runtimeMirror(getClass.getClassLoader)
  
  val fields = { 
    val members = ru.typeOf[Coffee].members.filter(m => m.isTerm && !m.isMethod).toList
    val result = members.map(_.name.decoded.trim).reverse.toVector
    println("Fields of Supplier class: " + result)
    result
  }
  
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%") = {
    
    val sortField: String = fields(orderBy.abs - 1)
    println("The field to sort against is: " + sortField)

    // Need to give the sorting field at compile time... is there a better way ?
    val methodFields = sortField match {
      case "name" => ru.typeOf[Coffees.type].declaration(ru.newTermName("name")).asMethod
      case "supID" => ru.typeOf[Coffees.type].declaration(ru.newTermName("supID")).asMethod
      case "price" => ru.typeOf[Coffees.type].declaration(ru.newTermName("price")).asMethod
      case "sales" => ru.typeOf[Coffees.type].declaration(ru.newTermName("sales")).asMethod
      case "total" => ru.typeOf[Coffees.type].declaration(ru.newTermName("total")).asMethod
      case "id" => ru.typeOf[Coffees.type].declaration(ru.newTermName("id")).asMethod
    }

    findAll().sortBy { x =>    
      val reflectedMethod = mirror.reflect(x._1).reflectMethod(methodFields)().asInstanceOf[Column[Any]]
      if (orderBy >= 0) reflectedMethod.asc
      else reflectedMethod.desc
    }.drop(page * pageSize).take(pageSize)
  }

  def findByPK(pk: Long) =
    for (c <- Coffees if c.id === pk) yield c
    
}


