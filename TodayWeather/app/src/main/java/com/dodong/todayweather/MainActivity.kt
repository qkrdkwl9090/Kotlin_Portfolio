package com.dodong.todayweather

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getData(this)
        Stetho.initializeWithDefaults(this)
    }
}

fun getData(activity: Activity){
    val client = OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl("http://www.kma.go.kr/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .client(client)
        .build()
    val service =retrofit.create(RetrofitService::class.java)

    service.getWeatherData().enqueue(object : Callback<weatherData>{
        override fun onFailure(call: Call<weatherData>, t: Throwable) {
            Log.d("test1", t.toString())
            Toast.makeText(activity, "통신 실패", Toast.LENGTH_LONG).show()
        }

        override fun onResponse(
            call: Call<weatherData>,
            response: Response<weatherData>
        ) {
            Log.d("test1", "응답 성공")
            if (response.isSuccessful) {
                Log.d("test1", "응답 성공")
            }
        }

    })
}