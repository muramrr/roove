package com.mmdev.meetups.ui.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.mmdev.meetups.R;
import com.mmdev.meetups.databinding.ActivityProfileBinding;
import com.mmdev.meetups.ui.main.ProfileViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;

public class ProfileActivity extends AppCompatActivity
{
	@BindView(R.id.inName)
	TextView tv_Name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
		ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
		profileViewModel.init();
		binding.setProfileViewModel(profileViewModel);
		binding.setLifecycleOwner(this);
		profileViewModel.getProfileModel(this).observe(this, profile -> {
			if (profile.getName().length() > 0)
				Toast.makeText(getApplicationContext(), "Name : " + profile.getName(), Toast.LENGTH_SHORT).show();
		});

	}
}
