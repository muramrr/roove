package com.mmdev.meetapp.ui.chat;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.mmdev.meetapp.R;
import com.mmdev.meetapp.utils.GlideApp;

public class FullScreenImageActivity extends AppCompatActivity {

	private PhotoView mImageView;
	private ProgressDialog progressDialog;
	private boolean isHide = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_image);
		bindViews();
	}

	private void bindViews(){
		progressDialog = new ProgressDialog(this);
		mImageView = findViewById(R.id.imageView);
		mImageView.setOnClickListener(v -> fullScreenCall());
		// Set the content to appear under the system bars so that the
		// content doesn't resize when the system bars hide and show.
		mImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	private void setValues(){
		String urlPhotoClick;
		urlPhotoClick = getIntent().getStringExtra("urlPhotoClick");
		Log.i("TAG","image received" + urlPhotoClick);
		GlideApp.with(this).load(urlPhotoClick).into(mImageView);
	}

	//hide bottom navigation to see fullscreen image
	public void fullScreenCall () {
		if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
			// lower api
			View v = this.getWindow().getDecorView();
			if (v.getSystemUiVisibility() == View.VISIBLE)
				v.setSystemUiVisibility(View.GONE);
			else v.setSystemUiVisibility(View.VISIBLE);
		} else if(Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			View decorView = getWindow().getDecorView();
			if (!isHide){
				decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
				isHide = true;
			}
			else {
				decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
				isHide = false;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setValues();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Runtime.getRuntime().gc();
		finish();
	}
}
