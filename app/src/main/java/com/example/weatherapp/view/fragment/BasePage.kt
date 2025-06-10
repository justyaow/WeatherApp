package com.example.weatherapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherResponse

class BasePage(private val index: Int) : Fragment() {
    private var dateView: TextView? = null
    private var info: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View
        if (index == 1) {
            view = inflater.inflate(R.layout.page_today, container, false)
        } else if (index == 2) {
            view = inflater.inflate(R.layout.page_tomorrow, container, false)
        } else {
            view = inflater.inflate(R.layout.page_after_tomorrow, container, false)
        }
        dateView = view.findViewById(R.id.dateView)
        dateView?.setText(info)
        return view
    }

    fun setPage(weatherInfo: WeatherResponse, pos: Int) {
        val result = weatherInfo.results[0]
        val date = result.daily[pos]
        info = "城市：${result.location.name}\n\n白天天气：${date.text_day}\n\n夜间天气：${date.text_night}\n\n最高温度：${date.high}℃ 最低温度：${date.low}℃\n\n降水概率：${date.precip}%\n\n降水量：${date.rainfall}\n\n风向：${date.wind_direction}风 风力等级：${date.wind_scale}级\n\n相对湿度：${date.humidity}"
        if (dateView != null) {
            dateView?.setText(info)
        }
    }
}