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
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    private val weather_api = "Sr-c5V7NWuYCNInth"
    private val datePage1: DatePage1 = DatePage1()
    private val datePage2: DatePage2 = DatePage2()
    private val datePage3: DatePage3 = DatePage3()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomTab: BottomNavigationView = findViewById(R.id.bottomTab)
        val menu = bottomTab.menu
        val item1: MenuItem = menu.findItem(R.id.page_date1)
        val item2: MenuItem = menu.findItem(R.id.page_date2)
        val item3: MenuItem = menu.findItem(R.id.page_date3)
        val calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        Log.d("aaa", "$year-$month-$day")
        item1.setTitle(calDate(year, month, day, 0))
        item2.setTitle(calDate(year, month, day, 1))
        item3.setTitle(calDate(year, month, day, 2))

        loadView(DatePage1())
        bottomTab.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_date1 -> loadView(datePage1)
                R.id.page_date2 -> loadView(datePage2)
                R.id.page_date3 -> loadView(datePage3)
                else -> false
            }
        }

        val searchButton: Button = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            selectWeather()
        }
    }

    private fun calDate(year: Int, month: Int, day: Int, sum: Int): String {
        val month1 = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val month2 = listOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var y: Int = year
        var m: Int = month
        var d: Int = day
        d += sum
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
            while (d > month2[m]) {
                d -= month2[m];
                m = (m + 1) % 12
                if (m == 0) {
                    ++y
                }
            }
            ++m
            return "$y-$m-$d"
        } else {
            while (d > month1[m]) {
                d -= month1[m];
                m = (m + 1) % 12
                if (m == 0) {
                    ++y
                }
            }
            ++m
            return "$y-$m-$d"
        }
    }

    private fun loadView(view: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.content, view).commit()
        return true
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
        val url = "https://api.seniverse.com/v3/weather/daily.json?key=$weather_api&location=$cityName&language=zh-Hans&unit=c&start=0&days=15"
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
//                    Log.d("aaa", weatherInfo.infoCode.toString())
                    if (weatherInfo.results.isNotEmpty()) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "请求成功", Toast.LENGTH_SHORT).show()
                            for (i in 0 until weatherInfo.results[0].daily.size) {
                                if (i == 0) {
                                    datePage1.setPage(weatherInfo, i)
                                    loadView(datePage1)
                                } else if (i == 1) {
                                    datePage2.setPage(weatherInfo, i)
                                } else {
                                    datePage3.setPage(weatherInfo, i)
                                }
                            }
                        }
//                        val showText: TextView = findViewById(R.id.showText)
//                        showText.setText("城市：${result.location.name}\n\n天气：${result.daily[0].text_day}\n\n温度：${result.last_update}\n")
////                        val searchText: EditText = findViewById(R.id.searchText)
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
