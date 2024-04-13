package com.example.simpleweather

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service class for fetching and parsing weather data.
 */
class WeatherService {
    /**
     * Parses weather data from JSON string.
     * @param jsonString JSON string containing weather data.
     * @return List of WeatherInfo objects parsed from JSON.
     */
    fun parseWeatherDataFromJson(jsonString: String): List<WeatherInfo> {
        // Parsing JSON string to weather data
        val gson = Gson()
        val weatherList = gson.fromJson(jsonString, WeatherData::class.java).list
        val dailyTemperatureMap = mutableMapOf<String, Pair<Double, Double>>() // Pair(minTemperature, maxTemperature)

        // Collecting daily temperature data
        for (weather in weatherList) {
            val date = weather.dt_txt.substring(0, 10) // Extracting the date

            // Convert the date to the day of the week
            val dayOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.let {
                SimpleDateFormat("EEEE", Locale.getDefault()).format(it)
            } ?: ""

            // Update dailyTemperatureMap with the current day's temperatures
            val currentTemperatures = dailyTemperatureMap.getOrPut(dayOfWeek) { Pair(Double.MAX_VALUE, Double.MIN_VALUE) }
            dailyTemperatureMap[dayOfWeek] = Pair(minOf(currentTemperatures.first, weather.main.temp_min), maxOf(currentTemperatures.second, weather.main.temp_max))
        }

        val weatherInfoList = mutableListOf<WeatherInfo>()

        // Creating WeatherInfo objects for each day
        for ((dayOfWeek, temperatures) in dailyTemperatureMap) {
            val dayString = if (dayOfWeek == dailyTemperatureMap.keys.first()) "Today" else dayOfWeek
            // Find the corresponding weather for the day
            val weatherForDay = weatherList.find {
                val date = it.dt_txt.substring(0, 10)
                val day = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.let {
                    SimpleDateFormat("EEEE", Locale.getDefault()).format(it)
                } ?: ""
                day == dayOfWeek
            }
            val averageTemperature = String.format("%.2f", (temperatures.first + temperatures.second) / 2.0).toDouble()

            // Create WeatherInfo object
            val weatherInfo = WeatherInfo(
                date = dayString,
                temperature = if (dayOfWeek == dailyTemperatureMap.keys.first()) weatherForDay?.main?.temp ?: 0.0 else averageTemperature,
                minTemperature = temperatures.first,
                maxTemperature = temperatures.second,
                weatherDescription = weatherForDay?.weather?.get(0)?.description.orEmpty(),
                icon = getWeatherIcon(weatherForDay?.weather?.get(0)?.description.orEmpty())
                // You can add other properties as needed
            )

            weatherInfoList.add(weatherInfo)
        }

        return weatherInfoList
    }

    /**
     * Fetches weather data from API.
     * @param lat Latitude of the location.
     * @param lon Longitude of the location.
     * @return JSON string containing weather data.
     */
    suspend fun fetchWeatherDataFromApi(lat: Double, lon: Double): String {
        return try {
            val apiKey = "4e20c1cc76cb27c0be289ea2419144cb"
            val apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=$apiKey&units=metric"

            HttpClient().use { client ->
                client.get<String>(apiUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MyWeatherApp", "Error fetching data from API: ${e.message}")
            "" // Return an empty string in case of an exception
        }
    }
}

/**
 * Determines the appropriate weather icon based on the weather description.
 * @param description Description of the weather.
 * @return Corresponding weather icon.
 */
fun getWeatherIcon(description: String): ImageVector {

    return when {
        description.toLowerCase(Locale.getDefault()).contains("clear sky") -> Icons.Default.WbSunny
        description.toLowerCase(Locale.getDefault()).contains("partly cloudy") -> Icons.Default.WbCloudy
        description.toLowerCase(Locale.getDefault()).contains("cloud") -> Icons.Default.Cloud
        description.toLowerCase(Locale.getDefault()).contains("rain") -> rainingIcon()
        description.toLowerCase(Locale.getDefault()).contains("wind") -> Icons.Default.Waves
        description.toLowerCase(Locale.getDefault()).contains("snow") -> snowingIcon()
        else -> Icons.Default.QuestionMark
    }
}

fun snowingIcon(): ImageVector {
    return ImageVector.Builder(
        name = "cloudy_snowing",
        defaultWidth = 40.0.dp,
        defaultHeight = 40.0.dp,
        viewportWidth = 40.0f,
        viewportHeight = 40.0f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(10f, 31.625f)
            quadToRelative(-0.625f, 0f, -1.042f, -0.437f)
            quadToRelative(-0.416f, -0.438f, -0.416f, -1.063f)
            quadToRelative(0f, -0.583f, 0.416f, -1.021f)
            quadToRelative(0.417f, -0.437f, 1.042f, -0.437f)
            reflectiveQuadToRelative(1.042f, 0.416f)
            quadToRelative(0.416f, 0.417f, 0.416f, 1.042f)
            reflectiveQuadToRelative(-0.416f, 1.063f)
            quadToRelative(-0.417f, 0.437f, -1.042f, 0.437f)
            close()
            moveToRelative(20f, 0f)
            quadToRelative(-0.625f, 0f, -1.042f, -0.437f)
            quadToRelative(-0.416f, -0.438f, -0.416f, -1.063f)
            quadToRelative(0f, -0.583f, 0.416f, -1.021f)
            quadToRelative(0.417f, -0.437f, 1.042f, -0.437f)
            reflectiveQuadToRelative(1.042f, 0.416f)
            quadToRelative(0.416f, 0.417f, 0.416f, 1.042f)
            reflectiveQuadToRelative(-0.416f, 1.063f)
            quadToRelative(-0.417f, 0.437f, -1.042f, 0.437f)
            close()
            moveToRelative(-15f, 6.667f)
            quadToRelative(-0.625f, 0f, -1.042f, -0.438f)
            quadToRelative(-0.416f, -0.437f, -0.416f, -1.062f)
            quadToRelative(0f, -0.584f, 0.416f, -1.021f)
            quadToRelative(0.417f, -0.438f, 1.042f, -0.438f)
            reflectiveQuadToRelative(1.042f, 0.417f)
            quadToRelative(0.416f, 0.417f, 0.416f, 1.042f)
            reflectiveQuadToRelative(-0.416f, 1.062f)
            quadToRelative(-0.417f, 0.438f, -1.042f, 0.438f)
            close()
            moveToRelative(5f, -6.667f)
            quadToRelative(-0.625f, 0f, -1.042f, -0.437f)
            quadToRelative(-0.416f, -0.438f, -0.416f, -1.063f)
            quadToRelative(0f, -0.583f, 0.416f, -1.021f)
            quadToRelative(0.417f, -0.437f, 1.042f, -0.437f)
            reflectiveQuadToRelative(1.042f, 0.416f)
            quadToRelative(0.416f, 0.417f, 0.416f, 1.042f)
            reflectiveQuadToRelative(-0.416f, 1.063f)
            quadToRelative(-0.417f, 0.437f, -1.042f, 0.437f)
            close()
            moveToRelative(5f, 6.667f)
            quadToRelative(-0.625f, 0f, -1.042f, -0.438f)
            quadToRelative(-0.416f, -0.437f, -0.416f, -1.062f)
            quadToRelative(0f, -0.584f, 0.416f, -1.021f)
            quadToRelative(0.417f, -0.438f, 1.042f, -0.438f)
            reflectiveQuadToRelative(1.042f, 0.417f)
            quadToRelative(0.416f, 0.417f, 0.416f, 1.042f)
            reflectiveQuadToRelative(-0.416f, 1.062f)
            quadToRelative(-0.417f, 0.438f, -1.042f, 0.438f)
            close()
            moveToRelative(-12.792f, -12.25f)
            quadToRelative(-3.625f, 0f, -6.208f, -2.584f)
            quadToRelative(-2.583f, -2.583f, -2.583f, -6.25f)
            quadToRelative(0f, -3.291f, 2.312f, -5.875f)
            quadTo(8.042f, 8.75f, 11.5f, 8.458f)
            quadToRelative(1.333f, -2.333f, 3.562f, -3.708f)
            quadTo(17.292f, 3.375f, 20f, 3.375f)
            quadToRelative(3.75f, 0f, 6.375f, 2.396f)
            reflectiveQuadToRelative(3.208f, 5.979f)
            quadToRelative(3.125f, 0.167f, 5.084f, 2.25f)
            quadToRelative(1.958f, 2.083f, 1.958f, 4.875f)
            quadToRelative(0f, 2.958f, -2.104f, 5.063f)
            quadToRelative(-2.104f, 2.104f, -5.063f, 2.104f)
            close()
            moveToRelative(0f, -2.667f)
            horizontalLineToRelative(17.25f)
            quadToRelative(1.875f, 0f, 3.188f, -1.313f)
            quadToRelative(1.312f, -1.312f, 1.312f, -3.187f)
            quadToRelative(0f, -1.875f, -1.312f, -3.187f)
            quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
            horizontalLineToRelative(-2.416f)
            verticalLineToRelative(-1.333f)
            quadToRelative(0f, -2.917f, -2.063f, -4.959f)
            quadTo(22.917f, 6.042f, 20f, 6.042f)
            quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
            reflectiveQuadToRelative(-2.583f, 3.083f)
            lineToRelative(-0.292f, 0.792f)
            horizontalLineToRelative(-1.125f)
            quadToRelative(-2.542f, 0.083f, -4.313f, 1.875f)
            quadToRelative(-1.77f, 1.791f, -1.77f, 4.291f)
            quadToRelative(0f, 2.584f, 1.812f, 4.375f)
            quadToRelative(1.813f, 1.792f, 4.354f, 1.792f)
            close()
            moveTo(20f, 14.708f)
            close()
        }
    }.build()

}


fun rainingIcon(): ImageVector {
    return ImageVector.Builder(
        name = "rainy",
        defaultWidth = 40.0.dp,
        defaultHeight = 40.0.dp,
        viewportWidth = 40.0f,
        viewportHeight = 40.0f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(23.208f, 36.458f)
            quadToRelative(-0.458f, 0.25f, -1f, 0.063f)
            quadToRelative(-0.541f, -0.188f, -0.791f, -0.646f)
            lineToRelative(-2.75f, -5.5f)
            quadToRelative(-0.25f, -0.5f, -0.084f, -1.042f)
            quadToRelative(0.167f, -0.541f, 0.667f, -0.791f)
            quadToRelative(0.5f, -0.209f, 1.021f, -0.042f)
            quadToRelative(0.521f, 0.167f, 0.771f, 0.667f)
            lineToRelative(2.75f, 5.5f)
            quadToRelative(0.25f, 0.5f, 0.083f, 1.021f)
            quadToRelative(-0.167f, 0.52f, -0.667f, 0.77f)
            close()
            moveToRelative(10f, 0f)
            quadToRelative(-0.458f, 0.209f, -1f, 0.042f)
            quadToRelative(-0.541f, -0.167f, -0.791f, -0.667f)
            lineToRelative(-2.75f, -5.5f)
            quadToRelative(-0.25f, -0.5f, -0.084f, -1.021f)
            quadToRelative(0.167f, -0.52f, 0.667f, -0.77f)
            reflectiveQuadToRelative(1.021f, -0.063f)
            quadToRelative(0.521f, 0.188f, 0.771f, 0.646f)
            lineToRelative(2.75f, 5.5f)
            quadToRelative(0.25f, 0.5f, 0.083f, 1.042f)
            quadToRelative(-0.167f, 0.541f, -0.667f, 0.791f)
            close()
            moveToRelative(-20f, 0f)
            quadToRelative(-0.458f, 0.209f, -1f, 0.042f)
            quadToRelative(-0.541f, -0.167f, -0.791f, -0.625f)
            lineToRelative(-2.75f, -5.5f)
            quadToRelative(-0.25f, -0.5f, -0.063f, -1.042f)
            quadToRelative(0.188f, -0.541f, 0.688f, -0.791f)
            quadToRelative(0.458f, -0.209f, 1f, -0.042f)
            quadToRelative(0.541f, 0.167f, 0.791f, 0.625f)
            lineToRelative(2.75f, 5.542f)
            quadToRelative(0.25f, 0.5f, 0.063f, 1.021f)
            quadToRelative(-0.188f, 0.52f, -0.688f, 0.77f)
            close()
            moveToRelative(-1f, -10.416f)
            quadToRelative(-3.625f, 0f, -6.208f, -2.584f)
            quadToRelative(-2.583f, -2.583f, -2.583f, -6.25f)
            quadToRelative(0f, -3.291f, 2.312f, -5.875f)
            quadTo(8.042f, 8.75f, 11.5f, 8.458f)
            quadToRelative(1.333f, -2.333f, 3.562f, -3.708f)
            quadTo(17.292f, 3.375f, 20f, 3.375f)
            quadToRelative(3.75f, 0f, 6.375f, 2.396f)
            reflectiveQuadToRelative(3.208f, 5.979f)
            quadToRelative(3.125f, 0.167f, 5.084f, 2.25f)
            quadToRelative(1.958f, 2.083f, 1.958f, 4.875f)
            quadToRelative(0f, 2.958f, -2.104f, 5.063f)
            quadToRelative(-2.104f, 2.104f, -5.063f, 2.104f)
            close()
            moveToRelative(0f, -2.667f)
            horizontalLineToRelative(17.25f)
            quadToRelative(1.875f, 0f, 3.188f, -1.313f)
            quadToRelative(1.312f, -1.312f, 1.312f, -3.187f)
            quadToRelative(0f, -1.875f, -1.312f, -3.187f)
            quadToRelative(-1.313f, -1.313f, -3.188f, -1.313f)
            horizontalLineToRelative(-2.416f)
            verticalLineToRelative(-1.333f)
            quadToRelative(0f, -2.917f, -2.063f, -4.959f)
            quadTo(22.917f, 6.042f, 20f, 6.042f)
            quadToRelative(-2.125f, 0f, -3.875f, 1.125f)
            reflectiveQuadToRelative(-2.583f, 3.083f)
            lineToRelative(-0.334f, 0.792f)
            horizontalLineToRelative(-1.083f)
            quadToRelative(-2.542f, 0.083f, -4.313f, 1.875f)
            quadToRelative(-1.77f, 1.791f, -1.77f, 4.291f)
            quadToRelative(0f, 2.584f, 1.812f, 4.375f)
            quadToRelative(1.813f, 1.792f, 4.354f, 1.792f)
            close()
            moveTo(20f, 14.708f)
            close()
        }
    }.build()
}