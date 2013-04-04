package util

import scala.language.dynamics
import shapeless.{Iso, ::, HList, HNil}

trait DynamicFinder extends Dynamic {
  
//  def applyDynamic(method:String)(name:String, price:Integer) = {
//      println("Method: "+method+" , Arguments: ("+name+","+price+")")
//  }
  
//  def applyDynamic(method:String)(args:String*) = {
//      println("Method For Strings: "+method+" , Arguments: "+args)
//  }
  
  def applyDynamic(method:String)(args:HList) = {
      println("Method For Integers: "+method+" , Arguments: "+args)
  }

}