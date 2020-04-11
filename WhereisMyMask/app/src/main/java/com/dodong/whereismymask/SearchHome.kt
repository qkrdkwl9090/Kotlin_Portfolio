package com.dodong.whereismymask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_search_home.*

class SearchHome() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_home)
        search.setOnClickListener {
            startActivity(
                Intent(this@SearchHome,MaskSaleList::class.java)
            )
        }

    }
}
