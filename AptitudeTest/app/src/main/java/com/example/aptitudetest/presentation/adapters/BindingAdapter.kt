package com.example.aptitudetest.presentation.adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

object BindingAdapter {
    @BindingAdapter("app:loadImage")
    @JvmStatic
    fun loadImage(view: View, url: String) {
        (view as ImageView).load(url)
    }
}