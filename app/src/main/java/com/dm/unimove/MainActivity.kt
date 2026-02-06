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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddRoad
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.ui.nav.BottomNavBar
import com.dm.unimove.ui.nav.BottomNavItem
import com.dm.unimove.ui.nav.MainNavHost
import com.dm.unimove.ui.nav.Route
import com.dm.unimove.ui.theme.CustomColors
import com.dm.unimove.ui.theme.Montserrat
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
            val viewModel: MainViewModel by viewModels()
            val navController = rememberNavController()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {}
            )
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val currentUser = Firebase.auth.currentUser
            LaunchedEffect(currentUser) {
                currentUser?.let {
                    viewModel.loadUserProfile(it.uid)
                }
            }

            UnimoveTheme {
                Scaffold(
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
                                        color = CustomColors.LightBlue,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
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
                                Box(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .offset(x = 12.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo_unimove),
                                        contentDescription = "Logo Unimove",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .size(65.dp)
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.MapButton,
                            BottomNavItem.RideButton,
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
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            navController.navigate(Route.CreateRide) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
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