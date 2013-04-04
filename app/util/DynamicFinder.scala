package util

import scala.language.dynamics
import shapeless.{Iso, ::, HList, HNil}
import models.Suppliers

case class FinderMethod(name:String) {
  case class Params(args:Any*) {
    require(true)
  }
}
trait DynamicFinder extends Dynamic {
  
//  def applyDynamic(method:String)(name:String, price:Integer) = {
//      println("Method: "+method+" , Arguments: ("+name+","+price+")")
//  }
  
  def applyDynamic(method:String)(args:Any*) = {
      println("Method For Strings: "+method+" , Arguments: "+args)
      findBy(2, "Superior Coffee")
  }
  
  def findBy(id:Long,name:String)  = {
     val result = for (
        entity <- Suppliers 
        if entity.id === id && entity.name === name
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