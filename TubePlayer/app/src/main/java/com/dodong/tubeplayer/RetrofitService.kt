package com.dodong.tubeplayer

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService{
    @GET("youtube/list/")
    fun getTubeList(): Call<ArrayList<Tube_List>>
}