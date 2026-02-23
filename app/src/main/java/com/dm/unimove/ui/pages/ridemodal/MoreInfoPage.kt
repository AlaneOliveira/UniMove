package com.dm.unimove.ui.pages.ridemodal

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dm.unimove.model.CarSeat
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.SeatStatus
import com.dm.unimove.ui.nav.Route

@Composable
fun CarSeatPicker(
    seats: List<CarSeat>,
    onSeatClick: (CarSeat) -> Unit
) {
    // Definimos as cores baseadas no seu design
    val availableColor = Color(0xFF9575CD) // Lilás
    val occupiedColor = Color(0xFF424242)  // Cinza escuro
    val carBodyColor = Color(0xFF1A237E)   // Azul Marinho Profundo

    Box(modifier = Modifier.size(300.dp, 500.dp)) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Lógica para detectar em qual assento o usuário clicou
                    // comparando o offset com as áreas desenhadas
                }
            }
        ) {
            // 1. Desenha o corpo do carro
            drawRoundRect(
                color = carBodyColor,
                size = size,
                cornerRadius = CornerRadius(40f, 40f)
            )

            // 2. Desenha os assentos (Exemplo simplificado)
            // Assento Motorista (Sempre ocupado)
            drawSeat(occupiedColor, x = 0.2f, y = 0.5f)

            // Assentos Passageiros
            seats.forEach { seat ->
                val color = when(seat.status) {
                    SeatStatus.AVAILABLE -> availableColor
                    SeatStatus.OCCUPIED -> occupiedColor
                    SeatStatus.SELECTED -> Color.Green // Feedback visual de seleção
                }
                // Lógica de posicionamento baseada no ID do assento
            }
        }
    }
}

@Composable
fun MoreInfoPage(ride: Route.Ride, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ProfileImage() // Círculo lilás com ícone
            Column {
                Text(text = "Fulano de tal", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text(text = "Modelo do carro: ${ride.vehicle_model}", color = Color(0xFF6200EE))
                Text(text = "Valor: ${ride.payment_type}", fontWeight = FontWeight.SemiBold)
            }
        }

        // Mapa de Assentos apenas para visualização (clique desativado)
        InteractiveCarMap(
            ride = ride,
            onSeatSelected = {}, // Passa vazio para não ter interação
            isReadOnly = true
        )

        // Legenda e Descrição
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendItem("Ocupado", Color.DarkGray)
            LegendItem("Disponível", Color(0xFF9575CD))
        }
    }
}
