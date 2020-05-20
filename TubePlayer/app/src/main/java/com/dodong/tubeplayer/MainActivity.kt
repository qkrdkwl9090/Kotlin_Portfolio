package com.dodong.tubeplayer

import android.app.Activity
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var glide : RequestManager
    lateinit var service: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Stetho.initializeWithDefaults(this)
        createRetrofit(this)
    }

    fun createRetrofit(activity: Activity) {
        val client = OkHttpClient.Builder()//
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        service = retrofit.create(RetrofitService::class.java)
        service.getTubeList().enqueue(object : Callback<ArrayList<Tube_List>>{
            override fun onFailure(call: Call<ArrayList<Tube_List>>, t: Throwable) {
                Toast.makeText(this@MainActivity,"실패",Toast.LENGTH_LONG)
            }

            override fun onResponse(
                call: Call<ArrayList<Tube_List>>,
                response: Response<ArrayList<Tube_List>>
            ) {
                if(response.isSuccessful){
                    glide = Glide.with(this@MainActivity)
                    val tubeList = response.body()
                    val adapter = tubeAdapter(
                        tubeList!!,
                        LayoutInflater.from(this@MainActivity),
                        glide,
                        this@MainActivity
                    )
                }
            }
        })

    }

}

class tubeAdapter(
    var tubeList : ArrayList<Tube_List>,
    val inflater : LayoutInflater,
    val glide: RequestManager,
    val activity: Activity
) : RecyclerView.Adapter<tubeAdapter.ViewHolder>(){
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title: TextView
        val thumnail: ImageView
        val content: TextView
        init{
            title = itemView.findViewById(R.id.itube_title)
            thumnail = itemView.findViewById(R.id.itube_thumbnail)
            content = itemView.findViewById(R.id.itube_content)

            itemView.setOnClickListener{
                val position: Int = adapterPosition
                val intent = Intent(activity, Tube_detail::class.java)
                intent.putExtra("video_url",tubeList.get(position).video)
                intent.putExtra("video_title",tubeList.get(position).title)
                intent.putExtra("video_content",tubeList.get(position).content)
                activity.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.tube_itemview, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tubeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(tubeList.get(position).title)
        holder.content.setText(tubeList.get(position).content)
        glide.load(tubeList.get(position).thumbnail).into(holder.thumnail)
    }
}