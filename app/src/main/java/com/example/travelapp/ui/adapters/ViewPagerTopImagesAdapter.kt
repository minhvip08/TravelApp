package com.example.travelapp.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.travelapp.R
import com.google.android.material.imageview.ShapeableImageView

class ViewPagerTopImagesAdapter(
    val context: Context,
    val images: List<Int>
): PagerAdapter() {
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: android.view.View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater
        var view = layoutInflater.inflate(com.example.travelapp.R.layout.top_images_layout, container, false)
        var imageView = view.findViewById<ShapeableImageView>(R.id.image_view_slider)
        imageView.setImageResource(images[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as android.view.View)
    }
}