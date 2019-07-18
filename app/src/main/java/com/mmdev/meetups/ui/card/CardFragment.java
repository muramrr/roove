package com.mmdev.meetups.ui.card;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mmdev.meetups.R;
import com.mmdev.meetups.models.ProfileModel;
import com.mmdev.meetups.ui.main.MainActivity;
import com.mmdev.meetups.ui.main.ProfileViewModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class CardFragment extends Fragment {
	
	private static final String USERS_COLLECTION_REFERENCE = "users";
	private static final String USERS_FILTER = "gender";
	
	private MainActivity mMainActivity;
	private FirebaseFirestore mFirestore;
	private CollectionReference mUsersCollection;
	private CardStackView cardStackView;
	private CardStackAdapter mCardStackAdapter;
	private CardStackLayoutManager mCardStackLayoutManager;
	private ProfileModel mProfileModel;
	private String mPreferedGender;
	private ProgressBar pbStatus;
	private int limit, total_users;
	
	private static List<ProfileModel> mUsersCards = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_card, container, false);
	}

	@Override
	public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (getActivity() != null) mMainActivity = (MainActivity) getActivity();
		cardStackView = view.findViewById(R.id.card_stack_view);
		pbStatus = view.findViewById(R.id.card_prBar);
		mCardStackAdapter = new CardStackAdapter(mUsersCards);
		ProfileViewModel mProfileViewModel = ViewModelProviders.of(mMainActivity).get(ProfileViewModel.class);
		mProfileModel = mProfileViewModel.getProfileModel(mMainActivity).getValue();
		mFirestore = FirebaseFirestore.getInstance();
		limit = 1;
		if (mProfileModel != null) {
			showLoadingBar();
			mUsersCards.clear();
			getPotentialUsers();
		}
		
		//mCardStackAdapter = new CardStackAdapter(mUsersCards);
		mCardStackLayoutManager = new CardStackLayoutManager(mMainActivity, new CardStackListener() {
			@Override
			public void onCardDragging (Direction direction, float ratio) {

			}

			@Override
			public void onCardSwiped (Direction direction) {
				//if(direction == Direction.Right)

			}

			@Override
			public void onCardRewound () {}

			@Override
			public void onCardCanceled () {}

			@Override
			public void onCardAppeared (View view, int position) {
				Toast.makeText(mMainActivity,
				               mCardStackAdapter.getSwipedProfile(position).getName() + position,
				               Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCardDisappeared (View view, int position) {
				if (position == total_users-1) {
					showLoadingBar();
					getPotentialUsers();
				}
			}
		});
		cardStackView.setLayoutManager(mCardStackLayoutManager);
		cardStackView.setAdapter(mCardStackAdapter);
	}

	@Override
	public void onActivityCreated (@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	

	private void getPotentialUsers(){
		mPreferedGender = mProfileModel.getPreferedGender();
		mUsersCollection = mFirestore.collection(USERS_COLLECTION_REFERENCE);
		mUsersCollection.whereEqualTo(USERS_FILTER, mPreferedGender)
				//.limit(limit)
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful() && task.getResult()!=null) {
						QuerySnapshot result = task.getResult();
						total_users = result.getDocuments().size();
						for (DocumentSnapshot doc :result)
							if (!doc.getId().equals(mProfileModel.getUserID()))
								mUsersCards.add(doc.toObject(ProfileModel.class));
						
						mCardStackAdapter.notifyDataSetChanged();
						mCardStackLayoutManager.setTopPosition(0);
						hideLoadingBar();
					}
				})
				.addOnFailureListener(e -> Toast.makeText(mMainActivity, "Cannot retrieve information", Toast.LENGTH_SHORT).show());
	}
	
	private void showLoadingBar(){
		cardStackView.setVisibility(View.GONE);
		pbStatus.setVisibility(View.VISIBLE);
	}
	
	private void hideLoadingBar(){
		pbStatus.setVisibility(View.GONE);
		cardStackView.setVisibility(View.VISIBLE);
	}

}
