package com.dm.unimove.ui.pages.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dm.unimove.model.Location
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.Occasion
import com.dm.unimove.model.PaymentType
import com.dm.unimove.model.Ride
import com.dm.unimove.model.RideStatus
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationSelector(
    label: String,
    locationName: String,
    onNameChange: (String) -> Unit,
    currentLatLng: LatLng,
    onLocationSelected: (LatLng) -> Unit
) {
    Column {
        OutlinedTextField(
            value = locationName,
            onValueChange = onNameChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
            },
            onMapClick = onLocationSelected // Captura lat/long no clique
        ) {
            Marker(state = MarkerState(position = currentLatLng))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRidePage(viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current

    var startLocationName by remember { mutableStateOf("") }
    var startCoords by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }
    var startLatLng by remember { mutableStateOf(LatLng(-8.0476, -34.8770)) } // Recife como padrão

    var destLocationName by remember { mutableStateOf("") }
    var destCoords by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }
    var destLatLng by remember { mutableStateOf(LatLng(-8.0476, -34.8770)) }

    var selectedTimestamp by remember { mutableStateOf<Timestamp?>(null) }
    var selectedOccasion by remember { mutableStateOf(Occasion.ONE_WAY) }
    var selectedPayment by remember { mutableStateOf(PaymentType.FREE) }
    var rideValue by remember { mutableStateOf("0.0") }

    var totalSeats by remember { mutableStateOf("4") }
    var vehicleModel by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("Criar Nova Carona") }

        item {
            LocationSelector(
                label = "Ponto de Partida",
                locationName = startLocationName,
                onNameChange = { startLocationName = it },
                currentLatLng = startLatLng,
                onLocationSelected = { startLatLng = it }
            )
        }

        item {
            LocationSelector(
                label = "Destino",
                locationName = destLocationName,
                onNameChange = { destLocationName = it },
                currentLatLng = destLatLng,
                onLocationSelected = { destLatLng = it }
            )
        }

        item {
            // 1. Diálogo de Data
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            showTimePicker = true // AQUI: Chama o próximo diálogo
                        }) { Text("Confirmar Data") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // 2. Diálogo de Hora
            if (showTimePicker) {
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val calendar = java.util.Calendar.getInstance()
                            // Usa a data que já foi selecionada no passo anterior
                            datePickerState.selectedDateMillis?.let { calendar.timeInMillis = it }
                            // Adiciona a hora e minuto do TimePicker
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                            calendar.set(java.util.Calendar.MINUTE, timePickerState.minute)

                            selectedTimestamp = Timestamp(calendar.time)
                            showTimePicker = false
                        }) { Text("OK") }
                    },
                    title = { Text("Selecione o Horário") },
                    text = { TimePicker(state = timePickerState) }
                )
            }

            // Botão que inicia o processo
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val displayDate = selectedTimestamp?.toDate()?.let {
                    java.text.SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", java.util.Locale.getDefault()).format(it)
                } ?: "Selecionar Data e Hora"
                Text(displayDate)
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
            if (selectedPayment == PaymentType.PAY) {
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
                    if (viewModel.canUserStartNewActivity()) {
                        val userUid = FirebaseAuth.getInstance().currentUser?.uid
                        if (userUid != null) {
                            viewModel.updateUserBusyStatus(userUid, true)

                            val newRide = Ride(
                                driver_ref = FirebaseFirestore.getInstance().collection("USERS").document(userUid),
                                starting_point = Location(startLocationName, GeoPoint(startLatLng.latitude, startLatLng.longitude)),
                                destination = Location(destLocationName, GeoPoint(destLatLng.latitude, destLatLng.longitude)),
                                date_time = selectedTimestamp ?: Timestamp.now(),
                                occasion = selectedOccasion,
                                payment_type = selectedPayment,
                                ride_value = rideValue.toDoubleOrNull() ?: 0.0,
                                total_seats = totalSeats.toIntOrNull() ?: 0,
                                vehicle_model = vehicleModel,
                                description = description,
                                status = RideStatus.AVAILABLE
                            )
                            viewModel.createNewRide(newRide)
                            navController.popBackStack() // Volta para o mapa
                        }
                    } else {
                        android.widget.Toast.makeText(context, "Você já possui uma carona em andamento!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar Carona")
            }
        }
    }
}
