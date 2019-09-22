package com.mmdev.meetapp.utils;

import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;

public class BindingAdapterUtils {

	@BindingAdapter({"android:src"})
	public static void loadImage(@NonNull ImageView imageView, int id) {
//		if (TextUtils.isEmpty(url)) {
//			return;
//		}
		Glide.with(imageView.getContext()).load(id).into(imageView);
	}
}
