package com.dodong.todayweather

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService{
    @GET("weather/forecast/mid-term-rss3.jsp?stnId=108")
    fun getWeatherData() : Call<List<ShortWeather>>
}