package com.dm.unimove

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dm.unimove.ui.theme.CustomColors
import com.dm.unimove.ui.theme.Montserrat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AddRoad
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showDialog by remember { mutableStateOf(false) }
            val viewModel: MainViewModel by viewModels()
            val navController = rememberNavController()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {}
            )
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            UnimoveTheme {
                if (showDialog) CityDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { city ->
                        if (city.isNotBlank()) viewModel.add(city)
                        showDialog = false
                    },
                    onClick = { showDialog = true }
                )

                Scaffold(
                    // 1. TOP BAR
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = CustomColors.BrightPurple
                            ),
                            title = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "unimove",
                                        fontFamily = Montserrat,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 30.sp,
                                        color = CustomColors.LightBlue, // Usando LightBlue para contraste
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            // 2. Ícone para abrir o Drawer
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isOpen) {
                                            drawerState.close()
                                        } else drawerState.open()
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = Color.White
                                    )
                                }
                            },
                            actions = {
                                Box(modifier = Modifier.wrapContentSize()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo_unimove),
                                        contentDescription = "Logo Unimove",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(vertical = 0.dp, horizontal = 0.dp)
                                    )
                                }
                            }
                        )
                    },
                    // 3. BOTTOM BAR
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.MapButton,
                            BottomNavItem.ListButton
                        )

                        BottomNavBar(
                            navController = navController,
                            items = items,
                            containerColor = CustomColors.BrightPurple,
                            contentColor = CustomColors.White
                        )
                    }
                ) { innerPadding ->
                    // 4. CONTEÚDO PRINCIPAL: ModalNavigationDrawer
                    ModalNavigationDrawer(
                        gesturesEnabled = false,
                        drawerState = drawerState,
                        drawerContent = {
                            val targetColor = MaterialTheme.colorScheme.surface
                            val itemColors = NavigationDrawerItemDefaults.colors(
                                unselectedIconColor = targetColor,
                                unselectedTextColor = targetColor,

                                selectedIconColor = targetColor,
                                selectedTextColor = targetColor,

                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            ModalDrawerSheet(
                                modifier = Modifier.width(250.dp),
                                drawerContainerColor = CustomColors.LightBlue,
                                drawerContentColor = CustomColors.White
                            ) {
                                Spacer(Modifier.height(80.dp))

                                // ITENS DO DRAWER
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                                    label = { Text(text = "Perfil") },
                                    selected = false,
                                    onClick = { scope.launch { drawerState.close() /*TODO*/ } },
                                    colors = itemColors
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.AddRoad, contentDescription = "Criar carona") },
                                    label = { Text(text = "Criar carona") },
                                    selected = false,
                                    onClick = { scope.launch { drawerState.close() /*TODO*/ } },
                                    colors = itemColors
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Group, contentDescription = "Contatos salvos") },
                                    label = { Text(text = "Contatos salvos") },
                                    selected = false,
                                    onClick = { scope.launch { drawerState.close() /*TODO*/ } },
                                    colors = itemColors
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = DividerDefaults.color
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Logout, contentDescription = "Log Off") },
                                    label = { Text(text = "Log Off") },
                                    selected = false,
                                    onClick = {
                                        val context = this@MainActivity

                                        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        with(sharedPref.edit()) {
                                            putBoolean("remember_me", false)
                                            apply()
                                        }

                                        Firebase.auth.signOut()
                                        val intent = Intent(context, InitialActivity::class.java).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                        context.startActivity(intent)
                                    },
                                    colors = itemColors
                                )
                            }
                        }
                    ) {
                        // CONTEÚDO DO DRAWER: MainNavHost com o padding do Scaffold
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