package com.example.travelapp.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.travelapp.R

class ViewPagerStartLayoutAdapter(val context: Context): PagerAdapter() {
    private val imageList = listOf(
        R.drawable.start_page_1,
        R.drawable.start_page_2,
        R.drawable.start_page_3
    )

    private val titleList = listOf(
        R.string.title_page_1,
        R.string.title_page_2,
        R.string.title_page_3
    )

    private val descriptionList = listOf(
        R.string.desc_page_1,
        R.string.desc_page_2,
        R.string.desc_page_3
    )
    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: android.view.View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater
        var view = layoutInflater.inflate(R.layout.slider_layout, container, false)
        var imageView = view.findViewById<ImageView>(R.id.image_view_slider)
        var title = view.findViewById<TextView>(R.id.title_slider)
        var description = view.findViewById<TextView>(R.id.description_slider)

        imageView.setImageResource(imageList[position])
        title.setText(titleList[position])
        description.setText(descriptionList[position])

        container.addView(view)


        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as android.view.View)
    }
}