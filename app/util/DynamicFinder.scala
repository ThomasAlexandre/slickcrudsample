package util

import scala.language.dynamics

trait DynamicFinder extends Dynamic {
  
  def applyDynamic(method:String)(name:String, price:Integer) = {
      println("Method: "+method+" , Arguments: ("+name+","+price+")")
  }

}