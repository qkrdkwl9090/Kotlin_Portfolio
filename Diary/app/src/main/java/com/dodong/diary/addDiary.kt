package com.dodong.diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_add_diary.*
import java.text.SimpleDateFormat
import java.util.*

class addDiary : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_diary)

        addDiary_ok.setOnClickListener {
            val intent = Intent(this@addDiary, MainActivity::class.java)
            val content = addDiary_content.text.toString()
            intent.putExtra("content", content)


        }

        fun date(): String {
            var date: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
            val time: Date = Date()
            return date.format(time)
        }
    }
}


