package com.example.travelapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.travelapp.R
import com.example.travelapp.ui.adapters.ViewPagerStartLayoutAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StartActivity : AppCompatActivity() {
    lateinit var mSliderViewPager: ViewPager
    lateinit var mDotLayout: LinearLayout
    lateinit var mDots: Array<TextView>
    lateinit var mStartAdapter: ViewPagerStartLayoutAdapter
    lateinit var nextBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        nextBtn = findViewById(R.id.next_btn)

        nextBtn.setOnClickListener {
            if (mSliderViewPager.currentItem == 2){
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                mSliderViewPager.currentItem = getitem(1)
            }

        }

        mSliderViewPager = findViewById(R.id.slider_view_pager)
        mDotLayout = findViewById(R.id.dots_layout)

        mStartAdapter = ViewPagerStartLayoutAdapter(this)
        mSliderViewPager.adapter = mStartAdapter

        setUpIndicator(0)

        mSliderViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) { }

            override fun onPageSelected(position: Int) {
                setUpIndicator(position)

            }

            override fun onPageScrollStateChanged(state: Int) { }

        })



    }

    private fun getitem(i: Int): Int {
        return mSliderViewPager.getCurrentItem() + i
    }

    fun setUpIndicator(position: Int){
            mDots = Array(3){TextView(this)}
            mDotLayout.removeAllViews()
            for (i in mDots.indices){
                mDots[i] = TextView(this)
                mDots[i].text = Html.fromHtml("&#8226")
                mDots[i].textSize = 35f
                mDots[i].setTextColor(resources.getColor(R.color.indicator_inactive_color, applicationContext.theme))
                mDotLayout.addView(mDots[i])
            }
            if (mDots.isNotEmpty()){
                mDots[position].setTextColor(resources.getColor(R.color.indicator_active_color, applicationContext.theme))
            }
    }



}