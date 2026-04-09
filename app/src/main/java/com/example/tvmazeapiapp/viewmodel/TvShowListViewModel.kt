package com.example.tvmazeapiapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeapiapp.data.repository.TvShowRepository
import com.example.tvmazeapiapp.ui.state.TvMazeUiState
import com.example.tvmazeapiapp.ui.state.TvShowListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue

sealed class TvShowListEvent {
    object LoadShows : TvShowListEvent()
    data class Search(val query: String) : TvShowListEvent()
    object Refresh : TvShowListEvent()
    object Retry : TvShowListEvent()
}

class TvShowListViewModel(
    private val repository: TvShowRepository
) : ViewModel() {
    private val _state = MutableStateFlow<TvShowListState>(TvShowListState.Loading)
    val state: StateFlow<TvShowListState> = _state.asStateFlow()

    var uiState by mutableStateOf(TvMazeUiState())
        private set

    private var currentQuery: String? = null

    fun onEvent(event: TvShowListEvent) {
        when (event) {
            is TvShowListEvent.LoadShows -> loadShows()
            is TvShowListEvent.Search -> searchShows(event.query)
            is TvShowListEvent.Refresh -> refresh()
            is TvShowListEvent.Retry -> retry()
        }
    }

    private fun loadShows() {
        viewModelScope.launch {
            _state.value = TvShowListState.Loading
            currentQuery = null

            val result = repository.getShows()
            if (result.isSuccess) {
                _state.value = TvShowListState.Success(result.getOrNull() ?: emptyList())
            } else {
                _state.value = TvShowListState.Error(result.exceptionOrNull()?.message ?: "Неизвестная ошибка")
            }
        }
    }

    private fun searchShows(query: String) {
        if (query.isBlank()) {
            loadShows()
            return
        }

        viewModelScope.launch {
            currentQuery = query

            val result = repository.searchShows(query)
            if (result.isSuccess) {
                _state.value = TvShowListState.Success(result.getOrNull() ?: emptyList())
            } else {
                _state.value = TvShowListState.Error(result.exceptionOrNull()?.message ?: "Неизвестная ошибка")
            }
        }
    }

    private fun refresh() {
        if (currentQuery.isNullOrBlank()) {
            loadShows()
        } else {
            searchShows(currentQuery!!)
        }
    }

    private fun retry() {
        refresh()
    }
}