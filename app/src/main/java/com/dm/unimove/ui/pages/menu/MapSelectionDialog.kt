package com.dm.unimove.ui.pages.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dm.unimove.ui.theme.CustomColors
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapSelectionDialog(
    initialLatLng: LatLng,
    onDismiss: () -> Unit,
    onLocationConfirmed: (LatLng) -> Unit
) {
    var tempLatLng by remember { mutableStateOf(initialLatLng) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Garante tela cheia
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(tempLatLng, 15f)
                    },
                    onMapClick = { tempLatLng = it } // Atualiza o marcador temporário
                ) {
                    Marker(state = MarkerState(position = tempLatLng))
                }

                // Botões de ação sobrepostos
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
                ) {
                    Button(
                        onClick = { onLocationConfirmed(tempLatLng) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CustomColors.BrightPurple)
                    ) {
                        Text("Confirmar Localização")
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            }
        }
    }
}