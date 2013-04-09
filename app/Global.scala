import play.api.GlobalSettings

import models._
import play.api.db.DB
import play.api.Application
import play.api.Play.current

import scala.slick.driver.H2Driver.simple._
// import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession


object Global extends GlobalSettings {

  override def onStart(app: Application) {

    lazy val database = Database.forDataSource(DB.getDataSource())

    database withSession {
      // Create the tables, including primary and foreign keys
      val ddl = (Suppliers.ddl ++ Coffees.ddl)

      //ddl.drop
      ddl.create

      // Insert some suppliers
      Suppliers.insertAll(
        Supplier(Some(1), "Acme, Inc.", "99 Market Street", "Mendocino", "CA", "95199"),
        Supplier(Some(2), "Superior Coffee", "1 Party Place", "Groundsville", "CA", "95460"),
        Supplier(Some(3), "The High Ground", "100 Coffee Lane", "Groundsville", "CA", "93966"))

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      Coffees.insertAll(
        Coffee(Some(1),"Colombian", 2, 799, 0, 0),
        Coffee(Some(2),"French_Roast", 3, 899, 0, 0),
        Coffee(Some(3),"Espresso", 1, 999, 0, 0),
        Coffee(Some(4),"Colombian_Decaf", 1, 899, 0, 0),
        Coffee(Some(5),"French_Roast_Decaf", 3, 999, 0, 0))
    }
  }
}