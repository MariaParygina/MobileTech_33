package com.example.tvmazeapiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.repository.TvShowRepository
import com.example.tvmazeapiapp.ui.state.TvShowListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TvShowListEvent {
    object LoadShows : TvShowListEvent()
    data class Search(val query: String) : TvShowListEvent()
    object Refresh : TvShowListEvent()
    object Retry : TvShowListEvent()
    object LoadMore : TvShowListEvent()
}

class TvShowListViewModel(
    private val repository: TvShowRepository
) : ViewModel() {
    private val _state = MutableStateFlow<TvShowListState>(TvShowListState.Loading)
    val state: StateFlow<TvShowListState> = _state.asStateFlow()

    private var currentPage = 0
    private var allShows = mutableListOf<TvShow>()
    private var currentQuery: String? = null
    private var isLoadingMore = false

    fun onEvent(event: TvShowListEvent) {
        when (event) {
            is TvShowListEvent.LoadShows -> loadShows()
            is TvShowListEvent.Search -> searchShows(event.query)
            is TvShowListEvent.Refresh -> refresh()
            is TvShowListEvent.Retry -> retry()
            is TvShowListEvent.LoadMore -> loadMore()
        }
    }

    private fun loadShows() {
        viewModelScope.launch {
            _state.value = TvShowListState.Loading
            currentQuery = null
            currentPage = 0
            allShows.clear()
            isLoadingMore = false

            val result = repository.getShows(page = currentPage)
            if (result.isSuccess) {
                val shows = result.getOrNull() ?: emptyList()
                val first25 = shows.take(25)
                allShows.addAll(first25)
                currentPage++
                _state.value = TvShowListState.Success(allShows.toList())
            } else {
                _state.value = TvShowListState.Error(result.exceptionOrNull()?.message ?: "Неизвестная ошибка")
            }
        }
    }

    private fun loadMore() {
        if (isLoadingMore) return

        isLoadingMore = true

        viewModelScope.launch {
            val result = repository.getShows(page = currentPage)
            if (result.isSuccess) {
                val newShows = result.getOrNull() ?: emptyList()
                val next25 = newShows.take(25)  // Берем следующие 25
                allShows.addAll(next25)
                currentPage++
                _state.value = TvShowListState.Success(allShows.toList())
            } else {
                _state.value = TvShowListState.Success(allShows.toList())
            }
            isLoadingMore = false
        }
    }

    private fun searchShows(query: String) {
        if (query.isBlank()) {
            loadShows()
            return
        }

        viewModelScope.launch {
            _state.value = TvShowListState.Loading
            currentQuery = query

            val result = repository.searchShows(query)
            if (result.isSuccess) {
                val shows = result.getOrNull() ?: emptyList()
                _state.value = TvShowListState.Success(shows)
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