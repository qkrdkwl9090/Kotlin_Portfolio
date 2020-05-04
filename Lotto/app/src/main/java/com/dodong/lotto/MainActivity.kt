package com.dodong.lotto

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createRetrofit(this@MainActivity)
        Stetho.initializeWithDefaults(this)


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
                        val adatper = PostAdatper(
                            postList!!,
                            LayoutInflater.from(this@MainActivity)
                        )
                        main_recyclerview.adapter = adatper
                        main_recyclerview.layoutManager = LinearLayoutManager(this@MainActivity)
                    }
                }

            })
    }

}

class PostAdatper(
    var postList: LottoNumber,
    val inflater: LayoutInflater
) : RecyclerView.Adapter<PostAdatper.viewHolder>() {
    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val num1: TextView
        val num2: TextView
        val num3: TextView
        val num4: TextView
        val num5: TextView
        val num6: TextView


        init {
            num1 = itemView.findViewById(R.id.num1)
            num2 = itemView.findViewById(R.id.num2)
            num3 = itemView.findViewById(R.id.num3)
            num4 = itemView.findViewById(R.id.num4)
            num5 = itemView.findViewById(R.id.num5)
            num6 = itemView.findViewById(R.id.num6)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = inflater.inflate(R.layout.itemlist, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.num1.setText(postList.drwtNo1.toString())
        holder.num2.setText(postList.drwtNo2.toString())
        holder.num3.setText(postList.drwtNo3.toString())
        holder.num4.setText(postList.drwtNo4.toString())
        holder.num5.setText(postList.drwtNo5.toString())
    holder.num6.setText(postList.drwtNo6.toString())

}
}
