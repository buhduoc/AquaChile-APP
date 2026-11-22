package com.duoc.aquachileapp.data.Remote

import retrofit2.http.*


interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun obtenerClima(

        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("current")
        current: String =
            "temperature_2m,weather_code",

        @Query("daily")
        daily: String =
            "temperature_2m_max,temperature_2m_min",

        @Query("forecast_days")
        forecastDays: Int = 1,

        @Query("timezone")
        timezone: String = "auto"

    ): WeatherResponseDto
}