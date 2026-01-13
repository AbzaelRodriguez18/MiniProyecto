package com.example.miniproyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
    var URL_Imagen by remember { mutableStateOf("") }

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

        Button(onClick = {
            if (palabra.isNotBlank()) {
                scope.launch {
                    URL_Imagen = "https://loremflickr.com/400/400/$palabra"
                    descripcion = buscarDescripcionRAE(palabra)
                }
            }
        }) {
            Text("Buscar")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (descripcion.isNotEmpty()) {
            Text(descripcion, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (URL_Imagen.isNotEmpty()) {
            AsyncImage(
                model = URL_Imagen,
                contentDescription = "Imagen de $palabra",
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }
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
        val request = Request.Builder()
            .url("https://rae-api.com/api/words/$palabra")
            .build()

        val response = OkHttpClient().newCall(request).execute()
        val body = response.body?.string() ?: return@withContext "No se encontró definición"

        val json = JSONObject(body)
        val description = json
            .optJSONObject("data")
            ?.optJSONArray("meanings")
            ?.optJSONObject(0)
            ?.optJSONArray("senses")
            ?.optJSONObject(0)
            ?.optString("description")


        description ?: "No se encontró definición"
    } catch (e: Exception) {
        "Error al conectarse o palabra no encontrada"
    }
}
