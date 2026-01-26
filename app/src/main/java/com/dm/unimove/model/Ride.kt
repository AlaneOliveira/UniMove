package com.dm.unimove.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

/*
SUMÁRIO:
RideStatus (Status mutável da corrida em tempo real, para disponibilidade, histórico de corridas e manipulação do banco de dados):
- AVAILABLE: Corrida disponível (aparece no mapa);
- CANCELED: Corida cancelada (não aparece no mapa e aparece no histórico do usuário);
- ON_GOING: Corrida iniciada (não aparece no mapa);
- FINISHED: Corrida iniciada (não aparece no mapa e aparece no histórico do usuário);

Occasion (Define o comportamento da corrida, por enquanto fica só como informaçao basica):
- ONE-WAY: Corrida de somente ida
- ROUND-TRIP: Corrida de ida e volta
*/
// TODO: UM DETALHE QUE ESQUECI DE IMPLEMENTAR É A LÓGICA DE FLUXO DE UMA CARONA DE IDA E VOLTA

enum class RideStatus { AVAILABLE, CANCELED, ON_GOING, FINISHED }
enum class PaymentType { PAY, NEGOTIABLE, FREE }
enum class Occasion { ONE_WAY, ROUND_TRIP }

data class Location(
    val name: String = "",
    val coordinates: GeoPoint = GeoPoint(0.0, 0.0)
)

data class Ride(
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