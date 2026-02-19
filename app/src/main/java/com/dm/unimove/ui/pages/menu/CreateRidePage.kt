package com.dm.unimove.ui.pages.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dm.unimove.model.Location
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.Occasion
import com.dm.unimove.model.PaymentType
import com.dm.unimove.model.Ride
import com.dm.unimove.model.RideStatus
import com.dm.unimove.ui.theme.CustomColors
import com.dm.unimove.ui.theme.Montserrat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.android.gms.maps.model.LatLng

@Composable
fun LocationSelector(
    label: String,
    locationName: String,
    onNameChange: (String) -> Unit,
    currentLatLng: LatLng,
    onLocationSelected: (LatLng) -> Unit
) {
    var showMapDialog by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = locationName,
            onValueChange = onNameChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            // Ícone que indica que o mapa pode ser aberto
            trailingIcon = {
                IconButton(onClick = { showMapDialog = true }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Abrir mapa", tint = CustomColors.BrightPurple)
                }
            }
        )

        if (showMapDialog) {
            MapSelectionDialog(
                initialLatLng = currentLatLng,
                onDismiss = { showMapDialog = false },
                onLocationConfirmed = {
                    onLocationSelected(it)
                    showMapDialog = false
                }
            )
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Criar carona",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        color = CustomColors.BrightPurple,
                        fontSize = 20.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        // CORREÇÃO: O LazyColumn deve envolver TODOS os items até o final da página
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // CORREÇÃO: Usando o padding do Scaffold
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                LocationSelector(
                    label = "Local de Destino",
                    locationName = destLocationName,
                    onNameChange = { destLocationName = it },
                    currentLatLng = destLatLng,
                    onLocationSelected = { destLatLng = it }
                )
            }

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
                // Diálogos de Data e Hora
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showDatePicker = false
                                showTimePicker = true
                            }) { Text("Confirmar Data") }
                        }
                    ) { DatePicker(state = datePickerState) }
                }

                if (showTimePicker) {
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val calendar = java.util.Calendar.getInstance()
                                datePickerState.selectedDateMillis?.let { calendar.timeInMillis = it }
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

                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F0F5), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    val displayDate = selectedTimestamp?.toDate()?.let {
                        java.text.SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", java.util.Locale.getDefault()).format(it)
                    } ?: "Selecionar Data e Hora"
                    Text(displayDate)
                }
            }

            // SWITCH DE OCASIÃO (Ida e Volta / Somente Ida)
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Occasion.entries.forEach { occ ->
                        val label = when (occ) {
                            Occasion.ONE_WAY -> "Somente Ida"
                            Occasion.ROUND_TRIP -> "Ida e Volta"
                        }
                        Button(
                            onClick = { selectedOccasion = occ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedOccasion == occ) Color(0xFFE8E0FF) else Color.White,
                                contentColor = Color.Black
                            ),
                            shape = if (occ == Occasion.ONE_WAY)
                                RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                            else RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            if (selectedOccasion == occ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(label)
                        }
                    }
                }
            }

            // SWITCH DE PAGAMENTO (Cortesia / A negociar / A pagar)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    PaymentType.entries.forEach { pay ->
                        val label = when (pay) {
                            PaymentType.FREE -> "Cortesia"
                            PaymentType.PAY -> "A pagar"
                            PaymentType.NEGOTIABLE -> "A negociar"
                        }
                        Button(
                            onClick = { selectedPayment = pay },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedPayment == pay) Color(0xFFE8E0FF) else Color.White,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Text(label, fontSize = 12.sp)
                        }
                    }
                }

                if (selectedPayment == PaymentType.PAY) {
                    Spacer(Modifier.height(8.dp))
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
            }

            item {
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
                        val userUid = FirebaseAuth.getInstance().currentUser?.uid
                        if (userUid != null && viewModel.canUserStartNewActivity()) {
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
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CustomColors.BrightPurple)
                ) {
                    Text("Publicar Carona", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } // FIM DO LazyColumn
    }
}