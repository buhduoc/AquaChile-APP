package com.duoc.aquachileapp.viewmodel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aquachileapp.data.Remote.WeatherResponseDto
import com.duoc.aquachileapp.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository =
        WeatherRepository()


    var clima by
    mutableStateOf<WeatherResponseDto?>(null)
        private set

    var cargando by
    mutableStateOf(false)
        private set

    var error by
    mutableStateOf<String?>(null)
        private set


    fun cargarClima(
        centro: String
    ) {

        val coordenadas =
            coordenadasCentro(centro)

        viewModelScope.launch {

            cargando = true
            error = null

            try {

                clima =
                    repository.obtenerClima(
                        latitude = coordenadas.first,
                        longitude = coordenadas.second
                    )

                Log.d(
                    "API_CLIMA",
                    "Clima cargado: $centro - ${clima?.current?.temperatura} °C"
                )

            } catch (e: Exception) {

                error =
                    "No se pudo obtener el clima"

                Log.e(
                    "API_CLIMA",
                    "Error: ${e.message}"
                )

            } finally {

                cargando = false
            }
        }
    }


    private fun coordenadasCentro(
        centro: String
    ): Pair<Double, Double> {

        return when {

            centro.contains(
                "Calbuco",
                ignoreCase = true
            ) ->
                Pair(-41.7680, -73.1273)

            centro.contains(
                "Hornopirén",
                ignoreCase = true
            ) ->
                Pair(-41.9694, -72.4730)

            centro.contains(
                "Osorno",
                ignoreCase = true
            ) ->
                Pair(-40.5739, -73.1335)

            else ->
                Pair(-41.4693, -72.9424)
        }
    }


    fun descripcion(
        codigo: Int?
    ): String {

        return when (codigo) {

            0 -> "Despejado"
            1 -> "Mayormente Despejado"
            2 -> "Parcialmente Nublado"
            3 -> "Nublado"

            45, 48 ->
                "Niebla"

            51, 53, 55 ->
                "Llovizna"

            61, 63, 65 ->
                "Lluvia"

            71, 73, 75 ->
                "Nieve"

            80, 81, 82 ->
                "Chubascos"

            95, 96, 99 ->
                "Tormenta"

            else ->
                "Condición Variable"
        }
    }
}