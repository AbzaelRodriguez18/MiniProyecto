package com.example.miniproyecto.data.model

data class DiccionarioUiState(
    val palabra: String = "",
    val descripcion: String = "",
    val urlImagen: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)