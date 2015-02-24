package models

import java.util.UUID

import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats.PartialFormat
import reactivemongo.bson.{BSONValue, BSONObjectID}

import scala.util.Try

/**
 * Created by Damian on 2/20/15.
 */
case class Vessel(name : String, width: Int, height: Int, draft: Int, lastCoordinate: Coordinate, _id: BSONObjectID = BSONObjectID.generate)

case class Coordinate(latitude: BigDecimal, longitude: BigDecimal)



object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._


  implicit object BSONObjectIDFormat extends PartialFormat[BSONObjectID] {
    def partialReads: PartialFunction[JsValue, JsResult[BSONObjectID]] = {
      case JsString(v) => JsSuccess(BSONObjectID(v))
    }
    val partialWrites: PartialFunction[BSONValue, JsValue] = {
      case oid: BSONObjectID =>  JsString(oid.stringify)
    }
  }

  implicit val coordinateFormat: Format[Coordinate] = Json.format[Coordinate]
  implicit val vesselFormat: Format[Vessel] = Json.format[Vessel]


}



