package com.example.simpleweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.simpleweather.LocationService.getLastKnownLocation
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices




private val airQualityService = AirQualityService()
private val weatherService = WeatherService()

class MainActivity : ComponentActivity() {

    companion object {
        var currentLocation by mutableStateOf(Pair(0.0,0.0))
        var airQuality by mutableStateOf<AirQualityInfo?>(AirQualityInfo(0.0, 0.0, "", "", 0))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocationService.requestLocationPermission(this)

        setContent {
            myApp()
        }
    }
}

enum class SecondPage {
    Province,City,Weather
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun myApp() {
    // State to track the list of buttons
    var buttonList by remember { mutableStateOf(List(5) { "Button $it" }) }
    val animationScope = rememberCoroutineScope()
    var whichFrameInSecondPage = SecondPage.Province
    var province = -1
    var city = -1
    var lat = 0.0
    var lon = 0.0

    MaterialTheme(
        lightColorScheme(primary = Color(56,146,188))
    ) {
        // Set up the PagerState with the number of pages
        val pagerState = rememberPagerState(pageCount = { 2 })

        // Set up the LazyColumn with items for each page
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(12, 27, 43))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                // Display content for each page
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    if(pagerState.currentPage==0)
                        if(page==0)
                            GetLocationContent()
                    if (page == 1) {
                        // Page 2 content with a vertical list of clickable buttons
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = null,
                                        tint = if(whichFrameInSecondPage == SecondPage.Province)Color.Gray else Color.White,
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clickable {
                                                whichFrameInSecondPage = SecondPage.Province
                                                buttonList = (List(5) { "Button $it" })
                                            }
                                    )
                                    Text(
                                        text = if(whichFrameInSecondPage == SecondPage.Weather) "" else if(whichFrameInSecondPage == SecondPage.Province) "Province" else "City",
                                        color = Color.White,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if(whichFrameInSecondPage == SecondPage.Weather) getButtonLabel(province,city) else "",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if(whichFrameInSecondPage == SecondPage.Weather) {
                                    var weatherList by remember { mutableStateOf<List<WeatherInfo>>(emptyList()) }
                                    var error by remember { mutableStateOf<String?>(null) }

                                    LaunchedEffect(Unit) {
                                        try {
                                            val jsonString = airQualityService.fetchAirQualityDataFromApi()
                                            val airQualityList = airQualityService.parseAirQualityDataFromJson(jsonString)
                                            var closestPoint: AirQualityInfo?
                                            var minDistance = Double.MAX_VALUE

                                            for (point in airQualityList) {
                                                val distance = calculateEuclideanDistanceForAirQuality(lat, lon, point.latitude, point.longitude)
                                                if (distance < minDistance) {
                                                    minDistance = distance
                                                    closestPoint = point
                                                    MainActivity.airQuality=closestPoint
                                                }
                                            }
                                            // Fetch data when the composable is first launched
                                            val jsonString2 = weatherService.fetchWeatherDataFromApi(lat,lon)
                                            weatherList = weatherService.parseWeatherDataFromJson(jsonString2)
                                        } catch (e: Exception) {
                                            // Handle errors here
                                            e.printStackTrace()
                                            error = "Failed to fetch data"
                                        }
                                    }

                                    // Display the parsed weather data, error message, or a loading indicator
                                    if (weatherList.isNotEmpty()) {
                                        WeatherCard(weatherList = weatherList)
                                    } else if (error != null) {
                                        ErrorScreen(error = error!!)
                                    } else {
                                        LoadingScreen()
                                    }
                                }

                            }
                            items(buttonList.size) { index ->
                                Button(
                                    shape = RoundedCornerShape(20, 20, 20, 20),
                                    onClick = {
                                        if(whichFrameInSecondPage == SecondPage.Province) {
                                            buttonList = List(3) { "Test${it + 1}" }
                                            province = index
                                            whichFrameInSecondPage = SecondPage.City
                                        }else{
                                            city = index
                                            val coordinates = getLatLonByCityCode(province, city)
                                            if (coordinates != null) {
                                                lat = coordinates.first
                                                lon = coordinates.second
                                            }
                                            whichFrameInSecondPage = SecondPage.Weather
                                            buttonList = List(0){""}
                                        }

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .padding(bottom = 15.dp, start = 10.dp, end = 10.dp)
                                ) {
                                    if (whichFrameInSecondPage == SecondPage.Province) {
                                        Text(
                                            text = when (index) {
                                                0 -> "Tehran"
                                                1 -> "Khouzestan"
                                                2 -> "Mazandaran"
                                                3 -> "Fars"
                                                4 -> "Markazi"
                                                else -> ""
                                            }, color = Color.White,fontSize = 20.sp,
                                        )
                                    }
                                    else{
                                        when (province) {
                                            0 -> {
                                                Text(
                                                    text = when (index) {
                                                        0 -> "Tehran"
                                                        1 -> "Firuzkuh"
                                                        2 -> "Damavand"
                                                        else -> ""
                                                    }, color = Color.White,fontSize = 20.sp,
                                                )
                                            }
                                            1 -> {
                                                Text(
                                                    text = when (index) {
                                                        0 -> "Ahwaz"
                                                        1 -> "Abadan"
                                                        2 -> "Dezful"
                                                        else -> ""
                                                    }, color = Color.White,fontSize = 20.sp,
                                                )
                                            }
                                            2 -> {
                                                Text(
                                                    text = when (index) {
                                                        0 -> "Sari"
                                                        1 -> "Ramsar"
                                                        2 -> "Chalus"
                                                        else -> ""
                                                    }, color = Color.White,fontSize = 20.sp,
                                                )
                                            }
                                            3 -> {
                                                Text(
                                                    text = when (index) {
                                                        0 -> "Shiraz"
                                                        1 -> "Nurabad"
                                                        2 -> "Jahrom"
                                                        else -> ""
                                                    }, color = Color.White,fontSize = 20.sp,
                                                )

                                            }
                                            4 -> {
                                                Text(
                                                    text = when (index) {
                                                        0 -> "Arak"
                                                        1 -> "Khomein"
                                                        2 -> "Mahalat"
                                                        else -> ""
                                                    }, color = Color.White,fontSize = 20.sp,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Counter at the bottom
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                items(2) { pageIndex ->
                    // Each item represents a dot or an icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = when {
                                    pageIndex == pagerState.currentPage -> Color.Gray
                                    else -> Color.LightGray
                                },
                                shape = CircleShape
                            )
                            .padding(4.dp)
                            .clickable {
                                animationScope.launch {
                                    if (pageIndex == 1) pagerState.animateScrollToPage(pagerState.currentPage + 1) else pagerState.animateScrollToPage(
                                        pagerState.currentPage - 1
                                    )
                                }
                            }
                    ) {
                        // If it's the first item, show location icon
                        if (pageIndex == 0) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null, // Provide a proper content description
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getButtonLabel(province: Int, index: Int): String {
    return when (province) {
        0 -> when (index) {
            0 -> "Tehran"
            1 -> "Firuzkuh"
            2 -> "Damavand"
            else -> ""
        }
        1 -> when (index) {
            0 -> "Ahwaz"
            1 -> "Abadan"
            2 -> "Dezful"
            else -> ""
        }
        2 -> when (index) {
            0 -> "Sari"
            1 -> "Ramsar"
            2 -> "Chalus"
            else -> ""
        }
        3 -> when (index) {
            0 -> "Shiraz"
            1 -> "Nurabad"
            2 -> "Jahrom"
            else -> ""
        }
        4 -> when (index) {
            0 -> "Arak"
            1 -> "Khomein"
            2 -> "Mahalat"
            else -> ""
        }
        else -> ""
    }
}

@Composable
fun GetLocationContent() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = remember { LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10000 // 10 seconds
        fastestInterval = 5000 // 5 seconds
    } }

    LaunchedEffect(LocalLifecycleOwner.current) {
        getLastKnownLocation(fusedLocationClient, locationRequest)
    }
    Spacer(modifier = Modifier.height(50.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(34.dp)
        )
        Text(
            text = "Your Location",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
    var weatherList by remember { mutableStateOf<List<WeatherInfo>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val jsonString = airQualityService.fetchAirQualityDataFromApi()
            val airQualityList = airQualityService.parseAirQualityDataFromJson(jsonString)
            var closestPoint: AirQualityInfo?
            var minDistance = Double.MAX_VALUE
            for (point in airQualityList) {
                val distance = calculateEuclideanDistanceForAirQuality(MainActivity.currentLocation.first, MainActivity.currentLocation.second, point.latitude, point.longitude)
                if (distance < minDistance) {
                    minDistance = distance
                    closestPoint = point
                    MainActivity.airQuality=closestPoint
                }
            }
            // Fetch data when the composable is first launched
            val jsonString2 = weatherService.fetchWeatherDataFromApi(MainActivity.currentLocation.first,MainActivity.currentLocation.second)
            weatherList = weatherService.parseWeatherDataFromJson(jsonString2)
        } catch (e: Exception) {
            // Handle errors here
            e.printStackTrace()
            error = "Failed to fetch data"
        }
    }
    // Display the parsed weather data, error message, or a loading indicator
    if (weatherList.isNotEmpty()) {
        WeatherCard(weatherList = weatherList)
    } else if (error != null) {
        ErrorScreen(error = error!!)
    } else {
        LoadingScreen()
    }

}

