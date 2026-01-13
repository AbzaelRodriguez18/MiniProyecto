package com.example.miniproyecto.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.miniproyecto.ui.viewmodel.DiccionarioViewModel

@Composable
fun DiccionarioScreen(
    viewModel: DiccionarioViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Diccionario Visual", fontSize = 28.sp)

        Spacer(Modifier.height(24.dp))

        TextField(
            value = state.palabra,
            onValueChange = { viewModel.onPalabraChanged(it) },
            label = { Text("Introduce una palabra") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.buscarSignificado() },
            enabled = !state.cargando
        ) {
            Text(if (state.cargando) "Buscando..." else "Buscar")
        }

        Spacer(Modifier.height(24.dp))

        if (state.cargando) {
            CircularProgressIndicator()
        } else {
            ResultadoContent(descripcion = state.descripcion, urlImagen = state.urlImagen)
        }
    }
}

@Composable
fun ResultadoContent(descripcion: String, urlImagen: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (descripcion.isNotEmpty()) {
            Text(
                text = descripcion,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (urlImagen.isNotEmpty()) {
            AsyncImage(
                model = urlImagen,
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}