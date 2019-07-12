package com.mmdev.meetups.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmdev.meetups.R;
import com.mmdev.meetups.ui.activities.MainActivity;
import com.mmdev.meetups.ui.adapters.CardStackAdapter;
import com.mmdev.meetups.viewmodels.ProfileViewModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class CardFragment extends Fragment {

	private MainActivity mainActivity;
	private ProfileViewModel profileViewModel;


	@Nullable
	@Override
	public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_card, container, false);
	}

	@Override
	public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (getActivity() != null) mainActivity = (MainActivity) getActivity();

		CardStackLayoutManager manager = new CardStackLayoutManager(mainActivity, new CardStackListener() {
			@Override
			public void onCardDragging (Direction direction, float ratio) {

			}

			@Override
			public void onCardSwiped (Direction direction) {

			}

			@Override
			public void onCardRewound () {

			}

			@Override
			public void onCardCanceled () {

			}

			@Override
			public void onCardAppeared (View view, int position) {

			}

			@Override
			public void onCardDisappeared (View view, int position) {

			}
		});
		//ProfileModel profileModel = profileViewModel.getProfileModel(mainActivity).getValue();
		CardStackAdapter adapter = new CardStackAdapter(MainActivity.usersCards);
		//Toast.makeText(mainActivity,String.valueOf(feedManager.getUsersCards()),Toast.LENGTH_SHORT).show();
		CardStackView cardStackView = view.findViewById(R.id.card_stack_view);
		cardStackView.setLayoutManager(manager);
		cardStackView.setAdapter(adapter);
	}

	@Override
	public void onActivityCreated (@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		profileViewModel = ViewModelProviders.of(mainActivity).get(ProfileViewModel.class);
	}
}
