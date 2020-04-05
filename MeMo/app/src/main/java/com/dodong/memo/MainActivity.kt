package com.dodong.memo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dodong.memo.databinding.ActivityMainBinding
import com.dodong.memo.databinding.ItemMemoBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var data = arrayListOf<Memo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MemoAdapter(data)

        binding.addButton.setOnClickListener {
            addMemo()
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }
    private fun addMemo(){
        val memo = Memo(binding.editText.text.toString())
        data.add(memo)
    }
}

data class Memo(val text: String, var isDone: Boolean = false)
class MemoAdapter(private val myDataset: List<Memo>) :
    RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    class MemoViewHolder(val binding: ItemMemoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemoAdapter.MemoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(ItemMemoBinding.bind(view))
    }


    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {

        holder.binding.memoText.text = myDataset[position].text
    }

    override fun getItemCount() = myDataset.size
}

