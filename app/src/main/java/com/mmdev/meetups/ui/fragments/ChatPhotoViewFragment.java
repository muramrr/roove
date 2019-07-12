package com.mmdev.meetups.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmdev.meetups.R;
import com.mmdev.meetups.ui.custom_views.SwipeBackFragment;
import com.mmdev.meetups.ui.custom_views.SwipeBackLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/* Created by A on 08.06.2019.*/

/**
 * This is the documentation block about the class
 */

public class ChatPhotoViewFragment extends SwipeBackFragment {

	private OnAddFragmentListener mAddFragmentListener;


	public static ChatPhotoViewFragment newInstance() {

		Bundle args = new Bundle();

		ChatPhotoViewFragment fragment = new ChatPhotoViewFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);


		getSwipeBackLayout().addSwipeListener(new SwipeBackLayout.OnSwipeListener() {
			@Override
			public void onDragStateChange(int state) {
			}

			@Override
			public void onEdgeTouch(int edgeFlag) {
			}

			@Override
			public void onDragScrolled(float scrollPercent) {
			}
		});
		return attachToSwipeBack(view);
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnAddFragmentListener) {
			mAddFragmentListener = (OnAddFragmentListener) context;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mAddFragmentListener = null;
	}

	public interface OnAddFragmentListener { void onAddFragment(Fragment fromFragment, Fragment toFragment);}
}
