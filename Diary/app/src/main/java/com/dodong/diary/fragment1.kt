package com.dodong.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dodong.diary.databinding.ActivityMainBinding
import com.dodong.diary.databinding.Fragment1Binding


class Fragment1 : Fragment() {
    private lateinit var fragment1Binding: Fragment1Binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragment1Binding = Fragment1Binding.inflate(layoutInflater)//바인딩 부분분
        val view = fragment1Binding.root
        setC





        return inflater.inflate(R.layout.fragment1, container, false)
    }
}
