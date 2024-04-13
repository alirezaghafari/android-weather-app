package com.example.simpleweather

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ErrorScreen(error: String) {
    Text(
        text = "Error: $error. It's probably because of bad internet connection.",
        color = Color.Red
    )
}
