package com.example.tvmazeapiapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tvmazeapiapp.ui.state.TvShowListState
import com.example.tvmazeapiapp.ui.widgets.TvShowItem
import com.example.tvmazeapiapp.viewmodel.TvShowListEvent
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowListScreen(
    state: TvShowListState,
    onEvent: (TvShowListEvent) -> Unit,
    onShowClick: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    when (state) {
        is TvShowListState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is TvShowListState.Success -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(
                            text = "TV Maze Api"
                        ) }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            onEvent(TvShowListEvent.Search(query))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Search by title") },
                        singleLine = true,
                        placeholder = { Text("Enter show name...") },
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (state.shows.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No shows found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    else {
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.shows) { show ->
                                TvShowItem(
                                    show = show,
                                    onClick = {onShowClick(show.id)}
                                )
                            }
                        }
                    }
                }
            }
        }

        is TvShowListState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { onEvent(TvShowListEvent.Retry) }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}