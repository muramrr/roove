package com.mmdev.meetups.ui.card;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mmdev.meetups.R;
import com.mmdev.meetups.models.ProfileModel;
import com.mmdev.meetups.ui.main.MainActivity;
import com.mmdev.meetups.ui.main.ProfileViewModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;



public class CardFragment extends Fragment {
	
	private static final String USERS_COLLECTION_REFERENCE = "users";
	private static final String USER_LIKES_COLLECTION_REFERENCE = "likes";
	private static final String USER_SKIPS_COLLECTION_REFERENCE = "skips";
	private static final String USER_MATCHES_COLLECTION_REFERENCE = "matches";
	
	
	private MainActivity mMainActivity;
	
	private DocumentReference mProfileDocumentRef;
	
	private CardStackView cardStackView;
	private CardStackAdapter mCardStackAdapter;
	private List<ProfileModel> mPotentialUsersList;
	private ProfileModel mSwipeUser;
	private ProgressBar pbStatus;
	
	private boolean mProgressShowing;
	
	

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
		
		ProfileViewModel profileViewModel = ViewModelProviders.of(mMainActivity).get(ProfileViewModel.class);
		ProfileModel profileModel = profileViewModel.getProfileModel(mMainActivity).getValue();
		
		CardsViewModel cardsViewModel = ViewModelProviders.of(mMainActivity).get(CardsViewModel.class);
		
		FirebaseFirestore firestore = FirebaseFirestore.getInstance();
		mProgressShowing = false;
		if (profileModel != null) {
			showLoadingBar();
			
			//current profile reference in firestore
			mProfileDocumentRef = firestore
					.collection(USERS_COLLECTION_REFERENCE)
					.document(profileModel.getUserID());
			
			//get users from viewmodel
			cardsViewModel.getUsers(profileModel.getPreferedGender(), profileModel.getUserID())
					.observe(mMainActivity, profileModelList -> {
						mPotentialUsersList = profileModelList;
						if(mCardStackAdapter == null)
							mCardStackAdapter = new CardStackAdapter(mPotentialUsersList);
						cardStackView.setAdapter(mCardStackAdapter);
						if(mCardStackAdapter.getItemCount() != 0) {
							hideLoadingBar();
							mCardStackAdapter.notifyDataSetChanged();
						}
					});
			
		}
		
		
		
		CardStackLayoutManager cardStackLayoutManager = new CardStackLayoutManager(mMainActivity, new CardStackListener() {
			
			@Override
			public void onCardAppeared (View view, int position) {
				//get current displayed on card profile
				mSwipeUser = mCardStackAdapter.getSwipeProfile(position);
			}
		
			@Override
			public void onCardDragging (Direction direction, float ratio) {

			}

			@Override
			public void onCardSwiped (Direction direction) {
				//if right = add to liked
				//else = add to skiped
				if(direction == Direction.Right)
					if (cardsViewModel.checkMatch(mSwipeUser.getUserID()))
						mProfileDocumentRef.collection(USER_LIKES_COLLECTION_REFERENCE)
								.document(mSwipeUser.getUserID())
								.set(mSwipeUser);
				else mProfileDocumentRef.collection(USER_SKIPS_COLLECTION_REFERENCE)
						.document(mSwipeUser.getUserID())
						.set(mSwipeUser);
				mCardStackAdapter.notifyDataSetChanged();
				mPotentialUsersList.remove(mSwipeUser);
			}

			@Override
			public void onCardRewound () {}

			@Override
			public void onCardCanceled () {}
			
			@Override
			public void onCardDisappeared (View view, int position) {
				//if there is no available user to show - show loading
				if (position == mCardStackAdapter.getItemCount()-1) {
					mCardStackAdapter.notifyDataSetChanged();
					showLoadingBar();
				}
			}
			
		});
		
		cardStackView.setLayoutManager(cardStackLayoutManager);
		
	}

	@Override
	public void onActivityCreated (@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void showLoadingBar(){
		if (!mProgressShowing) {
			cardStackView.setVisibility(View.GONE);
			pbStatus.setVisibility(View.VISIBLE);
			mProgressShowing = true;
		}
	}
	
	private void hideLoadingBar() {
		if (mProgressShowing){
			pbStatus.setVisibility(View.GONE);
			cardStackView.setVisibility(View.VISIBLE);
			mProgressShowing = false;
		}
	}
	
	
	
	
}
