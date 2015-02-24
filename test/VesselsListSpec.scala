import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import utils.MongoTestHelper.withMongoDb

@RunWith(classOf[JUnitRunner])
class VesselsListSpec extends Specification {

  "Application" should {

    "list all vessels" in withMongoDb { implicit app =>
      val response = route(FakeRequest(GET, "/vessels")).get

      status(response) must equalTo(OK)
      contentType(response) must beSome.which(_ == "application/json")
      val allVessels = contentAsJson(response)
      allVessels must not beNull

      (allVessels \ "page").asInstanceOf[JsNumber].value must equalTo(1)

      (allVessels \ "next_page") must equalTo(JsNull)

      (allVessels \ "result").asInstanceOf[JsArray].value.size must beEqualTo(10)
    }

    "list first page with 5 records per page" in withMongoDb { implicit app =>
      val response = route(FakeRequest(GET, "/vessels?page=1&max=5")).get

      status(response) must equalTo(OK)

      val allVessels = contentAsJson(response)
      allVessels must not beNull

      (allVessels \ "page").asInstanceOf[JsNumber].value must equalTo(1)

      (allVessels \ "next_page").asInstanceOf[JsNumber].value must equalTo(2)

      (allVessels \ "result").asInstanceOf[JsArray].value.size must equalTo(5)

      (allVessels \ "total_records").asInstanceOf[JsNumber].value must equalTo(5)

    }

    "get the first 2 pages with 5 records per page" in withMongoDb { implicit app =>

      // Get the first
      val firstResponse = route(FakeRequest(GET, "/vessels?page=1&max=5")).get

      status(firstResponse) must equalTo(OK)

      val firstPage = contentAsJson(firstResponse)

      val firstPageResult: Seq[JsValue] = (firstPage \ "result").asInstanceOf[JsArray].value


      // Get the second
      val secondResponse = route(FakeRequest(GET, "/vessels?page=2&max=5")).get

      val secondPage = contentAsJson(secondResponse)

      val secondPageResult: Seq[JsValue] = (secondPage \ "result").asInstanceOf[JsArray].value

      firstPageResult must not(equalTo(secondPageResult))

    }


    "list out of index page" in withMongoDb { implicit app =>
      val response = route(FakeRequest(GET, "/vessels?page=3&max=5")).get

      status(response) must equalTo(OK)

      val allVessels = contentAsJson(response)
      allVessels must not beNull

      (allVessels \ "page").asInstanceOf[JsNumber].value must equalTo(3)

      (allVessels \ "result").asInstanceOf[JsArray].value.size must equalTo(0)

      (allVessels \ "next_page") must equalTo(JsNull)

      (allVessels \ "total_records").asInstanceOf[JsNumber].value must equalTo(0)

    }

    "list all vessels that contains '5' in his name" in withMongoDb { implicit app =>
      val response = route(FakeRequest(GET, "/vessels?page=1&max=5&filter=5")).get

      status(response) must equalTo(OK)

      val allVessels = contentAsJson(response)

      (allVessels \ "page").asInstanceOf[JsNumber].value must equalTo(1)

      val result: Seq[JsValue] = (allVessels \ "result").asInstanceOf[JsArray].value

      result.size must equalTo(1)

      (result.head \ "name").asInstanceOf[JsString].value must contain("5")

      (allVessels \ "next_page") must equalTo(JsNull)

      (allVessels \ "total_records").asInstanceOf[JsNumber].value must equalTo(1)
    }


  }
}
