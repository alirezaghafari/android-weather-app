package com.example.simpleweather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DrawColorCircle(color: Color, description: String) {
    Canvas(
        modifier = Modifier
            .size(20.dp)
            .padding(5.dp)
    ) {
        drawCircle(color = color, radius = size.minDimension / 2)
    }
    Text(
        text = description,
        // Text style
        color = Color.White
    )
}

@Composable
fun getPollutionColor(pm2_5: Int): Color {
    return when {
        pm2_5 >= 300 -> Color(0xFFA52A2A) // قهوه‌ای
        pm2_5 in 200..299 -> Color(0xFF8A2BE2) // بنفش
        pm2_5 in 150..199 -> Color(0xFFFF0000) // قرمز
        pm2_5 in 100..149 -> Color(0xFFF59F03) // نارنجی
        pm2_5 in 50..99 -> Color(0xFFD8A91C) // زرد
        else -> Color(0xFF008000) // سبز
    }
}

@Composable
fun getPollutionDescription(pm2_5: Int): String {
    return when {
        pm2_5 >= 300 -> "Dangerous"
        pm2_5 in 200..299 ->"Risky"
        pm2_5 in 150..199 ->"Unhealthy"
        pm2_5 in 100..149 ->"Unhealthy for the sensitive group"
        pm2_5 in 50..99 -> "Not clean"
        else -> "Clean"
    }
}