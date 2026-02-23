package com.dm.unimove.model

enum class SeatStatus { AVAILABLE, OCCUPIED, SELECTED }

data class CarSeat(
    val id: Int,
    val status: SeatStatus,
    val label: String
)