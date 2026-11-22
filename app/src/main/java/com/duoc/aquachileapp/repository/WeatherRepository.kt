package com.duoc.aquachileapp.repository

import com.duoc.aquachileapp.data.Remote.WeatherResponseDto
import com.duoc.aquachileapp.data.Remote.WeatherRetrofitClient

class WeatherRepository {

    suspend fun obtenerClima(
        latitude: Double,
        longitude: Double
    ): WeatherResponseDto {

        return WeatherRetrofitClient.api
            .obtenerClima(
                latitude = latitude,
                longitude = longitude
            )
    }
}