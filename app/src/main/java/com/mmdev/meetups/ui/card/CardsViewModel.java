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
 * get users from firebase firestore
 * getAllUsers -> getSkipedUsers -> getLikedUsers -> getMatchedUsers -> mergeLikedSkipedMatched ->
 * -> create new list from getAllUsersCards list that does not contains mergedLikedSkipedMatched items
 * -> postValue into LiveData variable ... else return null and show loading bar in Fragment class
 * TODO: MAKE ASYNC DATA RETRIEVE AND FETCH DYNAMICALLY
 */

public class CardsViewModel extends ViewModel {
	
	private static final String USERS_COLLECTION_REFERENCE = "users";
	private static final String USERS_FILTER = "gender";
	private static final String USER_LIKES_COLLECTION_REFERENCE = "likes";
	private static final String USER_SKIPS_COLLECTION_REFERENCE = "skips";
	private static final String USER_MATCHES_COLLECTION_REFERENCE = "matches";
	
	private ProfileModel mCurrentUser;
	private String mCurrentUserId;
	
	private CollectionReference mUsersCollectionRef;
	private DocumentReference mCurrentProfileDocRef;
	
	private List<ProfileModel> mAllUsersCards = new ArrayList<>();
	private List<String> mSkipedUsersCardsIds = new ArrayList<>();
	private List<String> mLikedUsersCardsIds = new ArrayList<>();
	private List<String> mMatchedUsersCardsIds = new ArrayList<>();
	
	
	private MutableLiveData<List<ProfileModel>> potentialUsersCards;
	private MutableLiveData<ProfileModel> matchedUser;
	
	public CardsViewModel() {}
	
	void init (ProfileModel currentUser){
		mCurrentUser = currentUser;
		mCurrentUserId = mCurrentUser.getUserID();
		FirebaseFirestore firestore = FirebaseFirestore.getInstance();
		mUsersCollectionRef = firestore.collection(USERS_COLLECTION_REFERENCE);
		mCurrentProfileDocRef = mUsersCollectionRef.document(mCurrentUser.getUserID());
		//paris like, new york like, paris skip
		firestore.collection(USERS_COLLECTION_REFERENCE).document("axrknsctqj").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId).set(mCurrentUser);
		firestore.collection(USERS_COLLECTION_REFERENCE).document("lrgctefskb").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId).set(mCurrentUser);
		firestore.collection(USERS_COLLECTION_REFERENCE).document("iwdvqluegl").collection(USER_SKIPS_COLLECTION_REFERENCE).document(mCurrentUserId).set(mCurrentUser);
	}
	
	LiveData<List<ProfileModel>> getPotentialUsers () {
		if (potentialUsersCards == null) {
			potentialUsersCards = new MutableLiveData<>();
			loadUsers(mCurrentUser.getPreferedGender());
		}
		
		return potentialUsersCards;
	}
	
	LiveData<ProfileModel> getMatchedUser (){
		if (matchedUser == null) {
			matchedUser = new MutableLiveData<>();
		}
		return matchedUser;
	}
	
	private void loadUsers(String preferedGender) {
		getAllUsersCards(preferedGender);
		//potentialUsersCards.postValue(returningModels);
	}
	
	
	/*
	* GET ALL USERS
	*/
	private void getAllUsersCards(String preferedGender){
		mUsersCollectionRef.whereEqualTo(USERS_FILTER, preferedGender)
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
		mCurrentProfileDocRef.collection(USER_SKIPS_COLLECTION_REFERENCE)
				.get()
				.addOnCompleteListener(task -> {
					if(task.getResult() != null) {
						for (DocumentSnapshot doc :task.getResult().getDocuments())
							mSkipedUsersCardsIds.add(doc.getId());
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
		mCurrentProfileDocRef.collection(USER_LIKES_COLLECTION_REFERENCE)
				.get()
				.addOnCompleteListener(task -> {
					if(task.getResult() != null) {
						for (DocumentSnapshot doc :task.getResult().getDocuments())
							mLikedUsersCardsIds.add(doc.getId());
						
						Log.wtf("logs","likes on complete, size = "+ mLikedUsersCardsIds.size());
						getMatchedUsersCards();
					}
				})
				.addOnFailureListener(e -> Log.wtf("logs","liked fail"));
		
	}
	
	/*
	 * GET MATCHED
	 */
	private void getMatchedUsersCards(){
		mCurrentProfileDocRef.collection(USER_MATCHES_COLLECTION_REFERENCE)
				.get()
				.addOnCompleteListener(task -> {
					if(task.getResult() != null) {
						for (DocumentSnapshot doc :task.getResult().getDocuments())
							mMatchedUsersCardsIds.add(doc.getId());
						
						Log.wtf("logs","matches on complete, size = "+ mMatchedUsersCardsIds.size());
						mergeLikedSkipedMatched();
					}
				})
				.addOnFailureListener(e -> Log.wtf("logs","matches fail"));
		
	}
	
	//merge liked, skiped, matched lists
	private void mergeLikedSkipedMatched (){
		List<String> mergedLikesSkipsCardsIds = new ArrayList<>(mLikedUsersCardsIds);
		mergedLikesSkipsCardsIds.addAll(mSkipedUsersCardsIds);
		mergedLikesSkipsCardsIds.addAll(mMatchedUsersCardsIds);
		if (mergedLikesSkipsCardsIds.size() != 0) {
			List<ProfileModel> returningModelsIds = new ArrayList<>();
			for (ProfileModel profileModel: mAllUsersCards)
				if (!mergedLikesSkipsCardsIds.contains(profileModel.getUserID())) returningModelsIds.add(profileModel);
			Log.wtf("logs","potential users available = " + returningModelsIds.size());
			potentialUsersCards.postValue(returningModelsIds);
		}
		else {
			potentialUsersCards.postValue(mAllUsersCards);
			Log.wtf("logs","likes + skips + matches = 0 ");
		}
		
	}
	
	/*
	* check if users liked each other
	*/
	void handlePossibleMatch (ProfileModel likedUser) {
		String uId = likedUser.getUserID();
		mUsersCollectionRef.document(uId)
				.collection(USER_LIKES_COLLECTION_REFERENCE)
				.document(mCurrentUserId)
				.get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						//add to match collection
						mUsersCollectionRef
								.document(uId)
								.collection(USER_MATCHES_COLLECTION_REFERENCE)
								.document(mCurrentUserId)
								.set(mCurrentUser);
						mCurrentProfileDocRef
								.collection(USER_MATCHES_COLLECTION_REFERENCE)
								.document(uId)
								.set(likedUser);
						matchedUser.setValue(likedUser);
						//remove from like collection
						mUsersCollectionRef
								.document(uId)
								.collection(USER_LIKES_COLLECTION_REFERENCE)
								.document(mCurrentUserId)
								.delete();
						mCurrentProfileDocRef
								.collection(USER_LIKES_COLLECTION_REFERENCE)
								.document(uId)
								.delete();
						Log.wtf("logs", "match handle executed");
						
					}
					else mUsersCollectionRef
							.document(mCurrentUserId)
							.collection(USER_LIKES_COLLECTION_REFERENCE)
							.document(uId)
							.set(likedUser);
					
				})
				.addOnFailureListener(e -> {
					Log.wtf("logs","handlePossibleMatch fail");
				});
		
		
	}
	
	void addToSkipped (ProfileModel skipedUser){
		mCurrentProfileDocRef.collection(USER_SKIPS_COLLECTION_REFERENCE)
				.document(skipedUser.getUserID())
				.set(skipedUser);
	}
	
	
}

