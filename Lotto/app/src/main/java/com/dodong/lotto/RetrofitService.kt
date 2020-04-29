package com.dodong.lotto

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService{
    @GET("common.do?method=getLottoNumber&drwNo=800")
    fun getLottoNum() : Call<LottoNumber>
}
