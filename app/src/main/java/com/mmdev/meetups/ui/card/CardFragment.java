package com.mmdev.meetups.ui.card;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mmdev.meetups.R;
import com.mmdev.meetups.models.ProfileModel;
import com.mmdev.meetups.ui.main.MainActivity;
import com.mmdev.meetups.ui.main.ProfileViewModel;
import com.mmdev.meetups.utils.GlideApp;
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
	
	private MainActivity mMainActivity;
	
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
		
		
		mProgressShowing = false;
		if (profileModel != null) {
			showLoadingBar();
			//get users from viewmodel
			cardsViewModel.init(profileModel);
			cardsViewModel.getPotentialUsers().observe(mMainActivity, profileModelList -> {
				mPotentialUsersList = profileModelList;
				if(mCardStackAdapter == null)
					mCardStackAdapter = new CardStackAdapter(mPotentialUsersList);
				cardStackView.setAdapter(mCardStackAdapter);
				if(mCardStackAdapter.getItemCount() != 0) {
					hideLoadingBar();
					mCardStackAdapter.notifyDataSetChanged();
				}
			});
			
			//handle match event
			cardsViewModel.getMatchedUser().observe(mMainActivity, matchedUser ->{
					Toast.makeText(mMainActivity,"match!",Toast.LENGTH_SHORT).show();
				showMatchDialog(matchedUser);
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
				if(direction == Direction.Right) {
					cardsViewModel.handlePossibleMatch(mSwipeUser);
				}
				else cardsViewModel.addToSkipped(mSwipeUser);
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
	
	private void showMatchDialog(ProfileModel matchUser){
		Dialog matchDialog = new Dialog(mMainActivity);
		matchDialog.setContentView(R.layout.dialog_match);
		//matchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//matchDialog.getWindow().setDimAmount(0.87f);
		matchDialog.show();
		matchDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		ImageView backgr = matchDialog.findViewById(R.id.diag_match_iv_backgr_profile_img);
		GlideApp.with(this).load(matchUser.getMainPhotoUrl()).centerInside().into(backgr);
		matchDialog.findViewById(R.id.diag_match_tv_keep_swp).setOnClickListener(v -> matchDialog.dismiss());
	}
	
	
	
}
