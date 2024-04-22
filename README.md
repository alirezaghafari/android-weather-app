# MyWeather App

MyWeather App is a basic Android application that allows users to check the weather and air quality information based on their location. It provides a user-friendly interface for selecting the province and city, and then displays weather and air quality data accordingly.It was created using Kotlin and Jetpack Compose.

## Features

- **Location-based Weather**: Automatically detects the user's location and displays the current weather information.
- **Province and City Selection**: Users can manually select their province and city to view weather and air quality information for that location.
- **Weather Information**: Provides detailed weather information including temperature, humidity, wind speed, and more.
- **Air Quality Information**: Displays air quality information such as AQI (Air Quality Index), pollutant levels, and health recommendations.

## Installation

To use the MyWeather App, follow these steps:

1. Clone the repository to your local machine:

   ```bash
   git clone https://github.com/alirezaghafari/android-weather-app.git
   ```

2. Open the project in Android Studio.

3. Build and run the project on your Android device or emulator.

- Before running the app, ensure you have an OpenWeatherMap account and obtain an API key (for this usage OpenWeatherMap gives free API key). Once you have the API key, replace the existing key with your own in the WeatherService.kt file:

  ```kotlin
  // WeatherService.kt file
  val apiKey = "YOUR_OPENWEATHER_API_KEY"
  ```

- You also need to replace the default air pollution data API URL with your own in AirQualityService.kt file. (The origin API has been developed only for the cities of Iran)

  ```kotlin
  // AirQualityService.kt file
  val apiKey = "val apiUrl = "YOUR_AIR_POLLUTION_API_URL""
  ```

## Usage

Upon launching the MyWeather App, you will be presented with the main screen consisting of two pages:

1- My Location Page: This page displays your current location's weather and air quality information.

2- City Selection Page: This page allows you to manually select your province and city from a list of predefined options. Once selected, you can view the weather and air quality information for that specific location.

- The app provides weather and pollution data for 15 default cities in Iran. To customize the city selection, you need to update the city names and coordinates in the LocationService.kt file. You also need to change the names of the city buttons to your desired cities in MainActivity.kt.

  ```kotlin
   // Example of customizing city selection
   fun getLatLonByCityCode(provinceCode: Int, index: Int): Pair<Double, Double>? {
       return when (provinceCode) {
           // Update city names and coordinates accordingly
           0 -> when (index) {
               0 -> Pair(35.6895, 51.3890) // Tehran
               1 -> Pair(35.7590, 52.7755) // Firuzkuh
               2 -> Pair(35.7013, 52.0586) // Damavand
               else -> null
           }
           // Repeat for other provinces
           // ...
           else -> null
       }
   }
  ```

## Screenshots

Here are some screenshots of the MyWeather app:

<table>
  <tr>
    <td>
      <strong>Icon</strong><br>
      <img src="https://github.com/alirezaghafari/android-weather-app/raw/master/screenshots/icon.png" alt="Main Screen" width="200"/>
    </td>
    <td>
      <strong>Your Location</strong><br>
      <img src="https://github.com/alirezaghafari/android-weather-app/blob/master/screenshots/myLocation.png" alt="My Location" width="200">
    </td>
    <td>
      <strong>Custom City</strong><br>
      <img src="https://github.com/alirezaghafari/android-weather-app/blob/master/screenshots/customCity.png" alt="Custom City" width="200">
    </td>
  </tr>
  <tr>
    <td>
      <strong>Select Province</strong><br>
      <img src="https://github.com/alirezaghafari/android-weather-app/blob/master/screenshots/provinceSelect.png" alt="Province Selection" width="200">
    </td>
    <td>
      <strong>Select City</strong><br>
      <img src="https://github.com/alirezaghafari/android-weather-app/blob/master/screenshots/citySelect.png" alt="City Selection" width="200">
    </td>
  </tr>
</table>

## Releases

Download the APK file of the MyWeather app to get started:

[Download APK](https://github.com/alirezaghafari/android-weather-app/releases/tag/v1.0.0)

## Dependencies

The MyWeather App uses the following dependencies:

- Kotlin
- Android Jetpack (Compose, Lifecycle, Activity, etc.)
- Google Play Services (Location)
- Ktor (HTTP client)
- Material Components for Android

## Contributing

Contributions to the MyWeather App are welcome! If you would like to contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Make your changes and commit them (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Create a new Pull Request.

---
