package com.dm.unimove.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dm.unimove.model.MainViewModel
import com.dm.unimove.ui.pages.MapPage
import com.dm.unimove.ui.pages.ListPage
import com.dm.unimove.ui.pages.RidePage
import com.dm.unimove.ui.pages.menu.CreateRidePage

@Composable
fun MainNavHost(navController: NavHostController, viewModel: MainViewModel, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Route.Map) {
        composable<Route.Map> { MapPage(viewModel = viewModel) }
        composable<Route.Ride> { RidePage(viewModel = viewModel) }
        composable<Route.List> { ListPage(viewModel = viewModel) }
        composable<Route.CreateRide> { CreateRidePage(viewModel = viewModel, navController = navController) }
    }
}