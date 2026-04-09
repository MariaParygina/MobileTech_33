package com.example.tvmazeapiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import com.example.tvmazeapiapp.data.api.TvMazeApi
import com.example.tvmazeapiapp.data.repository.TvShowRepository
import com.example.tvmazeapiapp.di.NetworkModule
import com.example.tvmazeapiapp.ui.screen.TvShowListScreen
import com.example.tvmazeapiapp.ui.theme.TVmazeApiAppTheme
import com.example.tvmazeapiapp.viewmodel.TvShowListEvent
import com.example.tvmazeapiapp.viewmodel.TvShowListViewModel
import com.example.tvmazeapiapp.viewmodel.TvShowListViewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tvmazeapiapp.ui.screen.TvShowDetailsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TVmazeApiAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TvShowApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

object ShowRoutes {
    const val LIST_ROUTE = "showList"
    const val DETAILS_ROUTE = "showDetails"
    const val SHOW_ID_ARG = "showId"

    const val DETAILS_ROUTE_PATTERN = "$DETAILS_ROUTE/{$SHOW_ID_ARG}"
    fun details(showId: Int): String = "$DETAILS_ROUTE/$showId"
}

@Composable
fun TvShowApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val api = NetworkModule.api.create(TvMazeApi::class.java)
    val repository = TvShowRepository(api)

    val viewModel: TvShowListViewModel = viewModel(
        factory = TvShowListViewModelFactory(repository)
    )

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(TvShowListEvent.LoadShows)
    }

    NavHost(
        navController = navController,
        startDestination = ShowRoutes.LIST_ROUTE
    ) {
        composable(ShowRoutes.LIST_ROUTE) {
            TvShowListScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onShowClick = { showId ->
                    navController.navigate(ShowRoutes.details(showId))
                }
            )
        }

        composable(
            route = ShowRoutes.DETAILS_ROUTE_PATTERN,
            arguments = listOf(
                navArgument(ShowRoutes.SHOW_ID_ARG) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val showId = backStackEntry.arguments
                ?.getInt(ShowRoutes.SHOW_ID_ARG)

            if (showId != null) {
                TvShowDetailsScreen(
                    id = showId,
                    repository = repository
                )
            }
        }
    }
}