package com.dodong.diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_tab.addTab(view_tab.newTab().setText("일기장"))
        view_tab.addTab(view_tab.newTab().setText("일기작성"))


        val adapter = TabAdapter(LayoutInflater.from(this@MainActivity))
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
    }
}

class TabAdapter(
    val layoutInflater: LayoutInflater
) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {//실질적으로 뷰를 그림
        when (position) {
            0 -> {
                val view = layoutInflater.inflate(R.layout.fragment1, container, false)
                container.addView(view)
                return view
            }
            1 -> {
                val view = layoutInflater.inflate(R.layout.fragment2, container, false)
                container.addView(view)
                return view
            }

            else -> {
                val view = layoutInflater.inflate(R.layout.fragment1, container, false)
                container.addView(view)
                return view
            }
        }
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {//뷰가 가려질때 내부적으로 파기되야할때 필요한 함수
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {//화면에 나오는게 내가 만든게 맞는지 확인//===는 더 정확한 비교(주소값까지 비교)
        return view === `object` as View
    }

    override fun getCount(): Int {//pager 갯수
        return 2
    }
}
