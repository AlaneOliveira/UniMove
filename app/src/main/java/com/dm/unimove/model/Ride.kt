package com.dm.unimove.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

enum class RideStatus { AVAILABLE, CANCELED, ON_GOING, FINISHED }
enum class PaymentType { PAY, NEGOTIABLE, FREE }
enum class Occasion { ONE_WAY, ROUND_TRIP }

data class Location(
    val name: String = "",
    val coordinates: GeoPoint = GeoPoint(0.0, 0.0)
)

data class Ride(
    // Campo 'id' preenchido manualmente após leitura do Firestore (não é salvo no documento)
    @field:com.google.firebase.firestore.Exclude
    var id: String = "",

    val driver_ref: DocumentReference? = null,
    val passenger_refs: List<DocumentReference> = emptyList(),

    val starting_point: Location = Location(),
    val destination: Location = Location(),

    val date_time: Timestamp? = null,

    val occasion: Occasion = Occasion.ONE_WAY,
    val payment_type: PaymentType = PaymentType.FREE,
    val ride_value: Double = 0.0,
    val status: RideStatus = RideStatus.AVAILABLE,

    val seats_map: Map<String, DocumentReference?> = emptyMap(),
    val total_seats: Int = 0,

    val vehicle_model: String = "",
    val description: String = ""
)