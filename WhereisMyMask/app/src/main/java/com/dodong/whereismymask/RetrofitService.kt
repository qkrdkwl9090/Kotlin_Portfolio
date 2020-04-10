package com.dodong.whereismymask

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService{
    @GET("corona19-masks/v1/storesByAddr/json")
    fun getStore(): Call<ArrayList<StoreByAddressResponse>>
}