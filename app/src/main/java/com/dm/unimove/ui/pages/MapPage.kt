package com.dm.unimove.ui.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.dm.unimove.model.Location
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.model.Ride
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val camPosState = rememberCameraPositionState()
    val context = LocalContext.current

    // Pegamos a lista de caronas disponíveis do ViewModel
    val rides by viewModel.availableRides

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

    // Efeito para centralizar no usuário ao abrir o mapa
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    camPosState.move(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                }
            }
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        onMapClick = { latLng ->
            // Exemplo: Criando uma nova carona ao clicar no mapa (ajuste conforme sua lógica de UI)
            val newRide = Ride(
                starting_point = Location(
                    name = "Carona em ${latLng.latitude.toString().take(5)}",
                    coordinates = GeoPoint(latLng.latitude, latLng.longitude)
                )
            )
            viewModel.createNewRide(newRide)
        },
        cameraPositionState = camPosState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true)
    ) {
        // Renderiza os marcadores de todas as caronas disponíveis
        rides.forEach { ride ->
            val position = LatLng(
                ride.starting_point.coordinates.latitude,
                ride.starting_point.coordinates.longitude
            )
            Marker(
                state = MarkerState(position = position),
                title = ride.starting_point.name,
                snippet = "Toque para ver detalhes"
            )
        }
    }
}
