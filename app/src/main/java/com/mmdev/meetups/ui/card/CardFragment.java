package com.mmdev.meetups.ui.card;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
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


/* Todo: move data getters to another class */

public class CardFragment extends Fragment {
	
	private static final String USERS_COLLECTION_REFERENCE = "users";
	private static final String USERS_FILTER = "gender";
	private static final String USER_LIKES_COLLECTION_REFERENCE = "likes";
	private static final String USER_SKIPS_COLLECTION_REFERENCE = "skips";
	private static final String USER_MATCHES_COLLECTION_REFERENCE = "matches";
	
	
	private MainActivity mMainActivity;
	
	
	private FirebaseFirestore mFirestore;
	private CollectionReference mUsersCollection;
	private DocumentReference mProfileDocument;
	
	
	private CardStackView cardStackView;
	private CardStackAdapter mCardStackAdapter;
	private CardStackLayoutManager mCardStackLayoutManager;
	private CardsViewModel mCardsViewModel;
	private List<ProfileModel> mPotentialUsersList;
	private ProfileModel mProfileModel, mSwipeUser;
	private String mPreferedGender;
	private ProgressBar pbStatus;
	private int limit, total_users;
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
		ProfileViewModel mProfileViewModel = ViewModelProviders.of(mMainActivity).get(ProfileViewModel.class);
		mProfileModel = mProfileViewModel.getProfileModel(mMainActivity).getValue();
		
		mCardsViewModel = ViewModelProviders.of(mMainActivity).get(CardsViewModel.class);
		
		mFirestore = FirebaseFirestore.getInstance();
		limit = 1;
		mProgressShowing = false;
		if (mProfileModel != null) {
			showLoadingBar();
			
			mPreferedGender = mProfileModel.getPreferedGender();
			mUsersCollection = mFirestore.collection(USERS_COLLECTION_REFERENCE);
			mProfileDocument = mUsersCollection.document(mProfileModel.getUserID());
			
			mCardsViewModel
					.getUsers(mProfileModel.getPreferedGender(), mProfileModel.getUserID())
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
		
		
		
		mCardStackLayoutManager = new CardStackLayoutManager(mMainActivity, new CardStackListener() {
			
			@Override
			public void onCardAppeared (View view, int position) {
				mSwipeUser = mCardStackAdapter.getSwipeProfile(0);
				Toast.makeText(
						mMainActivity,
						"cards= "+position+" adapter = "+mCardStackAdapter.getItemCount(),
						Toast.LENGTH_SHORT
				).show();
				
			}
		
			@Override
			public void onCardDragging (Direction direction, float ratio) {

			}

			@Override
			public void onCardSwiped (Direction direction) {
				if(direction == Direction.Right)
					mProfileDocument.collection(USER_LIKES_COLLECTION_REFERENCE)
							.document(mSwipeUser.getUserID())
							.set(mSwipeUser);
				else mProfileDocument.collection(USER_SKIPS_COLLECTION_REFERENCE)
						.document(mSwipeUser.getUserID())
						.set(mSwipeUser);
				//mPotentialUsersList.remove(mSwipeUser);
			}

			@Override
			public void onCardRewound () {}

			@Override
			public void onCardCanceled () {}
			
			@Override
			public void onCardDisappeared (View view, int position) {
				
				if (position == mCardStackAdapter.getItemCount()-1) {
					
					showLoadingBar();
				}
			}
		});
		
		cardStackView.setLayoutManager(mCardStackLayoutManager);
		
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
	
	private boolean checkMatch() {
		return true;
	}
	
}
