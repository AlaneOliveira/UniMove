package com.dm.unimove.ui.pages

import com.dm.unimove.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.Occasion
import com.dm.unimove.model.Ride
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RideHistoryItem(
    ride: Ride,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: Imagem do Carro (pode usar um recurso estático por enquanto)
        /* Image(
            painter = painterResource(id = R.drawable.ic_car_black), // Substitua pelo seu ícone
            contentDescription = "Carro",
            modifier = Modifier.size(80.dp)
        ) */

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Motorista: ${ride.vehicle_model}", // Ou buscar o nome via driver_ref
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Destino:",
                color = Color(0xFF6200EE), // Roxo do seu tema
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = ride.destination.name,
                color = Color(0xFF6200EE),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ponto de partida: ${ride.starting_point.name}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            // Formatação da data e ocasião
            val dateStr = ride.date_time?.toDate()?.let {
                SimpleDateFormat("dd/MM 'às' HH'h'mm", Locale("pt", "BR")).format(it)
            } ?: ""

            Text(
                text = "$dateStr, ${if(ride.occasion == Occasion.ONE_WAY) "somente ida" else "ida e volta"}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão "Mais informações"
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF8C9EFF), // Azul claro do wireframe
                modifier = Modifier.clickable { /* Abrir detalhes */ }
            ) {
                Text(
                    text = "••• Mais informações",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ListPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    // Observa a lista de caronas vinda do Firestore através da ViewModel
    val rideList by viewModel.availableRides

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Título da Seção
        Text(
            text = "Caronas anteriores",
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE)
        )

        Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(rideList) { ride ->
                RideHistoryItem(ride = ride)
                Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}