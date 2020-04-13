package com.example.ingstargram

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication : Application() {

    lateinit var service: RetrofitService
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        createRetrofit()
    }

    fun createRetrofit() {
        val client = OkHttpClient.Builder()//
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        service = retrofit.create(RetrofitService::class.java)
    }
}