package com.dodong.lotto

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.stetho.okhttp3.StethoInterceptor
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        createRetrofit(this)


    }
    fun createRetrofit(activity: Activity) {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.nlotto.co.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        val service = retrofit.create(RetrofitService::class.java)
        Log.d("test1", "retrofit생성")




        service.getLottoNum()
            .enqueue(object : Callback<LottoNumber> {
                override fun onFailure(call: Call<LottoNumber>, t: Throwable) {
                    Log.d("test1", t.toString())
                    Toast.makeText(activity, "통신 실패", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<LottoNumber>,
                    response: Response<LottoNumber>
                ) {
                    Log.d("test1", "응답 성공")
                    if (response.isSuccessful) {
                        val postList = response.body()
                        num1.setText(postList?.drwtNo1!!.toString())
                        num2.setText(postList?.drwtNo2!!.toString())
                        num3.setText(postList?.drwtNo3!!.toString())
                        num4.setText(postList?.drwtNo4!!.toString())
                        num5.setText(postList?.drwtNo5!!.toString())
                        num6.setText(postList?.drwtNo6!!.toString())
                        num7.setText(postList?.bnusNo!!.toString())
                    }
                }

            })
    }

}

