package com.example.weatherapp.view.activity

import com.example.weatherapp.view.fragment.BasePage

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.data.WeatherTimeResponse
import com.example.weatherapp.utils.MyDatabase
import com.example.weatherapp.R
import com.example.weatherapp.utils.WeatherNetwork


class MainActivity : AppCompatActivity() {
    private val todayPage: BasePage = BasePage(1)
    private val tomorrowPage: BasePage = BasePage(2)
    private val afterTomorrowPage: BasePage = BasePage(3)
    private lateinit var weatherNetwork: WeatherNetwork
    private var flag = 1
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
        val item1: MenuItem = menu.findItem(R.id.page_today)
        val item2: MenuItem = menu.findItem(R.id.page_tomorrow)
        val item3: MenuItem = menu.findItem(R.id.page_after_tomorrow)
        val calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        Log.d("aaa", "$year-$month-$day")
        item1.setTitle(calDate(year, month, day, 0))
        item2.setTitle(calDate(year, month, day, 1))
        item3.setTitle(calDate(year, month, day, 2))

        loadView(todayPage)
        bottomTab.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_today -> loadView(todayPage)
                R.id.page_tomorrow -> loadView(tomorrowPage)
                R.id.page_after_tomorrow -> loadView(afterTomorrowPage)
                else -> false
            }
        }

        weatherNetwork = WeatherNetwork(this)

        val cityButton: Button = findViewById(R.id.cityButton)
        val exitButton: Button = findViewById(R.id.exit)
        val cityName: String? = intent.getStringExtra("cityName")
        if (cityName != null) {
            Log.d("aaa", "intent city $cityName")
            requestWeather(cityName)
            cityButton.setText("添加城市")
            exitButton.setText("取消")
            exitButton.setOnClickListener {
                cancel()
            }
            cityButton.setOnClickListener {
                addCity()
            }
        } else {
            if (flag == 1) {
                val dbInfo = MyDatabase(this, "weather",null, 1)
                val infoR: SQLiteDatabase = dbInfo.readableDatabase
                val cursor = infoR.rawQuery("select * from cityData", null)
                if (cursor.moveToFirst()) {
                    val name: String = cursor.getString(cursor.getColumnIndexOrThrow("cityName"))
                    requestWeather(name)
                } else {
                    requestWeather("北京")
                }
                cursor.close()
                dbInfo.close()
                flag = 0
            }
            cityButton.setText("城市管理")
            exitButton.setText("退出")
            exitButton.setOnClickListener {
                exitApp()
            }
            cityButton.setOnClickListener {
                showList()
            }
        }
    }

    private fun cancel() {
        finish()
    }

    private fun exitApp() {
        finish()
    }

    private fun addCity() {
        val ok = intent.getStringExtra("ok")
        if (ok == "n") {
            finish()
            return
        }
        val titleText: TextView = findViewById(R.id.title)
        val ans: String = titleText.text.toString()
        val intent = Intent().apply {
            putExtra(ListActivity.EXTRA_CITY_ADDED, ans)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showList() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
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

    private fun requestWeather(cityName: String) {
        runOnUiThread {
            Log.d("aaa", "cityName: " + cityName)
        }
        if (cityName == "") {
            return
        }
        weatherNetwork.getDaily(cityName, object: WeatherNetwork.weatherCallback {
            override fun onWeatherSuccess(weatherInfo: WeatherResponse) {
                runOnUiThread {
//                    Toast.makeText(this@MainActivity, "请求成功", Toast.LENGTH_SHORT).show()
                    for (i in 0 until weatherInfo.results[0].daily.size) {
                        if (i == 0) {
                            todayPage.setPage(weatherInfo, i)
                            loadView(todayPage)
                        } else if (i == 1) {
                            tomorrowPage.setPage(weatherInfo, i)
                            loadView(tomorrowPage)
                        } else {
                            afterTomorrowPage.setPage(weatherInfo, i)
                            loadView(afterTomorrowPage)
                        }
                    }
                }
            }

            override fun onWeatherFailure(info: String) {
                runOnUiThread {
                    Log.d("aaa", info)
//                    Toast.makeText(this@MainActivity, info, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onWeatherTimeSuccess(weatherInfo: WeatherTimeResponse) {}
        })

        weatherNetwork.getNow(cityName, object: WeatherNetwork.weatherCallback {
            override fun onWeatherTimeSuccess(weatherInfo: WeatherTimeResponse) {
                runOnUiThread {
                    val title: TextView = findViewById(R.id.title)
                    val result = weatherInfo.results[0]
                    title.setText("${result.location.name}     ${result.now.text}  ${result.now.temperature}℃\n更新时间：${result.last_update}")
                }
            }

            override fun onWeatherSuccess(weatherInfo: WeatherResponse) {}

            override fun onWeatherFailure(info: String) {
                runOnUiThread {
                    Log.d("aaa", info)
//                    Toast.makeText(this@MainActivity, info, Toast.LENGTH_SHORT).show()
                }
            }
        })

//        val client = OkHttpClient()Add commentMore actions
//        val url = "https://restapi.amap.com/v3/weather/weatherInfo?key=$weather_api&city=$cityName"
//        val url = "https://api.seniverse.com/v3/weather/daily.json?key=$weather_api&location=$cityName&language=zh-Hans&unit=c&start=0&days=15"
//        val request = Request.Builder().url(url).build()
//
//        client.newCall(request).enqueue(object : okhttp3.Callback {
    }
}
