package com.example.miniproyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.gson.annotations.SerializedName
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miniproyecto.ui.theme.MiniProyectoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniProyectoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Pantalla(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Pantalla(modifier: Modifier = Modifier) {
    var palabra by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Diccionario Visual",
            fontSize = 25.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = palabra,
            onValueChange = { palabra = it },
            label = { Text("Palabra") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            if (palabra.isNotBlank()) {
                scope.launch {
                    descripcion = buscarDescripcionRAE(palabra)
                }
            }
        }) {
            Text("Buscar")
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(descripcion, fontSize = 18.sp)
    }
}
@Preview(showBackground = true)
@Composable
fun ProyectoPreview() {
    MiniProyectoTheme {
        Pantalla()
    }
}


suspend fun buscarDescripcionRAE(palabra: String): String = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://rae-api.com/api/words/$palabra")
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return@withContext "No se encontró definición"

        // Parsear JSON
        val json = JSONObject(body)
        val data = json.getJSONObject("data")
        val meanings = data.getJSONArray("meanings")
        if (meanings.length() == 0) return@withContext "No se encontró definición"

        val firstMeaning = meanings.getJSONObject(0)
        val senses = firstMeaning.getJSONArray("senses")
        if (senses.length() == 0) return@withContext "No se encontró definición"

        val firstSense = senses.getJSONObject(0)
        return@withContext firstSense.getString("description")
    } catch (e: Exception) {
        return@withContext "Error al conectarse"
    }
}