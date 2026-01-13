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

    // Instanciamos el repositorio (en apps grandes esto se inyecta con Hilt/Koin)
    private val repository = DiccionarioRepository()

    // Estado interno (privado) y externo (público)
    private val _uiState = MutableStateFlow(DiccionarioUiState())
    val uiState = _uiState.asStateFlow()

    fun onPalabraChanged(nuevaPalabra: String) {
        _uiState.update { it.copy(palabra = nuevaPalabra, error = null) }
    }

    fun buscarSignificado() {
        val palabra = _uiState.value.palabra
        if (palabra.isBlank()) return

        viewModelScope.launch {
            // 1. Estado de carga
            _uiState.update {
                it.copy(cargando = true, descripcion = "", urlImagen = "", error = null)
            }

            // 2. Llamada al repositorio
            val resultadoDefinicion = repository.obtenerDefinicion(palabra)

            // Generamos la URL de la imagen aquí (o podría ser en el repo si viniera de API)
            val nuevaImagen = "https://loremflickr.com/400/400/$palabra"

            // 3. Actualizamos estado con el resultado
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