import models.JsonFormats._
import models.Vessel
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.core.commands.Count
import utils.MongoTestHelper.withMongoDb

import scala.concurrent._
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class VesselsDeleteSpec extends Specification {

  "Application" should {

    "delete vessel" in withMongoDb { implicit app =>
      

      val vessel = Await.result(ReactiveMongoPlugin.db.collection[JSONCollection]("vessels")
        .genericQueryBuilder.cursor[Vessel].headOption, Duration(2, SECONDS)).get


      val response = route(FakeRequest(DELETE, "/vessels/" + vessel._id.stringify)).get

      status(response) must equalTo(NO_CONTENT)
      
      ReactiveMongoPlugin.db.command(Count("vessels")) must equalTo(9).await
      
    }

    
    "delete a vessel with invalid id" in withMongoDb { implicit app =>

      val response = route(FakeRequest(DELETE, "/vessels/507f1f77bcf86cd799439011")).get

      status(response) must equalTo(NOT_FOUND)


      contentAsString(response) must equalTo("The vessel that you are looking for not exists")
    }

  }
  
}
