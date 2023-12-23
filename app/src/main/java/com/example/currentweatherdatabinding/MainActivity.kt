package com.example.currentweatherdatabinding

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.updateButton)
        button.setOnClickListener{
            val cityEditText: EditText = findViewById(R.id.cityEditText)
            val city = cityEditText.text.toString()

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val api = "71708aba4504a562f55ad203b3d109cf"
                    val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$api&units=metric"
                    val stream = URL(weatherURL).openStream()
                    val data = stream.bufferedReader().use { it.readText() }

                    val gson = Gson()
                    val weatherData = gson.fromJson(data, WeatherData::class.java)

                    val sunriseTime = weatherData.sys.sunrise * 1000
                    val sunsetTime = weatherData.sys.sunset * 1000

                    val sunrise = Date(sunriseTime)
                    val sunset = Date(sunsetTime)

                    val hours = (sunset.time - sunrise.time) / (1000 * 60 * 60)
                    val minutes = ((sunset.time - sunrise.time) % (1000 * 60 * 60)) / (1000 * 60)

                    val temperature = weatherData.main.temperature
                    val windSpeed = weatherData.wind.windSpeed

                    val timeText = "Время:  $hours h $minutes min"
                    val temperatureText = "Температура: $temperature °C"
                    val windSpeedText = "Скорость ветра: $windSpeed m/s"

                    val daylightTextView: TextView = findViewById(R.id.daylightTextView)
                    val temperatureTextView: TextView = findViewById(R.id.temperatureTextView)
                    val windSpeedTextView: TextView = findViewById(R.id.windSpeedTextView)

                    runOnUiThread {
                        daylightTextView.text = timeText
                        temperatureTextView.text = temperatureText
                        windSpeedTextView.text = windSpeedText
                    }
                } catch (e: IOException) {
                    Log.d("IOException", "I'm downed")
                }
            }
        }
    }

    data class WeatherData(
        @SerializedName("sys") val sys: Sys,
        @SerializedName("name") val cityName: String,
        @SerializedName("main") val main: Main,
        @SerializedName("wind") val wind: Wind
    )

    data class Sys(
        @SerializedName("sunrise") val sunrise: Long,
        @SerializedName("sunset") val sunset: Long
    )

    data class Main(
        @SerializedName("temp") val temperature: Double
    )

    data class Wind(
        @SerializedName("speed") val windSpeed: Double
    )
}