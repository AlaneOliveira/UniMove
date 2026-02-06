package com.dm.unimove.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.Ride
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import androidx.compose.foundation.lazy.items

@Preview
@Composable
fun RidePage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val activeRideData by viewModel.activeRide
    val solicitations by viewModel.pendingSolicitations
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Gatilho para buscar dados ao abrir a página
    LaunchedEffect(currentUser) {
        currentUser?.let { viewModel.fetchActiveRide(it.uid) }
    }

    if (activeRideData == null) {
        // ESTADO DEFAULT: Nenhuma carona
        EmptyRideState(modifier)
    } else {
        val (rideId, ride) = activeRideData!!

        // Verifica se o usuário logado é o motorista da carona encontrada
        val isDriver = ride.driver_ref?.id == currentUser?.uid

        if (isDriver) {
            DriverInterface(
                ride = ride,
                solicitations = solicitations,
                viewModel = viewModel,
                rideId = rideId // Passando o ID capturado acima
            )
        } else {
            PassengerInterface(ride)
        }
    }
}

@Composable
fun SolicitationItem(
    solicId: String,
    data: Map<String, Any>,
    rideId: String,
    viewModel: MainViewModel
) {
    val passengerRef = data["passenger_ref"] as? DocumentReference
    val passengerId = passengerRef?.id ?: ""

    // Idealmente, você buscaria o nome do passageiro aqui ou no ViewModel
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Passageiro: ${passengerId.take(5)}...") // Exemplo com ID curto

        Row {
            IconButton(onClick = { viewModel.rejectPassenger(solicId, passengerId) }) {
                Icon(Icons.Default.Close, contentDescription = "Rejeitar", tint = Color.Red)
            }
            IconButton(onClick = { viewModel.acceptPassenger(solicId, rideId, passengerId) }) {
                Icon(Icons.Default.Check, contentDescription = "Aceitar", tint = Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun EmptyRideState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.LightGray
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Você não possui caronas agendadas!",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun PassengerInterface(ride: Ride) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Sua Carona (Passageiro)", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Destino: ${ride.destination.name}", fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
                Text("Partida: ${ride.starting_point.name}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Modelo: ${ride.vehicle_model}")
                Text("Status da Carona: ${ride.status}")
            }
        }
        // Botão para cancelar ou falar com motorista (opcional)
    }
}

@Composable
fun DriverInterface(
    ride: Ride,
    solicitations: List<Pair<String, Map<String, Any>>>,
    viewModel: MainViewModel,
    rideId: String
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Sua Carona Ativa (Motorista)", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(text = "Destino: ${ride.destination.name}", color = Color(color = 0xFF6200EE))

        Spacer(Modifier.height(16.dp))
        Text(text = "Solicitações Pendentes:", fontWeight = FontWeight.SemiBold)

        LazyColumn {
            items(solicitations) { (solicId, data) ->
                SolicitationItem(
                    solicId = solicId,
                    data = data,
                    rideId = rideId, // Agora passamos o ID da carona corretamente
                    viewModel = viewModel
                )
            }
        }
    }
}