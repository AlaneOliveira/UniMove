package com.dm.unimove.ui.pages.ridemodal

import com.dm.unimove.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dm.unimove.ui.nav.Route


@Composable
fun RideSchedulePage(ride: Route.Ride, navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Selecione seu assento", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        InteractiveCarMap(
            ride = ride,
            isReadOnly = false,
            onSeatSelected = { seatKey ->
                // Ao selecionar, navega para a página de confirmação passando o assento
                navController.navigate("confirmation/${seatKey}")
            }
        )
    }
}

@Composable
fun InteractiveCarMap(
    ride: Route.Ride,
    isReadOnly: Boolean,
    onSeatSelected: (String) -> Unit
) {
    var selectedSeatKey by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.size(320.dp, 450.dp), contentAlignment = Alignment.Center) {
        // 1. Base do Carro (car_detailspage.svg)
        Image(
            painter = painterResource(id = R.drawable.car_detailspage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Sobreposição - POSICIONAMENTO CORRIGIDO
        // Motorista (Frente Esquerda - Geralmente)
        SeatIcon(
            label = "motorista",
            occupant = ride.driver_ref,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 75.dp, top = 160.dp),
            isSelected = false,
            onCLick = {}
        )

        // Carona Frente (Frente Direita)
        SeatIcon(
            label = "carona-frente",
            occupant = ride.seats_map["carona-frente"],
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 75.dp, top = 160.dp),
            isSelected = selectedSeatKey == "carona-frente",
            onCLick = {
                if (!isReadOnly) {
                    selectedSeatKey = "carona-frente"
                    onSeatSelected("carona-frente")
                }
            }
        )

        // Assentos Traseiros (Ajuste os paddings conforme seu SVG)
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val trasKeys = listOf("carona-trás-esquerda", "carona-trás-meio", "carona-trás-direita")
            trasKeys.forEach { key ->
                SeatIcon(
                    label = key,
                    occupant = ride.seats_map[key],
                    modifier = Modifier,
                    isSelected = selectedSeatKey == key,
                    onCLick = {
                        if (!isReadOnly) {
                            selectedSeatKey = key
                            onSeatSelected(key)
                        }
                    }
                )
            }
        }
    }
}