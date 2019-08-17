package com.mmdev.meetapp.utils;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;

public class BindingAdapterUtils {
	@BindingAdapter({"android:src"})
	public static void loadImage(ImageView imageView, int id) {
		Glide.with(imageView.getContext()).load(id).into(imageView);
	}
}
