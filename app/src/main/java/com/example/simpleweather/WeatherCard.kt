package com.example.simpleweather

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch

/**
 * Composable function responsible for displaying weather information in a card layout.
 * @param weatherList The list of weather information to display.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherCard(weatherList: List<WeatherInfo>) {
    val pagerState = rememberPagerState(pageCount = { weatherList.size })
    val animationScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(state = pagerState) { page ->
            WeatherItem(weather = weatherList[page])
        }

        LazyRow(
            modifier = Modifier
                .padding(bottom = 16.dp)
        ) {
            items(6) { pageIndex ->
                // Each item represents a dot or an icon
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = when {
                                pageIndex == pagerState.currentPage -> Color.Cyan
                                else -> Color.Gray
                            },
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clickable {
                            animationScope.launch {
                                pagerState.animateScrollToPage(pageIndex)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        // Pollution Information
        Spacer(modifier = Modifier.height(70.dp))
        Row(){
            Text(
                text = "Pollution:",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Start,
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(115.dp)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .weight(3f)
                        .background(color = Color.Transparent),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Pollutant:  PM2.5",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Station:  ${MainActivity.airQuality?.stationName_En}",
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${MainActivity.airQuality?.let { getPollutionDescription(it.pm2_5)} }",
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.Transparent),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = MainActivity.airQuality?.pm2_5?.toString() ?: "-",
                        style = TextStyle(fontSize = 48.sp),
                        textAlign = TextAlign.End,
                        color = MainActivity.airQuality?.let { getPollutionColor(it.pm2_5) }
                            ?: Color.Transparent
                    )
                }

            }
        }

        // Pollution Scale
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DrawColorCircle(Color(0xFF008000), "Clean")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DrawColorCircle(Color(0xFFFFFF00) , "Not clean")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DrawColorCircle(Color(0xFFFFA500), "Unhealthy for the sensitive group")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DrawColorCircle(Color(0xFFFF0000), "Unhealthy")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DrawColorCircle(Color(0xFF8A2BE2), "Risky")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DrawColorCircle(Color(0xFFA52A2A), "Dangerous")
        }
    }
}
