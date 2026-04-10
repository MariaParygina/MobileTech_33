package com.example.tvmazeapiapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tvmazeapiapp.ui.state.TvShowListState
import com.example.tvmazeapiapp.ui.widgets.TvShowItem
import com.example.tvmazeapiapp.viewmodel.TvShowListEvent
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.tvmazeapiapp.ui.state.list.TvShowListEmpty
import com.example.tvmazeapiapp.ui.state.list.TvShowListError
import com.example.tvmazeapiapp.ui.state.list.TvShowListLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowListScreen(
    state: TvShowListState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onEvent: (TvShowListEvent) -> Unit,
    onShowClick: (Int) -> Unit
) {
    when (state) {
        is TvShowListState.Loading -> {
            TvShowListLoading()
        }

        is TvShowListState.Success -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "TV Maze Api") }
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
                            onSearchQueryChange(query)
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
                        TvShowListEmpty()
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.shows) { show ->
                                TvShowItem(
                                    show = show,
                                    onClick = { onShowClick(show.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        is TvShowListState.Error -> {
            TvShowListError(
                state = state,
                onEvent = onEvent
            )
        }
    }
}