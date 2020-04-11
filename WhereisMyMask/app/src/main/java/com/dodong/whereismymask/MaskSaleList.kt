package com.dodong.whereismymask

import android.app.Activity
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
import kotlinx.android.synthetic.main.activity_mask_sale_list.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.facebook.stetho.okhttp3.StethoInterceptor

class MaskSaleList : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mask_sale_list)
        createRetrofit(this@MaskSaleList)
        Stetho.initializeWithDefaults(this)
    }

    fun createRetrofit(activity: Activity) {
        Log.d("test1","에러다 에러1")

        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://8oi9s0nnth.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        val service = retrofit.create(RetrofitService::class.java)
        Log.d("test1","에러다 에러2")



        service.getStore().enqueue(object : Callback<StoreByAddressResponse> {
            override fun onFailure(call: Call<StoreByAddressResponse>, t: Throwable) {
                Log.d("test1", t.toString())
                Toast.makeText(activity, "통신 실패", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<StoreByAddressResponse>,
                response: Response<StoreByAddressResponse>
            ) {
                Log.d("test1","성공이다 성공")
                if (response.isSuccessful) {
                    val postList = response.body()?.stores
                    val adatper = PostAdatper(
                        postList!!,
                        LayoutInflater.from(this@MaskSaleList)
                    )
                    recyclerview.adapter = adatper
                    recyclerview.layoutManager = LinearLayoutManager(this@MaskSaleList)
                }
            }

        }
        )
    }
}


class PostAdatper(
    var postList: List<StoreByAddressResponse.Store>,
    val inflater: LayoutInflater
) : RecyclerView.Adapter<PostAdatper.viewHolder>() {
    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        var stock: TextView
        val address: TextView

        init {
            name = itemView.findViewById(R.id.name)
            stock = itemView.findViewById(R.id.stock)
            address = itemView.findViewById(R.id.address)
            Log.d("test1","에러다 에러3")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = inflater.inflate(R.layout.itemview, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var store : StoreByAddressResponse.Store = postList.get(position)
        holder.name.setText(store.getname())
        holder.stock.setText(store.getstat())
        holder.address.setText(store.getaddr())
    }
}