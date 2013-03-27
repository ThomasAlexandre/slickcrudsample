package models

import scala.slick.driver.H2Driver.simple._
import scala.reflect.runtime.{ universe => ru }

case class Supplier(
  supId: Option[Int],
  name: String,
  street: String,
  city: String,
  state: String,
  zipCode: String)

// Definition of the SUPPLIERS table
object Suppliers extends Table[Supplier]("SUPPLIERS") {
  def supId = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
  def name = column[String]("SUP_NAME")
  def street = column[String]("STREET")
  def city = column[String]("CITY")
  def state = column[String]("STATE")
  def zipCode = column[String]("ZIP")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = supId.? ~ name ~ street ~ city ~ state ~ zipCode <> (Supplier.apply _, Supplier.unapply _)

  def findAll() = for (s <- Suppliers) yield s

  val mirror = ru.runtimeMirror(getClass.getClassLoader)

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%") = {
    val members = ru.typeOf[Supplier].members.filter(m => m.isTerm && !m.isMethod).toList
    val fields = members.map(_.name.decoded.trim).reverse.toVector
    println("Fields of Supplier class: " + fields)

    val sortField: String = fields(orderBy.abs - 1)
    println("The field to sort against is: " + sortField)

    // Need to give the sorting field at compile time... is there a better way ?
    val objectSuppliers = sortField match {
      case "street" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("street")).asMethod
      case "city" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("city")).asMethod
      case "state" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("state")).asMethod
      case "zipCode" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("zipCode")).asMethod
      case "name" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("name")).asMethod
    }

    findAll.sortBy { x =>    
      val reflectedMethod = mirror.reflect(x).reflectMethod(objectSuppliers)().asInstanceOf[Column[Any]]
      if (orderBy >= 0) reflectedMethod.asc
      else reflectedMethod.desc
    }
  }

  def findByPK(pk: String) =
    for (c <- Suppliers if c.name === pk) yield c

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options = this.findAll.map(x => x.supId -> x.name)
}

