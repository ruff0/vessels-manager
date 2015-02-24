import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import utils.MongoTestHelper.withMongoDb

@RunWith(classOf[JUnitRunner])
class VesselsCreateSpec extends Specification {

  "Application" should {

    "create a new vessel" in withMongoDb { implicit app =>
      val response = route(FakeRequest(POST, "/vessels").withJsonBody(
        Json.obj(
          "name" -> "New Vessel",
          "width" -> 26,
          "height" -> 266,
          "draft" -> 5,
          "lastCoordinate" -> Json.obj("latitude" -> 58.1231, "longitude" -> -3.12356)
        )
      
      )).get

      status(response) must equalTo(CREATED)
      contentType(response) must beSome.which(_ == "application/json")
      val createdVessel = contentAsJson(response)
      
      createdVessel must not(beNull)
      
      (createdVessel \ "_id").asInstanceOf[JsString] must not(beNull[JsString])
      
    }
    
    "create a vessel with invalid format" in withMongoDb { implicit app =>
      val response = route(FakeRequest(POST, "/vessels").withJsonBody(
        Json.obj(
          "vessel_name" -> "New Vessel",
          "vessel_width" -> 26
        )
      )).get

      status(response) must equalTo(UNPROCESSABLE_ENTITY)
      
      
      contentAsString(response) must equalTo("There are missing or invalid data")
    }

  }
}
