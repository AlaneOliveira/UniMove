package com.dm.unimove.ui.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.PaymentType
import com.dm.unimove.model.Ride
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.fetchAvailableRides()
    }

    val context = LocalContext.current
    val rides by viewModel.availableRides
    val RECIFE_FALLBACK = LatLng(-8.0631, -34.8711)
    var selectedRideForDetail by remember { mutableStateOf<Ride?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val camPosState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(RECIFE_FALLBACK, 12f)
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 3. Gerenciamento de Localização: Se tiver permissão, foca no usuário.
    // Se não, mantém no Recife.
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    camPosState.move(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                } else {
                    camPosState.move(CameraUpdateFactory.newLatLngZoom(RECIFE_FALLBACK, 12f))
                }
            }
        } else {
            camPosState.move(CameraUpdateFactory.newLatLngZoom(RECIFE_FALLBACK, 12f))
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        // REMOVIDO: onMapClick que criava caronas por engano
        cameraPositionState = camPosState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true)
    ) {
        // 4. Renderiza as caronas disponíveis com o Pin Azul
        rides.forEach { ride ->
            val position = LatLng(
                ride.starting_point.coordinates.latitude,
                ride.starting_point.coordinates.longitude
            )
            Marker(
                state = MarkerState(position = position),
                title = "Partida: ${ride.starting_point.name}",
                snippet = "Destino: ${ride.destination.name} | Vagas: ${ride.total_seats}",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                onInfoWindowClick = {
                    selectedRideForDetail = ride
                    showBottomSheet = true
                }
            )
        }
    }

    if (showBottomSheet && selectedRideForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFFD1C4E9)
                    )

                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        // TODO: Buscar o nome real via ride.driver_ref futuramente
                        Text(text = "Motorista: Carona Disponível", fontWeight = FontWeight.Bold)

                        // Valor Dinâmico do Destino
                        Text(
                            text = "Destino: ${selectedRideForDetail!!.destination.name}",
                            color = Color(0xFF6200EE),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Valor Dinâmico do Ponto de Partida
                Text(text = "Ponto de partida: ${selectedRideForDetail!!.starting_point.name}")

                // Formatação Dinâmica de Data e Hora
                val dataHora = selectedRideForDetail!!.date_time?.toDate()?.let {
                    java.text.SimpleDateFormat("dd/MM 'às' HH:mm", java.util.Locale.getDefault()).format(it)
                } ?: "Data não definida"

                Text(text = "$dataHora, ${selectedRideForDetail!!.occasion.name.lowercase().replace("_", " ")}")

                // Exibe o valor se não for cortesia
                if (selectedRideForDetail!!.payment_type == PaymentType.PAY) {
                    Text(text = "Valor: R$ ${selectedRideForDetail!!.ride_value}", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão Mais Informações
                Button(
                    onClick = { /* Navegar para página de detalhes completa */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF82B1FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("... Mais informações")
                }

                // Botão Agendar (Agora enviando os dados reais)
                Button(
                    onClick = {
                        if (viewModel.canUserStartNewActivity()) {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser != null) {
                                viewModel.sendRideSolicitation(selectedRideForDetail!!, currentUser.uid)
                                showBottomSheet = false
                            }
                        } else {
                            Toast("Você já possui uma carona em andamento!")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agendar carona")
                }
            }
        }
    }

}