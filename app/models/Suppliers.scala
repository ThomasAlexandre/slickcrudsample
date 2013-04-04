package models

import scala.slick.driver.H2Driver.simple._
import scala.reflect.runtime.{ universe => ru }
import util.DynamicFinder

case class Supplier(
  id: Option[Long],
  name: String,
  street: String,
  city: String,
  state: String,
  zipCode: String)

// Definition of the SUPPLIERS table
object Suppliers extends Table[Supplier]("SUPPLIERS") with DynamicFinder {
  def id = column[Long]("SUP_ID", O.PrimaryKey,O AutoInc) // This is the primary key column
  def name = column[String]("SUP_NAME")
  def street = column[String]("STREET")
  def city = column[String]("CITY")
  def state = column[String]("STATE")
  def zipCode = column[String]("ZIP")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ name ~ street ~ city ~ state ~ zipCode <> (Supplier.apply _, Supplier.unapply _)

  def findAll() = for (s <- Suppliers) yield s

  val mirror = ru.runtimeMirror(getClass.getClassLoader)

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, order:String, asc:Boolean= false, filter: String = "%") = {
    val members = ru.typeOf[Supplier].members.filter(m => m.isTerm && !m.isMethod).toList
    val fields = members.map(_.name.decoded.trim).reverse.toVector
    println("Fields of Supplier class: " + fields)

    val sortField: String = fields(orderBy.abs - 1)
    println("The field to sort against is: " + sortField)

    // Need to give the sorting field at compile time... is there a better way ?
    val methodFields = sortField match {
      case "street" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("street")).asMethod
      case "city" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("city")).asMethod
      case "state" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("state")).asMethod
      case "zipCode" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("zipCode")).asMethod
      case "name" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("name")).asMethod
      case "id" => ru.typeOf[Suppliers.type].declaration(ru.newTermName("id")).asMethod
    }

    findAll.sortBy { x =>    
      val reflectedMethod = mirror.reflect(x).reflectMethod(methodFields)().asInstanceOf[Column[Any]]
      if (orderBy >= 0) reflectedMethod.asc
      else reflectedMethod.desc
    }.drop(page * pageSize).take(pageSize)
  }

  def findByPK(pk: Long) =
    for (entity <- Suppliers if entity.id === pk) yield entity 

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options = this.findAll.map(x => x.id -> x.name)
  
  def findByIdAndName(id:Long,name:String)  = {
     val result = for (
        entity <- Suppliers 
        if entity.id === id && entity.name === name
        ) yield entity
      result
    }
        
  def findByNameAndCity(name:String,city:String)  =
    for (
        entity <- Suppliers 
        if entity.name === name && entity.city === city
        ) yield entity
        
  def findByIdAndNameAndCity(id:Long,name:String,city:String)  =
    for (
        entity <- Suppliers 
        if entity.id===id && entity.name === name && entity.city === city
        ) yield entity
  
}

