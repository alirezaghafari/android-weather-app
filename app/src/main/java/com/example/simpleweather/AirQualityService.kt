package com.example.simpleweather

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Service class for fetching and parsing air quality data.
 */
class AirQualityService {
    /**
     * Parses air quality data from JSON string.
     * @param jsonString JSON string containing air quality data.
     * @return List of AirQualityInfo objects parsed from JSON.
     */
    fun parseAirQualityDataFromJson(jsonString: String): List<AirQualityInfo> {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val jsonArray = json.decodeFromString<JsonArray>(jsonString)

            val airQualityList = mutableListOf<AirQualityInfo>()

            for (jsonElement in jsonArray) {
                val jsonObject = jsonElement.jsonObject

                val latitude = jsonObject["Latitude"]?.jsonPrimitive?.doubleOrNull
                val longitude = jsonObject["Longitude"]?.jsonPrimitive?.doubleOrNull
                val stationName = jsonObject["StationName_En"]?.jsonPrimitive?.contentOrNull
                val city = jsonObject["City"]?.jsonPrimitive?.contentOrNull
                val pm2_5 = jsonObject["PM2_5"]?.jsonPrimitive?.intOrNull

                if (latitude != null && longitude != null && city != null && pm2_5 != null && stationName != null) {
                    airQualityList.add(AirQualityInfo(latitude, longitude, stationName, city, pm2_5))
                }
            }

            airQualityList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetches air quality data from API.
     * @return JSON string containing air quality data.
     */
    suspend fun fetchAirQualityDataFromApi(): String {
        return try {
            val apiUrl = "https://aqms.doe.ir/Home/LoadAQIMap?id=1"

            HttpClient().use { client ->
                client.get<String>(apiUrl)
            }
        } catch (e: Exception) {
            Log.e("MyWeatherApp", "Error fetching air quality from API: ${e.message}")
            ""
        }
    }
}
