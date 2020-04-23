package com.dodong.diary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dodong.diary.databinding.ActivityMainBinding
import com.dodong.diary.databinding.Fragment1Binding
import com.dodong.diary.databinding.ItemviewBinding


class Fragment1 : Fragment(R.layout.fragment1) {
    private val data = arrayListOf<Diary>()
    private lateinit var fragment1Binding: Fragment1Binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        data.add(Diary("dfsfsf","1213-1-1"))
        data.add(Diary("dfsfs2f","1213-5-1"))
        data.add(Diary("dfsf3sf","1213-6-1"))

        fragment1Binding = Fragment1Binding.inflate(layoutInflater)//바인딩 부분분
//        val view = fragment1Binding.root

        fragment1Binding.recyclerView.layoutManager = LinearLayoutManager(context)
        fragment1Binding.recyclerView.adapter = MyAdapter(data)

        return inflater.inflate(R.layout.fragment1, container, false)
    }
}

class MyAdapter(private val myDataset: List<Diary>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    class MyViewHolder(val binding: ItemviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview, parent, false) as TextView
        return MyViewHolder(ItemviewBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.itemviewDate.text = myDataset[position].date
    }


    override fun getItemCount() = myDataset.size
}
