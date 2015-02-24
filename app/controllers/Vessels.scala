package controllers

import models.Vessel
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import views.html
import scala.concurrent.Future

// Reactive Mongo imports
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection


object Vessels extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("vessels")

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
}