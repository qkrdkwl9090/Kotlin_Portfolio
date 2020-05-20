package com.dodong.lotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RandomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random)
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