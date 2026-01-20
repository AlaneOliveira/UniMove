package com.dm.unimove.db.fb

import com.dm.unimove.model.Location
import com.dm.unimove.model.Ride
import com.google.firebase.firestore.GeoPoint

class FBRide {
    var startingName: String? = null
    var startingLat: Double? = null
    var startingLng: Double? = null
    
    var destName: String? = null
    var destLat: Double? = null
    var destLng: Double? = null

    fun toRide(): Ride {
        val startLoc = Location(
            name = startingName ?: "",
            coordinates = GeoPoint(startingLat ?: 0.0, startingLng ?: 0.0)
        )
        val destLoc = Location(
            name = destName ?: "",
            coordinates = GeoPoint(destLat ?: 0.0, destLng ?: 0.0)
        )
        return Ride(starting_point = startLoc, destination = destLoc)
    }
}

fun Ride.toFBRide(): FBRide {
    val fbRide = FBRide()
    fbRide.startingName = this.starting_point.name
    fbRide.startingLat = this.starting_point.coordinates.latitude
    fbRide.startingLng = this.starting_point.coordinates.longitude
    
    fbRide.destName = this.destination.name
    fbRide.destLat = this.destination.coordinates.latitude
    fbRide.destLng = this.destination.coordinates.longitude
    return fbRide
}
