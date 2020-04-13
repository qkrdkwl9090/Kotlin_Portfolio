package com.example.ingstargram

import android.app.Person
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService{
    @GET("youtube/list/")
    fun getITubeList():Call<ArrayList<ITube>>

    @GET("melon/list/")
    fun getSongList():Call<ArrayList<Song>>
}