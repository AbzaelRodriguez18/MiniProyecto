package com.example.miniproyecto.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class DiccionarioRepository {

    // Función suspendida para no bloquear el hilo principal
    suspend fun obtenerDefinicion(palabra: String): String = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://rae-api.com/api/words/$palabra")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (body != null) {
                parsearJsonRAE(body)
            } else {
                "No se encontró respuesta del servidor."
            }
        } catch (e: Exception) {
            "Error de conexión: ${e.message}"
        }
    }

    // Función privada auxiliar para limpiar el JSON
    private fun parsearJsonRAE(jsonString: String): String {
        return try {
            val json = JSONObject(jsonString)
            val description = json
                .optJSONObject("data")
                ?.optJSONArray("meanings")
                ?.optJSONObject(0)
                ?.optJSONArray("senses")
                ?.optJSONObject(0)
                ?.optString("description")

            description ?: "No se encontró definición para esta palabra."
        } catch (e: Exception) {
            "Error al leer los datos."
        }
    }
}