package com.example.weatherapp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import com.google.gson.Gson
import okhttp3.Call
import android.content.Context
import com.example.weatherapp.Data.WeatherResponse
import com.example.weatherapp.Data.WeatherTimeResponse

class WeatherNetwork(private val context: Context) {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val weather_api = "Sr-c5V7NWuYCNInth"

    fun getNow(cityName: String, callback: weatherCallback) {
        val url = "https://api.seniverse.com/v3/weather/now.json?key=$weather_api&location=$cityName&language=zh-Hans&unit=c"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onWeatherFailure("请求失败")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val data = response.body?.string()
                    if (data != null) {
                        val weatherInfo = gson.fromJson(data, WeatherTimeResponse::class.java)
                        if (weatherInfo.results.isNotEmpty()) {
                            callback.onWeatherTimeSuccess(weatherInfo)
                        } else {
                            callback.onWeatherFailure("获取天气信息失败")
                        }
                    } else {
                        callback.onWeatherFailure("获取数据失败")
                    }
                } else {
                    callback.onWeatherFailure("请求失败")
                }
            }
        })
    }

    fun getDaily(cityName: String, callback: weatherCallback) {
        val url = "https://api.seniverse.com/v3/weather/daily.json?key=$weather_api&location=$cityName&language=zh-Hans&unit=c&start=0&days=15"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onWeatherFailure("请求失败1")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val data = response.body?.string()
//                    runOnUiThread {
//                        Log.d("aaa", "data: $data")
//                    }
                    callback.onWeatherFailure("data:$data.toString()")
                    if (data != null) {
                        val weatherInfo = gson.fromJson(data, WeatherResponse::class.java)
                        if (weatherInfo.results.isNotEmpty()) {
                            callback.onWeatherSuccess(weatherInfo)
                        } else {
                            callback.onWeatherFailure("获取天气信息失败")
                        }
                    } else {
                        callback.onWeatherFailure("获取数据失败")
                    }
                } else {
                    callback.onWeatherFailure("${response.code}")
                }
            }
        })
    }

    interface weatherCallback {
        fun onWeatherSuccess(weatherInfo: WeatherResponse)
        fun onWeatherFailure(info: String)
        fun onWeatherTimeSuccess(weatherInfo: WeatherTimeResponse)
    }
}