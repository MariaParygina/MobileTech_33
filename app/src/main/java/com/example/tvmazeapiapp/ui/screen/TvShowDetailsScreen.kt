package com.example.tvmazeapiapp.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import com.example.tvmazeapiapp.data.repository.TvShowRepository
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModelFactory

@Composable
fun TvShowDetailsScreen(
    id: Int,
    repository: TvShowRepository,
    viewModel: TvShowDetailsViewModel = viewModel(
        factory = TvShowDetailsViewModelFactory(repository)
    )
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadShow(id)
    }

    state?.let { show ->
        Text(text = show.name)
        //Text(text = show.summary ?: "")
    }
}