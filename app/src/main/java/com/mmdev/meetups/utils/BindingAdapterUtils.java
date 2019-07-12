package com.mmdev.meetups.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.databinding.BindingAdapter;

public class BindingAdapterUtils {
	@BindingAdapter({"android:src"})
	public static void loadImage(ImageView imageView, int id) {
		Glide.with(imageView.getContext()).load(id).into(imageView);
	}
}
