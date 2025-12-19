package com.dm.unimove.db.fb

class FBRide {
    var name : String? = null
    var lat : Double? = null
    var lng : Double? = null
    fun toRide(): Ride {
        val latlng = if (lat!=null&&lng!=null) LatLng(lat!!, lng!!) else null
        return City(name!!, location = latlng)
    }
}
fun Ride.toFBRide() : FBRide {
    val fbCity = FBCity()
    fbCity.name = this.name
    fbCity.lat = this.location?.latitude ?: 0.0
    fbCity.lng = this.location?.longitude ?: 0.0
    return fbCity
}