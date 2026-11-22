package com.duoc.aquachileapp.data.Remote


import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    val current: CurrentWeatherDto?,
    val daily: DailyWeatherDto?
)

data class CurrentWeatherDto(

    @SerializedName("temperature_2m")
    val temperatura: Double?,

    @SerializedName("weather_code")
    val weatherCode: Int?
)

data class DailyWeatherDto(

    @SerializedName("temperature_2m_max")
    val maxima: List<Double>?,

    @SerializedName("temperature_2m_min")
    val minima: List<Double>?
)