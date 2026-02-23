package com.dm.unimove.ui.pages.ridemodal

import com.dm.unimove.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.PaymentType
import com.dm.unimove.model.Ride
import com.dm.unimove.ui.theme.CustomColors

// ─────────────────────────────────────────────
// COMPONENTES COMPARTILHADOS
// ─────────────────────────────────────────────

@Composable
fun SeatIcon(
    label: String,
    occupant: Any?,
    modifier: Modifier,
    isSelected: Boolean,
    onCLick: () -> Unit
) {
    val isOccupied = occupant != null
    val imageRes = if (isOccupied) R.drawable.occupied_seat else R.drawable.empty_seat

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = label,
        colorFilter = if (isSelected) ColorFilter.tint(Color.Green, BlendMode.SrcAtop) else null,
        modifier = modifier
            .size(60.dp)
            .clickable(enabled = !isOccupied) { onCLick() }
    )
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(16.dp), shape = CircleShape, color = color) {}
        Spacer(Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
    }
}

// ─────────────────────────────────────────────
// MAPA INTERATIVO DO CARRO
// ─────────────────────────────────────────────

/**
 * Layout do carro baseado no wireframe:
 *
 *   [Motorista esq.]   [Carona frente dir.]
 *   [trás-esq.] [trás-meio] [trás-dir.]
 *
 * O motorista fica sempre no lado ESQUERDO (posição brasileira: volante à esquerda).
 */
@Composable
fun InteractiveCarMap(
    ride: Ride,
    viewModel: MainViewModel,
    navController: NavController,
    isReadOnly: Boolean = false,
    onSeatSelected: (String) -> Unit
) {
    var selectedSeatKey by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.size(320.dp, 450.dp), contentAlignment = Alignment.Center) {

        // Base do carro
        Image(
            painter = painterResource(id = R.drawable.car_detailspage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // ── Assento do Motorista — ESQUERDA (sempre ocupado) ──
        SeatIcon(
            label = "motorista",
            occupant = ride.driver_ref,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 75.dp, top = 160.dp),
            isSelected = false,
            onCLick = {}
        )

        // ── Carona Frente — DIREITA ──
        SeatIcon(
            label = "carona-frente",
            occupant = ride.seats_map["carona-frente"],
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 75.dp, top = 160.dp),
            isSelected = selectedSeatKey == "carona-frente",
            onCLick = {
                if (!isReadOnly) {
                    selectedSeatKey = "carona-frente"
                    viewModel.selectSeat("carona-frente")
                    onSeatSelected("carona-frente")
                    navController.navigate("confirmation/carona-frente")
                }
            }
        )

        // ── Assentos Traseiros — da ESQUERDA para DIREITA ──
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Ordem correta: esquerda → meio → direita
            val trasKeys = listOf(
                "carona-trás-esquerda",
                "carona-trás-meio",
                "carona-trás-direita"
            )
            trasKeys.forEach { key ->
                SeatIcon(
                    label = key,
                    occupant = ride.seats_map[key],
                    modifier = Modifier,
                    isSelected = selectedSeatKey == key,
                    onCLick = {
                        if (!isReadOnly) {
                            selectedSeatKey = key
                            viewModel.selectSeat(key)
                            onSeatSelected(key)
                            navController.navigate("confirmation/${key}")
                        }
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// MORE INFO PAGE — Layout igual ao wireframe
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreInfoPage(
    ride: Ride,
    navController: NavController,
    viewModel: MainViewModel
) {
    var driverName by remember { mutableStateOf("Carregando...") }
    LaunchedEffect(ride.driver_ref) {
        ride.driver_ref?.get()?.addOnSuccessListener { snapshot ->
            driverName = snapshot.getString("name") ?: "Motorista Desconhecido"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mais informações",
                        color = CustomColors.BrightPurple,
                        fontWeight = FontWeight.Bold
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Cabeçalho: ícone + nome + modelo + valor (horizontal, igual wireframe) ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circular
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF3E5F5),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color(0xFF9575CD),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Dados do motorista — coluna à direita do avatar
                Column {
                    Text(
                        text = driverName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Modelo do carro:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF6200EE)
                    )
                    Text(
                        text = ride.vehicle_model.ifBlank { "Não informado" },
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                    val valorText = when (ride.payment_type) {
                        PaymentType.FREE -> "Cortesia"
                        PaymentType.NEGOTIABLE -> "A negociar"
                        PaymentType.PAY -> "R$ ${"%.2f".format(ride.ride_value)}"
                    }
                    Text(
                        text = "Valor: $valorText",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Mapa do carro (somente leitura) ──
            InteractiveCarMap(
                ride = ride,
                viewModel = viewModel,
                navController = navController,
                isReadOnly = true,
                onSeatSelected = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Legenda ──
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                LegendItem("Ocupado", Color(0xFF424242))
                LegendItem("Disponível", Color(0xFF9575CD))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Card de Descrição (igual wireframe) ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Descrição:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF6200EE)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ride.description.ifBlank { "Nenhuma." },
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────
// RIDE SCHEDULE PAGE
// ─────────────────────────────────────────────

@Composable
fun RideSchedulePage(
    ride: Ride,
    navController: NavController,
    viewModel: MainViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Selecione seu assento",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF6200EE)
        )

        InteractiveCarMap(
            ride = ride,
            viewModel = viewModel,
            navController = navController,
            isReadOnly = false,
            onSeatSelected = { seatKey ->
                viewModel.selectSeat(seatKey)
                navController.navigate("confirmation/${seatKey}")
            }
        )
    }
}