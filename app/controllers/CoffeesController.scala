package controllers

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

object CoffeesController extends Controller {

  lazy val database = Database.forDataSource(DB.getDataSource())

  val pageSize = 3

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.CoffeesController.list(0, 2, ""))

  val supplierSelect = database withSession {
    Suppliers.options.list.map(item => (item._1.toString, item._2))
  }

  /**
   * Describe the form (used in both edit and create screens).
   */
  val form = Form(
    mapping(
      //"id" -> optional(number),
      "name" -> optional(nonEmptyText),
      "supID" -> number,
      "price" -> longNumber,
      "sales" -> number,
      "total" -> number)(Coffee.apply)(Coffee.unapply))

  // -- Actions

  /**
   * Handle default path requests, redirect to entities list
   */
  def index = Action { Home }

  /**
   * Display the paginated list.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on entity names
   */
  def list(page: Int, orderBy: Int, filter: String = "%") = Action { implicit request =>
    database withSession {
      Ok(html.coffees.list(
        Page(Coffees.list(page, pageSize, orderBy, filter).list, 
            page, 
            offset = pageSize * page, 
            Coffees.findAll(filter).list.size),
        orderBy,
        filter))
    }
  }
  
  /**
   * Display the 'new form'.
   */
  def create = Action {
    database withSession {
      Ok(html.coffees.createForm(form, supplierSelect))
    }
  }
  
   /**
   * Handle the 'new form' submission.
   */
  def save = Action { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => BadRequest(html.coffees.createForm(formWithErrors, supplierSelect)),
      entity => {
        database withTransaction {
          Coffees.insert(entity)
          Home.flashing("success" -> s"Entity ${entity.name} has been created")
        }
      })
  }

  /**
   * Display the 'edit form' of an existing entity.
   *
   * @param id Id of the entity to edit
   */
  def edit(pk: String) = Action {
    database withSession {
      Coffees.findByPK(pk).list.headOption match {
        case Some(e) => Ok(html.coffees.editForm(pk, form.fill(e), supplierSelect))
        case None => NotFound
      }
    }
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the entity to edit
   */
  def update(pk: String) = Action { implicit request =>
    database withSession {
      form.bindFromRequest.fold(
        formWithErrors => BadRequest(html.coffees.editForm(pk, formWithErrors, supplierSelect)),
        entity => {
          Home.flashing(Coffees.findByPK(pk).update(entity) match {
            case 0 => "failure" -> s"Could not update entity ${entity.name}"
            case _ => "success" -> s"Entity ${entity.name} has been updated"
          })
        })
    }
  }

  /**
   * Handle entity deletion.
   */
  def delete(pk: String) = Action {
    database withSession {
      Home.flashing(Coffees.findByPK(pk).delete match {
        case 0 => "failure" -> "Entity has Not been deleted"
        case x => "success" -> s"Entity has been deleted (deleted $x row(s))"
      })
    }
  }

}
            
