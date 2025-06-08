package com.example.weatherapp

data class WeatherResponse(
    val results: List<Result>
)

data class Result(
    val location: Information,
    val daily: List<Daily>,
    val last_update: String
)

data class Information(
    val id: String,
    val name: String,
    val country: String,
    val path: String,
    val timezone: String,
    val timezone_offset: String
)

data class Daily(
    val date: String,
    val text_day: String,
    val code_day: String,
    val text_night: String,
    val code_night: String,
    val high: String,
    val low: String,
    val precip: String,
    val wind_direction: String,
    val wind_direction_degree: String,
    val wind_speed: String,
    val wind_scale: String,
    val rainfall: String,
    val humidity: String
)

//{
//    "results": [{
//    "location": {
//    "id": "WX4FBXXFKE4F",
//    "name": "北京",
//    "country": "CN",
//    "path": "北京,北京,中国",
//    "timezone": "Asia/Shanghai",
//    "timezone_offset": "+08:00"
//},
//    "daily": [{                          //返回指定days天数的结果
//    "date": "2015-09-20",              //日期
//    "text_day": "多云",                //白天天气现象文字
//    "code_day": "4",                  //白天天气现象代码
//    "text_night": "晴",               //晚间天气现象文字
//    "code_night": "0",                //晚间天气现象代码
//    "high": "26",                     //当天最高温度
//    "low": "17",                      //当天最低温度
//    "precip": "0",                    //降水概率，范围0~100，单位百分比（目前仅支持国外城市）
//    "wind_direction": "",             //风向文字
//    "wind_direction_degree": "255",   //风向角度，范围0~360
//    "wind_speed": "9.66",             //风速，单位km/h（当unit=c时）、mph（当unit=f时）
//    "wind_scale": "",                 //风力等级
//    "rainfall": "0.0",                //降水量，单位mm
//    "humidity": "76"                  //相对湿度，0~100，单位为百分比
//}, {
//    "date": "2015-09-21",
//    "text_day": "晴",
//    "code_day": "0",
//    "text_night": "晴",
//    "code_night": "0",
//    "high": "27",
//    "low": "17",
//    "precip": "0",
//    "wind_direction": "",
//    "wind_direction_degree": "157",
//    "wind_speed": "17.7",
//    "wind_scale": "3",
//    "rainfall": "0.0",
//    "humidity": "76"
//}, {
//    ...                               //更多返回结果
//}],
//    "last_update": "2015-09-20T18:00:00+08:00" //数据更新时间（该城市的本地时间）
//}]
//}