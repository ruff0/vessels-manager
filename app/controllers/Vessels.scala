package controllers

import models.{Coordinate, Vessel}
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import views.html
import scala.concurrent.Future

// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection


object Vessels extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("vessels")

  val vesselForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "width" -> number,
      "height" -> number,
      "draft" -> number,
      "lastCoordinate" -> mapping("latitude" -> bigDecimal, "longitude" -> bigDecimal)(Coordinate.apply)(Coordinate.unapply),
      "_id" -> ignored(BSONObjectID.generate: BSONObjectID))(Vessel.apply)(Vessel.unapply))


  import play.api.data.Form
  import models._
  import models.JsonFormats._

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def list(page: Option[Int], max: Option[Int], filter: Option[String]) = Action.async { implicit request =>

    val futureVesselPage = filter match {
      case Some(filter) => collection.find(Json.obj("name" -> Json.obj("$regex" -> (".*" + filter + ".*"))))
      case None => collection.genericQueryBuilder
    }

    val futureVesselPageWithOpts = (max, page) match {
      case (Some(max), Some(page)) => futureVesselPage.options(QueryOpts((page - 1) * max, max)).cursor[Vessel].collect[List](max)
      case _ => futureVesselPage.cursor[Vessel].collect[List]()
    }



    //Take the current page, otherwise returns 1 as default value
    val currentPage: Int = page.getOrElse(1)

    futureVesselPageWithOpts.map { vessels =>

      //Get the next page
      val nextPage: JsValue = vessels match {
        case _ if vessels.size == max.getOrElse(vessels.size + 1) => JsNumber(currentPage + 1)
        case _ => JsNull
      }

      Ok(Json.obj(
        "page" -> currentPage,
        "next_page" -> nextPage,
        "total_records" -> vessels.size,
        "result" -> Json.toJson(vessels)
      ))
    }
  }
  
  
  def create = Action.async(parse.json) { implicit request =>
    
    vesselForm.bindFromRequest.fold(
      validationErrors => Future.successful(UnprocessableEntity("There are missing or invalid data")),
      vessel => {
        collection.insert(vessel).map { lastError =>
          Logger.debug(s"Successfully inserted with LastError: $lastError")
          Created(Json.toJson(vessel))
        }
        
      }
    )
  }

  def edit(id: String) = Action.async(parse.json) { implicit request =>

    vesselForm.bindFromRequest.fold(
      validationErrors => Future.successful(UnprocessableEntity("There are missing or invalid data")),
      vessel => {
        collection.update(Json.obj("_id" -> id), vessel.copy(_id = BSONObjectID(id))).map { lastError =>
          lastError.updated match {
            case 1 => Ok(Json.toJson(vessel))
            case 0 => NotFound("The vessel that you are looking for not exists")
          }
        }
      }
    )
  }

  def delete(id: String) = Action.async { implicit request =>
    collection.remove(Json.obj("_id" -> id)).map { lastError =>
      lastError.updated match {
        case 1 => NoContent
        case 0 => NotFound("The vessel that you are looking for not exists")
      }
    }
  }
}