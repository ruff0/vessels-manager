package utils

import models.JsonFormats._
import models.{Coordinate, Vessel}
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.Helpers._
import play.api.test._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DefaultDB

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Random

/**
 *
 * @author <a href="mailto:damian.ea@gmail.com">Damian Albrun</a>
 */
object MongoTestHelper {

  /**
   * Run the given block with MongoDB
   */
  def withMongoDb[T](block: Application => T): T = {
    
    implicit val app = FakeApplication(
      additionalConfiguration = Map("mongodb.uri" -> "mongodb://localhost/unittests")
    )
    running(app) {
      val db = ReactiveMongoPlugin.db
      try {
        createVessels(db)
        block(app)
      } finally {
        dropAll(db)
      }
    }
  }

  def dropAll(db: DefaultDB) = {
    Await.ready(Future.sequence(Seq(
      db.collection[JSONCollection]("vessels").drop()
    )), 4 seconds)
  }

  def createVessels( db: DefaultDB) = {

    for(idx <- 1 to 10) {
      val vessel = Vessel(s"Vessel $idx",
        Random.nextInt(20) + 20, //Width
        Random.nextInt(300) + 300, //Height
        Random.nextInt(8) + 8, //Draft
        Coordinate(Random.nextDouble(), Random.nextDouble()))

      Await.ready(db.collection[JSONCollection]("vessels").insert(vessel), 1 second)
    }
  }
}