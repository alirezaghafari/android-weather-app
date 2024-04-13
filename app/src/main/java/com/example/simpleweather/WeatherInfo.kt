package com.example.simpleweather

import androidx.compose.ui.graphics.vector.ImageVector

data class WeatherInfo(
    val date: String,
    val temperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherDescription: String,
    val icon: ImageVector
)
