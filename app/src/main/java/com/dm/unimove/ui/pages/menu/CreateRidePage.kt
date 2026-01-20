package com.dm.unimove.ui.pages.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dm.unimove.model.Location
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.Occasion
import com.dm.unimove.model.PaymentType
import com.dm.unimove.model.Ride
import com.dm.unimove.model.RideStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreateRidePage(viewModel: MainViewModel, navController: NavController) {
    var startLocationName by remember { mutableStateOf("") }
    var startCoords by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }

    var destLocationName by remember { mutableStateOf("") }
    var destCoords by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }

    var selectedTimestamp by remember { mutableStateOf<Timestamp?>(null) }
    var selectedOccasion by remember { mutableStateOf(Occasion.ONE_WAY) }
    var selectedPayment by remember { mutableStateOf(PaymentType.FREE) }
    var rideValue by remember { mutableStateOf("0.0") }

    var totalSeats by remember { mutableStateOf("4") }
    var vehicleModel by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("Criar Nova Carona") }

        item {
            OutlinedTextField(
                value = startLocationName,
                onValueChange = { startLocationName = it },
                label = { Text("Ponto de Partida") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = destLocationName,
                onValueChange = { destLocationName = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(onClick = { /* Abrir DatePickerDialog */ }) {
                Text(selectedTimestamp?.toDate()?.toString() ?: "Selecionar Data e Hora")
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Occasion.entries.forEach { occ ->
                    FilterChip(
                        selected = selectedOccasion == occ,
                        onClick = { selectedOccasion = occ },
                        label = { Text(occ.name) }
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentType.entries.forEach { pay ->
                    FilterChip(
                        selected = selectedPayment == pay,
                        onClick = { selectedPayment = pay },
                        label = { Text(pay.name) }
                    )
                }
            }
            if (selectedPayment == PaymentType.PAID) {
                OutlinedTextField(
                    value = rideValue,
                    onValueChange = { rideValue = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            OutlinedTextField(
                value = vehicleModel,
                onValueChange = { vehicleModel = it },
                label = { Text("Modelo do Veículo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = totalSeats,
                onValueChange = { totalSeats = it },
                label = { Text("Total de Vagas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição/Recados") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
        }

        item {
            Button(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    val currentUser = auth.currentUser
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    val driverRef = db.collection("USERS").document(currentUser!!.uid)
                    val newRide = Ride(
                        driver_ref = driverRef, // Referência correta inserida aqui
                        starting_point = Location(startLocationName, startCoords),
                        destination = Location(destLocationName, destCoords),
                        date_time = selectedTimestamp ?: com.google.firebase.Timestamp.now(),
                        occasion = selectedOccasion,
                        payment_type = selectedPayment,
                        ride_value = rideValue.toDoubleOrNull() ?: 0.0,
                        total_seats = totalSeats.toIntOrNull() ?: 0,
                        vehicle_model = vehicleModel,
                        description = description,
                        status = RideStatus.AVAILABLE,
                        seats_map = emptyMap()
                    )
                    viewModel.createNewRide(newRide)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar Carona")
            }
        }
    }
}