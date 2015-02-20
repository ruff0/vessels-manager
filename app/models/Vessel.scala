package models

/**
 * Created by Damian on 2/20/15.
 */
case class Vessel(name: String, width: Int, height: Int, draft: Int, lastCoordinate: Coordinate)

case class Coordinate(latitude: Double, longitude: Double)
