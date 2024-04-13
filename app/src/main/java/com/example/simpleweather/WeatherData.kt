package com.example.simpleweather

data class WeatherData(
    val list: List<WeatherItem>,
    val city: City
)

data class WeatherItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val dt_txt: String
)

data class Main(
    val temp: Double,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)

data class City(
    val name: String,
    val coord: Coord,
    val country: String
)

data class Coord(
    val lat: Double,
    val lon: Double
)
