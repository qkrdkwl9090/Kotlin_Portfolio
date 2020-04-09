package com.dodong.whereismymask

import android.app.Activity
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_mask_sale_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MaskSaleList : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mask_sale_list)
        data(this)
    }

    fun data(activity: Activity){

        val retrofit = Retrofit.Builder()
            .baseUrl("http://https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetrofitService::class.java)

        service.getStore().enqueue(object : Callback<ArrayList<Store>>{
            override fun onFailure(call: Call<ArrayList<Store>>, t: Throwable) {
                Toast.makeText(activity,"통신 실패",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Store>>,
                response: Response<ArrayList<Store>>
            ) {
                if (response.isSuccessful) {
                    val postList = response.body()
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
    var postList : ArrayList<Store>,
    val inflater: LayoutInflater
): RecyclerView.Adapter<PostAdatper.viewHolder>() {
    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val type: TextView
        var stock: TextView
        val address: TextView

        init {
            name = itemView.findViewById(R.id.name)
            type = itemView.findViewById(R.id.type)
            stock = itemView.findViewById(R.id.stock)
            address = itemView.findViewById(R.id.address)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = inflater.inflate(R.layout.activity_mask_sale_list, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.name.setText(postList.get(position).name)
        holder.type.setText(postList.get(position).type)
//        holder.stock.setText(postList.get(position).)
        holder.address.setText(postList.get(position).addr)
    }
}