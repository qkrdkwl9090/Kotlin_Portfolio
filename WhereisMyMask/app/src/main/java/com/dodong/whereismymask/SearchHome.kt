package com.dodong.whereismymask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_search_home.*
import kotlinx.android.synthetic.main.itemview.*

class SearchHome() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_home)
        search.setOnClickListener {
            val intent = Intent(this@SearchHome, MaskSaleList::class.java)
            val searchAddress = addresssearch.text.toString()
            intent.putExtra("searchAddress", searchAddress)
            Log.d("test1","주소"+searchAddress)
            startActivity(intent)


        }

    }
}
