package com.dodong.memo

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
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
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val RC_SIGN_IN = 1000;
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        if (FirebaseAuth.getInstance().currentUser == null) {
            login()
        }


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == Activity.RESULT_OK) {
                viewModel.fetchData()

            } else {
                //로그인 실패나 안했을 시
                finish()
            }
        }
    }

    fun login() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                login()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_log_out -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

data class Memo(
    val text: String,
    var isDone: Boolean = false
)

class MemoAdapter(
    private var myDataset: List<DocumentSnapshot>,
    val onClickDeleteIcon: (memo: DocumentSnapshot) -> Unit,
    val onClickItem: (memo: DocumentSnapshot) -> Unit
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
        holder.binding.memoText.text = memo.getString("text") ?: ""
        if ((memo.getBoolean("isDone") ?: false) == true) {
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

    fun setData(newData: List<DocumentSnapshot>) {
        myDataset = newData
        notifyDataSetChanged()
    }
}

class MainViewModel : ViewModel() {
    val db = Firebase.firestore
    val memoLiveData = MutableLiveData<List<DocumentSnapshot>>()

    init {
        fetchData()
    }

    fun fetchData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            db.collection(user.uid)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        memoLiveData.value = value.documents
                    }
                }

        }
    }

    fun toggleItem(memo: DocumentSnapshot) {
        FirebaseAuth.getInstance().currentUser?.let {
            val isDone = memo.getBoolean("isDone") ?: false
            db.collection(it.uid).document(memo.id).update("isDone", !isDone)
        }

    }

    fun addMemo(memo: Memo) {
        FirebaseAuth.getInstance().currentUser?.let {
            db.collection(it.uid).add(memo)
        }
    }

    fun deleteMemo(memo: DocumentSnapshot) {
        FirebaseAuth.getInstance().currentUser?.let {
            db.collection(it.uid).document(memo.id).delete()
        }
    }
}
