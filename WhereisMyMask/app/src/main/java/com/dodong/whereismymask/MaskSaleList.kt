package com.dodong.whereismymask

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
import androidx.lifecycle.ViewModel
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MaskSaleList : AppCompatActivity() {

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mask_sale_list)
        createRetrofit(this@MaskSaleList)
        Stetho.initializeWithDefaults(this)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }

    fun createRetrofit(activity: Activity) {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://8oi9s0nnth.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        val service = retrofit.create(RetrofitService::class.java)
        Log.d("test1","retrofit생성")



        val searchaddress = intent.getStringExtra("searchAddress")
        Log.d("test1","받은 주소" +searchaddress)
        service.getStore(searchaddress)
            .enqueue(object : Callback<StoreByAddressResponse> {
            override fun onFailure(call: Call<StoreByAddressResponse>, t: Throwable) {
                Log.d("test1", t.toString())
                Toast.makeText(activity, "통신 실패", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<StoreByAddressResponse>,
                response: Response<StoreByAddressResponse>
            ) {
                Log.d("test1","응답 성공")
                if (response.isSuccessful) {
                    val postList = response.body()?.stores
                    val adatper = PostAdatper(
                        postList!!,
                        LayoutInflater.from(this@MaskSaleList),
                        this@MaskSaleList
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
    val inflater: LayoutInflater,
    val activity: Activity
) : RecyclerView.Adapter<PostAdatper.viewHolder>() {
    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val remain: TextView
        val stock: TextView
        val address: TextView

        init {
            name = itemView.findViewById(R.id.name)
            stock = itemView.findViewById(R.id.stockat)
            address = itemView.findViewById(R.id.address)
            remain = itemView.findViewById(R.id.remain)

            itemView.setOnClickListener {

                val postion: Int = adapterPosition
                val intent = Intent(activity, MapsActivity::class.java)
                intent.putExtra("map_name", postList.get(postion).getname())
                intent.putExtra("map_lat", postList.get(postion).getlat())
                intent.putExtra("map_lng", postList.get(postion).getlng())

                Log.d("test1", "송신 lat값 : " + postList.get(postion).getlat())
                activity.startActivity(intent)
            }
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
        var store: StoreByAddressResponse.Store = postList.get(position)
        holder.name.setText(store.getname())
        holder.stock.setText("최근입고시간 : " + store.getstockAt())
        fun getremainStat(position: Int): String {
            var store: StoreByAddressResponse.Store = postList.get(position)
            val stat = store.getremainStat()
            if (stat == "plenty"){
                holder.remain.setTextColor(Color.GREEN)
                return "100개 이상"
            }
            else if (stat == "some"){
                holder.remain.setTextColor(Color.YELLOW)
                return "30개 이상 100개 미만"
            }

            else if (stat == "few"){
                holder.remain.setTextColor(Color.RED)
                return "2개 이상 30개 미만"
            }
            else if (stat == "empty"){
                holder.remain.setTextColor(Color.GRAY)
                return "1개 이하"
            }
            else if (stat == "break") return "판매중지"
            else return "알수없음"
        }
        holder.remain.setText(getremainStat(position))
        holder.address.setText(store.getaddr())
    }
}
