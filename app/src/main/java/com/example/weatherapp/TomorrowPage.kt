package com.example.weatherapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.util.Log
import com.example.weatherapp.Data.WeatherResponse

class TomorrowPage : Fragment() {
    private var dateView: TextView? = null
    private var info: String = ""
    private var ok: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("aaa", "datePage2")
        val view = inflater.inflate(R.layout.page_tomorrow, container, false)
        dateView = view.findViewById(R.id.dateView)
        ok = true
        dateView?.setText(info)
        return view
    }

    fun setPage(weatherInfo: WeatherResponse, pos: Int) {
        val result = weatherInfo.results[0]
        val date = result.daily[pos]
        info = "城市：${result.location.name}\n\n白天天气：${date.text_day}\n\n夜间天气：${date.text_night}\n\n最高温度：${date.high}℃ 最低温度：${date.low}℃\n\n降水概率：${date.precip}%\n\n降水量：${date.rainfall}\n\n风向：${date.wind_direction}风 风力等级：${date.wind_scale}级\n\n相对湿度：${date.humidity}"
        if (ok) {
            Log.d("aaa", "datePage2 ok")
            dateView?.setText(info)
        }
//        dateView.setText(info)
//        dateView.setText("城市：${result.location.name}\n天气：${date.text_day}\n最高温度：${date.high}  最低温度：${date.low}\n")
    }
}