package com.example.simpleweather

data class AirQualityInfo(
    val latitude: Double,
    val longitude: Double,
    val stationName_En: String,
    val city: String,
    val pm2_5: Int
)
