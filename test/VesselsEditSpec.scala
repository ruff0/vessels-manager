import models.Vessel
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import utils.MongoTestHelper.withMongoDb


import scala.concurrent._
import scala.concurrent.duration._
import models.JsonFormats._

@RunWith(classOf[JUnitRunner])
class VesselsEditSpec extends Specification {

  "Application" should {

    "edit vessel's name" in withMongoDb { implicit app =>
      

      val vessel = Await.result(ReactiveMongoPlugin.db.collection[JSONCollection]("vessels")
        .genericQueryBuilder.cursor[Vessel].headOption, Duration(2, SECONDS)).get
     
      
      val vesselName = JsString("Edited Vessel")

      val response = route(FakeRequest(PUT, "/vessels/" + vessel._id.stringify).withJsonBody(
        Json.obj(
          "name" -> vesselName,
          "width" -> vessel.width,
          "height" -> vessel.height,
          "draft" -> vessel.draft,
          "lastCoordinate" -> Json.toJson(vessel.lastCoordinate)
        )
      
      )).get

      status(response) must equalTo(OK)
      contentType(response) must beSome.which(_ == "application/json")
      val editedVessel = contentAsJson(response)
      
      editedVessel must not(beNull)
      
      (editedVessel \ "name").asInstanceOf[JsString] must equalTo(vesselName)
      
    }
    
    "edit a vessel with invalid format" in withMongoDb { implicit app =>
      val vessel = Await.result(ReactiveMongoPlugin.db.collection[JSONCollection]("vessels")
        .genericQueryBuilder.cursor[Vessel].headOption, Duration(2, SECONDS)).get
      
      val response = route(FakeRequest(PUT, "/vessels/" + vessel._id.stringify).withJsonBody(
        Json.obj(
          "vessel_name" -> "New Vessel",
          "vessel_width" -> 26
        )
      )).get

      status(response) must equalTo(UNPROCESSABLE_ENTITY)
      
      
      contentAsString(response) must equalTo("There are missing or invalid data")
    }
    
    "edit a vessel with invalid id" in withMongoDb { implicit app =>
      
      val response = route(FakeRequest(PUT, "/vessels/507f1f77bcf86cd799439011").withJsonBody(
        Json.obj(
          "name" -> "My Vessel",
          "width" -> 26,
          "height" -> 266,
          "draft" -> 5,
          "lastCoordinate" -> Json.obj("latitude" -> 58.1231, "longitude" -> -3.12356)
        )
      )).get

      status(response) must equalTo(NOT_FOUND)
      
      
      contentAsString(response) must equalTo("The vessel that you are looking for not exists")
    }

  }
  
}
