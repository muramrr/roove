package com.mmdev.meetups.ui.card;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mmdev.meetups.R;
import com.mmdev.meetups.ui.main.MainActivity;
import com.mmdev.meetups.ui.main.ProfileViewModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class CardFragment extends Fragment {

	private MainActivity mMainActivity;
	private ProfileViewModel profileViewModel;
	private CardStackAdapter mCardStackAdapter;
	private int position = 0;

	@Nullable
	@Override
	public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_card, container, false);
	}

	@Override
	public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (getActivity() != null) mMainActivity = (MainActivity) getActivity();
		mCardStackAdapter = new CardStackAdapter(MainActivity.usersCards);
		CardStackLayoutManager manager = new CardStackLayoutManager(mMainActivity, new CardStackListener() {
			@Override
			public void onCardDragging (Direction direction, float ratio) {

			}

			@Override
			public void onCardSwiped (Direction direction) {
				if(direction == Direction.Right)
					Toast.makeText(mMainActivity, mCardStackAdapter.getSwipedProfile(position).getName(), Toast.LENGTH_SHORT).show();
				else Toast.makeText(mMainActivity, String.valueOf(position), Toast.LENGTH_SHORT).show();
				position++;

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
		//ProfileModel profileModel = profileViewModel.getProfileModel(mMainActivity).getValue();

		//Toast.makeText(mMainActivity,String.valueOf(feedManager.getUsersCards()),Toast.LENGTH_SHORT).show();
		CardStackView cardStackView = view.findViewById(R.id.card_stack_view);
		cardStackView.setLayoutManager(manager);
		cardStackView.setAdapter(mCardStackAdapter);
	}

	@Override
	public void onActivityCreated (@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		profileViewModel = ViewModelProviders.of(mMainActivity).get(ProfileViewModel.class);
	}

}
