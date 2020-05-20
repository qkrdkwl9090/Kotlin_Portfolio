package com.dodong.tubeplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_tube_detail.*

class Tube_detail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tube_detail)
        val title = intent.getStringExtra("video_title")
        Log.d("titlez",title)
        detail_title.setText(title)
        val content = intent.getStringExtra("video_content")
        detail_content.setText(content)

        val url = intent.getStringExtra("video_url")

    }
}
