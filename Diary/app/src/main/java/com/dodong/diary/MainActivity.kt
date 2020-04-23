package com.dodong.diary

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.RequestManager
import com.dodong.diary.databinding.ActivityMainBinding
import com.dodong.diary.databinding.Fragment1Binding
import com.dodong.diary.databinding.ItemviewBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment1.*
import kotlinx.android.synthetic.main.fragment1.view.*
import kotlinx.android.synthetic.main.fragment1.view.recyclerView
import kotlinx.android.synthetic.main.fragment2.*
import kotlinx.android.synthetic.main.itemview.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var glide : RequestManager
    private lateinit var auth: FirebaseAuth
    val RC_SIGN_IN = 1000
//
    private lateinit var binding: ActivityMainBinding // 바인딩




//    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)//바인딩 부분분
        val view = binding.root
        setContentView(view)


        val auth = FirebaseAuth.getInstance()
        if (FirebaseAuth.getInstance().currentUser == null) {
            login()
        }

        binding.viewTab.view_tab.addTab(view_tab.newTab().setText("일기장"))
        binding.viewTab.view_tab.addTab(view_tab.newTab().setText("일기작성"))


        val adapter = TabAdapter(supportFragmentManager, 2)

        view_pager.adapter = adapter
        view_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {//탭이 클릭됬을 때
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabSelected(tab: TabLayout.Tab) {
                view_pager.setCurrentItem(tab.position)
            }
        })
        view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view_tab))


//        addDiary_ok.setOnClickListener{
//            fun date(): String {
//                var date: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
//                val time: Date = Date()
//                return date.format(time)
//            }
//            val content = addDiary_content.text.toString()
//            val diary = Diary(content, date()
//            viewModel.addDiary(diary)
//        }




    }
    fun login(){
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }


}

class TabAdapter(
    fragmentManager: FragmentManager,
    val tabCount: Int
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        when (position){
            0->{
                return Fragment1()
            }
            1->{
                return Fragment2()
            }
            else -> return Fragment1()
        }
    }

    override fun getCount(): Int {//pager 갯수
        return 2
    }
}




class Diary(
    val content: String,
    val date : String
){

}

//
//class MainViewModel : ViewModel() {
//    val db = Firebase.firestore
//    val memoLiveData = MutableLiveData<List<DocumentSnapshot>>()
//
//    init {
//        fetchData()
//    }
//
//    fun fetchData() {
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            db.collection(user.uid)
//                .addSnapshotListener { value, e ->
//                    if (e != null) {
//                        return@addSnapshotListener
//                    }
//                    if (value != null) {
//                        memoLiveData.value = value.documents
//                    }
//                }
//
//        }
//    }
//
//
//    fun addDiary(diary: Diary) {
//        FirebaseAuth.getInstance().currentUser?.let {
//            db.collection(it.uid).add(diary)
//        }
//    }
//
//    fun deleteDiary(diary: DocumentSnapshot) {
//        FirebaseAuth.getInstance().currentUser?.let {
//            db.collection(it.uid).document(diary.id).delete()
//        }
//    }
//}
