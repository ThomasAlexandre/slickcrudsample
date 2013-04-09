package util

import scala.language.dynamics
import shapeless._
import models._

import util._
import Record._

import scala.slick.driver.H2Driver.simple._
import scala.reflect.runtime.{ universe => ru }

//object thename extends shapeless.Field[String]
//object thecity extends shapeless.Field[String]


case class FinderMethod(name:String) {
  case class Params(args:Any*) {
    require(true)
  }
}

trait DynamicFinder extends Dynamic {
  
//  def applyDynamic(method:String)(name:String, price:Integer) = {
//      println("Method: "+method+" , Arguments: ("+name+","+price+")")
//  }
  
  
  def applyDynamic(method:String)(arguments:AnyVal*) = {
      println("Method For Strings: "+method+" , Arguments: "+arguments)
      
      val members = ru.typeOf[Supplier].members.filter(m => m.isTerm && !m.isMethod).toList
      val fields = members.map(_.name.decoded.trim).reverse.toVector
      println("Fields of Supplier class: " + fields)
      
      
      println("Arguments are : "+arguments)
//      
//      
//      val city = arguments(1).toString
      
      val city = arguments(1).toString
      
      //println("city  is " +city)
      val result = for (
        entity <- Suppliers if  entity.city === city
        ) yield entity
      result
  }
  
  
  
//  def applyDynamic(method:String)(args:HList) = {
//      println("Method For Integers: "+method+" , Arguments: "+args)
//  }

}


/* Example dependent types
case class Board(length: Int, height: Int) {
  case class Coordinate(x: Int, y: Int) { 
    require(0 <= x && x < length && 0 <= y && y < height) 
  }
  val occupied = scala.collection.mutable.Set[Coordinate]()
}
val b1 = Board(20, 20)
val b2 = Board(30, 30)
val c1 = b1.Coordinate(15, 15)
val c2 = b2.Coordinate(25, 25)
b1.occupied += c1
b2.occupied += c2
// Next line doesn't compile
b1.occupied += c2

*/