package com.dm.unimove.ui.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    object Map : Route()
    @Serializable
    object Ride : Route()
    @Serializable
    object List : Route()
    @Serializable
    object CreateRide : Route()
    @Serializable
    data class RideDetails(val rideId: String) : Route()

    @Serializable
    data class RideSchedule(val rideId: String) : Route()

    @Serializable
    data class Confirmation(val seatId: String) : Route()
}