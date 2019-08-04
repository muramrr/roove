package com.mmdev.meetups.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmdev.meetups.R;
import com.mmdev.meetups.ui.main.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class MessagesFragment extends Fragment {
	
	
	private MainActivity mMainActivity;
	
	@Nullable
	@Override
	public View onCreateView (@NonNull LayoutInflater inflater,
							  @Nullable ViewGroup container,
							  @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_messages, container, false);
	}
	
	@Override
	public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (getActivity() != null) mMainActivity = (MainActivity) getActivity();
		
		
	}
}
