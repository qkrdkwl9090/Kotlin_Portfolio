package com.dodong.whereismymask

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService{
    @GET("/stores/json/")
    fun getStore(): Call<ArrayList<Store>>
}