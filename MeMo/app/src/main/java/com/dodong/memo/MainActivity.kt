package com.dodong.memo

import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dodong.memo.databinding.ActivityMainBinding
import com.dodong.memo.databinding.ItemMemoBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MemoAdapter(
                viewModel.data,
                onClickDeleteIcon = {
                    viewModel.deleteMemo(it)
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                },
                onClickItem = {
                    viewModel.toggleItem(it)
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            )
        }

        binding.addButton.setOnClickListener {
            val memo = Memo(binding.editText.text.toString())
            viewModel.addMemo(memo)
            binding.editText.text = null
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

}

data class Memo(
    val text: String,
    var isDone: Boolean = false
)

class MemoAdapter(
    private val myDataset: List<Memo>,
    val onClickDeleteIcon: (memo: Memo) -> Unit,
    val onClickItem: (memo: Memo) -> Unit
) :
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
        val memo = myDataset[position]
        holder.binding.memoText.text = memo.text
        if (memo.isDone) {
            holder.binding.memoText.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC)
            }
        } else {
            holder.binding.memoText.apply {
                paintFlags = 0
                setTypeface(null, Typeface.NORMAL)
            }
        }


        holder.binding.deleteImageView.setOnClickListener {
            onClickDeleteIcon.invoke(memo)
        }
        holder.binding.root.setOnClickListener {
            onClickItem.invoke(memo)

        }

    }

    override fun getItemCount() = myDataset.size
}

class MainViewModel : ViewModel() {
    var data = arrayListOf<Memo>()
    fun toggleItem(memo: Memo) {
        memo.isDone = !memo.isDone

    }

    fun addMemo(memo: Memo) {
        data.add(memo)
    }

    fun deleteMemo(memo: Memo) {
        data.remove(memo)

    }
}
