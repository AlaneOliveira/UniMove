package com.dm.unimove.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.ui.HomePage
import com.dm.unimove.ui.MapPage
import com.dm.unimove.ui.ListPage

@Composable
fun MainNavHost(navController: NavHostController, viewModel: MainViewModel, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel) }
        composable<Route.List> { ListPage(viewModel = viewModel) }
        composable<Route.Map> { MapPage(viewModel = viewModel) }
    }
}