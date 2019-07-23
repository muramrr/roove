package com.mmdev.meetups.ui.card;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mmdev.meetups.models.ProfileModel;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/* Created by A on 20.07.2019.*/

/**
 * This is the documentation block about the class
 */

public class CardsViewModel extends ViewModel {
	
	private static final String USERS_COLLECTION_REFERENCE = "users";
	private static final String USERS_FILTER = "gender";
	private static final String USER_LIKES_COLLECTION_REFERENCE = "likes";
	private static final String USER_SKIPS_COLLECTION_REFERENCE = "skips";
	private static final String USER_MATCHES_COLLECTION_REFERENCE = "matches";
	
	private CollectionReference mUsersCollection;
	private DocumentReference mProfileDocument;
	
	private List<ProfileModel> mAllUsersCards = new ArrayList<>();
	private List<String> mLikedUsersCardsIds = new ArrayList<>();
	private List<String> mSkipedUsersCardsIds = new ArrayList<>();
	
	
	private MutableLiveData<List<ProfileModel>> potentialUsersCards;
	
	public CardsViewModel() {}
	
	public LiveData<List<ProfileModel>> getUsers(String preferedGender, String userId) {
		FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
		mUsersCollection = mFirestore.collection(USERS_COLLECTION_REFERENCE);
		mProfileDocument = mUsersCollection.document(userId);
		if (potentialUsersCards == null) {
			potentialUsersCards = new MutableLiveData<>();
			loadUsers(preferedGender, userId);
		}
		
		return potentialUsersCards;
	}
	
	public LiveData<List<ProfileModel>> getUsers() {
		return potentialUsersCards;
	}
	
	private void loadUsers(String preferedGender, String userId) {
		getAllUsersCards(preferedGender);
		//potentialUsersCards.postValue(returningModels);
	}
	
	
	/*
	* GET ALL USERS
	*/
	private void getAllUsersCards(String preferedGender){
		mUsersCollection.whereEqualTo(USERS_FILTER, preferedGender)
				//.limit(limit)
				.get()
				.addOnCompleteListener(task -> {
					if(task.getResult() != null) {
						for (DocumentSnapshot doc :task.getResult().getDocuments())
							mAllUsersCards.add(doc.toObject(ProfileModel.class));
						getSkipedUsersCards();
						Log.wtf("logs","all on complete, size = "+ mAllUsersCards.size());
					}
				})
				.addOnFailureListener(e -> Log.wtf("logs","all fail"));
	}
	
	/*
	 * GET SKIPED USERS
	 */
	private void getSkipedUsersCards(){
		mProfileDocument.collection(USER_SKIPS_COLLECTION_REFERENCE)
				.get()
				.addOnCompleteListener(task -> {
					if(task.getResult() != null) {
						for (DocumentSnapshot doc :task.getResult().getDocuments())
							mSkipedUsersCardsIds.add(doc.toObject(ProfileModel.class).getUserID());
						getLikedUsersCards();
						Log.wtf("logs","skips on complete, size = "+ mSkipedUsersCardsIds.size());
					}
				})
				.addOnFailureListener(e -> Log.wtf("logs","skipped fail"));
		
	
	}
	
	
	/*
	 * GET LIKED USERS
	 */
	private void getLikedUsersCards(){
		mProfileDocument.collection(USER_LIKES_COLLECTION_REFERENCE)
				.get()
				.addOnCompleteListener(task -> {
					if(task.getResult() != null) {
						for (DocumentSnapshot doc :task.getResult().getDocuments())
							mLikedUsersCardsIds.add(doc.toObject(ProfileModel.class).getUserID());
						
						Log.wtf("logs","likes on complete, size = "+ mLikedUsersCardsIds.size());
						merge();
					}
				})
				.addOnFailureListener(e -> Log.wtf("logs","liked fail"));
		
	}
	
	private void merge(){
		List<String> mergedLikesSkipsCardsIds = new ArrayList<>(mLikedUsersCardsIds);
		mergedLikesSkipsCardsIds.addAll(mSkipedUsersCardsIds);
		List<ProfileModel> returningModelsIds = new ArrayList<>();
		if (mergedLikesSkipsCardsIds.size() != 0) {
			for (ProfileModel profileModel: mAllUsersCards)
				if (!mergedLikesSkipsCardsIds.contains(profileModel.getUserID())) returningModelsIds.add(profileModel);
			Log.wtf("logs","potential users available = " + returningModelsIds.size());
			potentialUsersCards.postValue(returningModelsIds);
		}
		else {
			potentialUsersCards.postValue(mAllUsersCards);
			Log.wtf("logs","likes and skips = 0 ");
		}
		
	}
	
}

