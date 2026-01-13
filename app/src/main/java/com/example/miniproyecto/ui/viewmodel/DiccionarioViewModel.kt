package com.example.miniproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto.data.model.DiccionarioUiState
import com.example.miniproyecto.data.repository.DiccionarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiccionarioViewModel : ViewModel() {

    private val repository = DiccionarioRepository()

    private val _uiState = MutableStateFlow(DiccionarioUiState())
    val uiState = _uiState.asStateFlow()

    fun onPalabraChanged(nuevaPalabra: String) {
        _uiState.update { it.copy(palabra = nuevaPalabra, error = null) }
    }

    fun buscarSignificado() {
        val palabra = _uiState.value.palabra
        if (palabra.isBlank()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(cargando = true, descripcion = "", urlImagen = "", error = null)
            }

            val resultadoDefinicion = repository.obtenerDefinicion(palabra)

            val nuevaImagen = "https://loremflickr.com/400/400/$palabra"

            _uiState.update {
                it.copy(
                    cargando = false,
                    descripcion = resultadoDefinicion,
                    urlImagen = nuevaImagen
                )
            }
        }
    }
}