package com.dm.unimove.db.fb

import com.google.firebase.Timestamp

class FBSolicitation {
    var id: String? = null
    var passengerId: String? = null
    var driverId: String? = null
    var rideId: String? = null
    var status: String = "PENDING" // PENDING, ACCEPTED, REJECTED
    var timestamp: Timestamp? = null
}