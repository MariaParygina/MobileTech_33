package com.example.tvmazeapiapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeapiapp.data.model.TvShow
import com.example.tvmazeapiapp.data.repository.TvShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TvShowDetailsViewModel(
    private val repository: TvShowRepository
) : ViewModel() {
    private val _state = MutableStateFlow<TvShow?>(null)
    val state: StateFlow<TvShow?> = _state

    fun loadShow(id: Int) {
        viewModelScope.launch {
            val show = repository.getShowById(id)
            _state.value = show
        }
    }
}