package com.dodong.whereismymask

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService{
    @GET("corona19-masks/v1/storesByAddr/json")
    fun getStore(@Query("address") address: String): Call<StoreByAddressResponse>
}