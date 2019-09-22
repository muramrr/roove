package com.mmdev.meetapp.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mmdev.meetapp.R;
import com.mmdev.meetapp.ui.MainActivity;


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
