package com.example.weatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import android.widget.Toast
import com.google.gson.Gson
import android.widget.Button
import android.widget.EditText
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val weather_api = "608b6665d935c8fc2726c224c226af29"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            selectWeather()
        }
    }

    fun selectWeather() {
        val searchText: EditText = findViewById(R.id.searchText)
        val cityName: String = searchText.text.toString()
        if (cityName == "") {
            Toast.makeText(this, "请输入城市名称", Toast.LENGTH_SHORT).show()
        } else {
            requestWeather(cityName)
        }
    }

    fun requestWeather(cityName: String) {
        runOnUiThread {
            Log.d("aaa", "cityName: " + cityName)
        }
        val client = OkHttpClient()
        val url = "https://restapi.amap.com/v3/weather/weatherInfo?key=$weather_api&city=$cityName"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "请求失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val data = response.body?.string()
                    if (data == null) {
                        return
                    }
                    runOnUiThread {
                        Log.d("aaa", "data:" + data)
                    }
                    val gson = Gson()
                    val weatherInfo = gson.fromJson(data, WeatherResponse::class.java)
                    runOnUiThread {
                        Log.d("aaa", "infoCode: " + weatherInfo.infocode)
                    }
//                    Log.d("aaa", weatherInfo.infoCode.toString())
                    if (weatherInfo.infocode == "10000") {
                        val info = weatherInfo.lives[0]
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "请求成功", Toast.LENGTH_SHORT).show()
                        }
                        val showText: TextView = findViewById(R.id.showText)
                        showText.setText("城市：${info.city}\n\n天气：${info.weather}\n\n温度：${info.temperature}\n")
//                        val searchText: EditText = findViewById(R.id.searchText)
//                        searchText.setText("")
                    } else {
                        runOnUiThread {
                            Log.d("aaa", "infoCode:" + weatherInfo.toString())
                            Toast.makeText(this@MainActivity, "获取天气信息失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "请求失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}

data class WeatherResponse (
    val infocode: String,
    val lives: List<LiveWeather>
)

data class LiveWeather(
    val city: String,
    val weather: String,
    val temperature: String,
    val winddirection: String,
    val windpower: String,
    val humidity: String,
    val reporttime: String
)