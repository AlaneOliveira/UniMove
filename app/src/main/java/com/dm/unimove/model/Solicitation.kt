package com.dm.unimove.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ServerTimestamp

enum class SolicitationStatus { PENDING, ACCEPTED, REJECTED }
enum class SeatPosition { FRONT, BACK_LEFT, BACK_MIDDLE, BACK_RIGHT }

data class Solicitation(
    val ride_ref: DocumentReference? = null,
    val passenger_ref: DocumentReference? = null,
    val driver_ref: DocumentReference? = null,
    val requested_seat: SeatPosition = SeatPosition.FRONT,
    val status: SolicitationStatus = SolicitationStatus.PENDING,

    @ServerTimestamp
    val timestamp: Timestamp? = null
)