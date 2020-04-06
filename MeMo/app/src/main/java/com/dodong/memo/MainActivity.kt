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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dodong.memo.databinding.ActivityMainBinding
import com.dodong.memo.databinding.ItemMemoBinding
import com.firebase.ui.auth.AuthUI

class MainActivity : AppCompatActivity() {

    val RC_SIGN_IN = 1000;
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MemoAdapter(
                emptyList(),
                onClickDeleteIcon = {
                    viewModel.deleteMemo(it)
                },
                onClickItem = {
                    viewModel.toggleItem(it)
                }
            )
        }

        binding.addButton.setOnClickListener {
            val memo = Memo(binding.editText.text.toString())
            viewModel.addMemo(memo)
            binding.editText.text = null
        }
        //관찰 UI 업데이트
        viewModel.memoLiveData.observe(this, Observer {
            (binding.recyclerView.adapter as MemoAdapter).setData(it)

        })
    }

}

data class Memo(
    val text: String,
    var isDone: Boolean = false
)

class MemoAdapter(
    private var myDataset: List<Memo>,
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

    fun setData(newData: List<Memo>){
        myDataset = newData
        notifyDataSetChanged()
    }
}

class MainViewModel : ViewModel() {
    val memoLiveData = MutableLiveData<List<Memo>>()
    private var data = arrayListOf<Memo>()
    fun toggleItem(memo: Memo) {
        memo.isDone = !memo.isDone
        memoLiveData.value = data

    }

    fun addMemo(memo: Memo) {
        data.add(memo)
        memoLiveData.value = data
    }

    fun deleteMemo(memo: Memo) {
        data.remove(memo)
        memoLiveData.value = data
    }
}
