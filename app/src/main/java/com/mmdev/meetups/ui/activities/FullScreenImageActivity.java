package com.mmdev.meetups.ui.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mmdev.meetups.R;
import com.mmdev.meetups.ui.custom_views.TouchImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FullScreenImageActivity extends AppCompatActivity {

	private TouchImageView mImageView;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_full_screen_image);
		bindViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setValues();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.gc();
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}


	private void bindViews(){
		progressDialog = new ProgressDialog(this);
		mImageView = findViewById(R.id.imageView);
	}

	private void setValues(){
		String urlPhotoClick;
		urlPhotoClick = getIntent().getStringExtra("urlPhotoClick");
		Log.i("TAG","image received"+urlPhotoClick);
		Glide.with(this).asBitmap().load(urlPhotoClick).override(640,640).fitCenter().into(new CustomTarget<Bitmap>() {
			@Override
			public void onLoadStarted (@Nullable Drawable placeholder) {
				progressDialog.setMessage("Loading...");
				progressDialog.show();
			}

			@Override
			public void onResourceReady (@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
				progressDialog.dismiss();
				mImageView.setImageBitmap(resource);
			}

			@Override
			public void onLoadCleared (@Nullable Drawable placeholder) {
				Toast.makeText(FullScreenImageActivity.this,"Error",Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
			}
		});
	}

}
