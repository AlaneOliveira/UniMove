package com.dm.unimove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.ui.CityDialog
import com.dm.unimove.ui.nav.BottomNavBar
import com.dm.unimove.ui.nav.BottomNavItem
import com.dm.unimove.ui.nav.MainNavHost
import com.dm.unimove.ui.theme.UnimoveTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showDialog by remember { mutableStateOf(false) }
            val viewModel : MainViewModel by viewModels()
            val navController = rememberNavController()
            val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {} )
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            UnimoveTheme {
                if (showDialog) CityDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { city ->
                        if (city.isNotBlank()) viewModel.add(city)
                        showDialog = false
                    },
                    onClick = { showDialog = true })

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text("Unimove", modifier = Modifier.padding(16.dp))
                            Spacer(Modifier.height(16.dp))
                            NavigationDrawerItem(
                                label = { Text(text = "Perfil") },
                                selected = false,
                                onClick = { /*TODO*/ }
                            )
                            NavigationDrawerItem(
                                label = { Text(text = "Criar carona") },
                                selected = false,
                                onClick = { /*TODO*/ }
                            )
                            NavigationDrawerItem(
                                label = { Text(text = "Contatos salvos") },
                                selected = false,
                                onClick = { /*TODO*/ }
                            )
                            NavigationDrawerItem(
                                label = { Text(text = "Log Off") },
                                selected = false,
                                onClick = { Firebase.auth.signOut() }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("unimove") },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { /* TODO: Ação do perfil */ }) {
                                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                                    }
                                }
                            )
                        },

                        bottomBar = {
                            val items = listOf(
                                BottomNavItem.MapButton,
                                BottomNavItem.ListButton
                            )

                            BottomNavBar(navController = navController, items)

                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                            MainNavHost(navController = navController, viewModel = viewModel)
                        }
                    }
                }

            }
        }
    }
}