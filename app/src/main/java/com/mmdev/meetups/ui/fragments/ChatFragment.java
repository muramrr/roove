package com.mmdev.meetups.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mmdev.meetups.BuildConfig;
import com.mmdev.meetups.R;
import com.mmdev.meetups.models.ChatModel;
import com.mmdev.meetups.models.FileModel;
import com.mmdev.meetups.models.ProfileModel;
import com.mmdev.meetups.models.UserChatModel;
import com.mmdev.meetups.ui.activities.MainActivity;
import com.mmdev.meetups.ui.adapters.ChatAdapter;
import com.mmdev.meetups.ui.adapters.ClickChatAttachmentsFirebase;
import com.mmdev.meetups.viewmodels.ProfileViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

/* Created by A on 10.07.2019.*/

/**
 * This is the documentation block about the class
 */

public class ChatFragment extends Fragment implements ClickChatAttachmentsFirebase
{

	private static final int IMAGE_GALLERY_REQUEST = 1;
	private static final int IMAGE_CAMERA_REQUEST = 2;
	private static final int PLACE_PICKER_REQUEST = 3;

	private static final String TAG = MainActivity.class.getSimpleName();
	//static final String CHAT_REFERENCE = "chatmodel";
	private static final String GENERAL_COLLECTION_REFERENCE = "chats";
	private static final String SECONDARY_COLLECTION_REFERENCE = "messages";
	private static final String URL_STORAGE_REFERENCE = "gs://meetups-c34b0.appspot.com";
	private static final String FOLDER_STORAGE_IMG = "images";
	private String CHAT_REFERENCE = "";

	// Gallery Permissions
	private static final int REQUEST_STORAGE = 1;
	private static final String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE

	};

	// Camera Permission
	private static final int REQUEST_CAMERA = 2;
	private static final String[] PERMISSIONS_CAMERA = {
			Manifest.permission.CAMERA,
			Manifest.permission.READ_EXTERNAL_STORAGE
	};


	private MainActivity mMainActivity;

	// Firebase
	private FirebaseFirestore mFirestore;
	private FirebaseStorage mStorage;

	// POJO models
	private ProfileModel mProfileModel;
	private UserChatModel mUserChatModel;

	// Views UI
	private RecyclerView rvMessagesList;
	private EditText edMessageWrite;
	private ImageView ivAttachments;
	private ImageView ivSendMessage;
	private LinearLayoutManager mLinearLayoutManager;
	private ChatAdapter mChatAdapter;

	// File
	private File mFilePathImageCamera;


	@Nullable
	@Override
	public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_chat, container, false);
	}

	@Override
	public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
		if (getActivity() != null) mMainActivity = (MainActivity) getActivity();
		edMessageWrite = view.findViewById(R.id.editTextMessage);
		rvMessagesList = view.findViewById(R.id.messageRecyclerView);
		ivAttachments = view.findViewById(R.id.buttonAttachements);
		ivSendMessage = view.findViewById(R.id.buttonMessage);
		setupViews();
		getFirebaseInstances();
		ProfileViewModel mProfileViewModel = ViewModelProviders.of(mMainActivity).get(ProfileViewModel.class);
		mProfileModel = mProfileViewModel.getProfileModel(mMainActivity).getValue();
		if (mProfileModel != null) {
			mUserChatModel = new UserChatModel(mProfileModel.getName(),
											   mProfileModel.getGender(),
											   mProfileModel.getMainPhotoUrl(),
											   mProfileModel.getUserID());
			CHAT_REFERENCE = "user_chat_" + mProfileModel.getUserID();
		}
		readMessagesFirebase();
	}

	@Override
	public void onActivityCreated (@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * setup managers and adapters for views
	 */
	private void setupViews (){
		mLinearLayoutManager = new LinearLayoutManager(mMainActivity);
		mLinearLayoutManager.setStackFromEnd(true);
		ivAttachments.setOnClickListener(v -> photoCameraClick());
		ivSendMessage.setOnClickListener(v -> photoGalleryClick());
	}

	/*
	 * Init firebase instances
	 * auth, storage, firestore
	 */
	private void getFirebaseInstances(){
		mFirestore = FirebaseFirestore.getInstance();
		mStorage = FirebaseStorage.getInstance();
	}

	/*
	 * Checks if the app has permissions to OPEN CAMERA and take photos
	 * If the app does not has permission then the user will be prompted to grant permissions
	 */
	private void photoCameraClick(){
		// Check if we have needed permissions
		int result;
		ArrayList<String> listPermissionsNeeded = new ArrayList<>();
		for (String permission: PERMISSIONS_CAMERA) {
			result = ActivityCompat.checkSelfPermission(mMainActivity, permission);
			if (result != PackageManager.PERMISSION_GRANTED)
				listPermissionsNeeded.add(permission);
		}
		if (!listPermissionsNeeded.isEmpty())
			requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
		else photoCameraIntent();
	}

	/*
	 * Checks if the app has permissions to READ user files
	 * If the app does not has permission then the user will be prompted to grant permissions
	 */
	private void photoGalleryClick(){
		if (ActivityCompat.checkSelfPermission(mMainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED)
			requestPermissions(PERMISSIONS_STORAGE, REQUEST_STORAGE);
		else photoGalleryIntent();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		// If request is cancelled, the result arrays are empty.
		if (requestCode == REQUEST_CAMERA)
			// permission was granted
			if (grantResults.length > 0 &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED)
				photoCameraIntent();
		if (requestCode == REQUEST_STORAGE)
			// permission was granted
			if (grantResults.length > 0 &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED)
				photoGalleryIntent();
	}

	/*
	 * Read collections chatmodel Firebase
	 */
	private void readMessagesFirebase (){
		Query query = mFirestore.collection(GENERAL_COLLECTION_REFERENCE)
				.document(CHAT_REFERENCE)
				.collection(SECONDARY_COLLECTION_REFERENCE)
				.orderBy("timestamp")
				.limit(50);

		FirestoreRecyclerOptions<ChatModel> options =
				new FirestoreRecyclerOptions.Builder<ChatModel>()
						.setQuery(query, ChatModel.class)
						.build();

		mChatAdapter = new ChatAdapter(options, mUserChatModel.getName(), this, mMainActivity);
		mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				int friendlyMessageCount = mChatAdapter.getItemCount();
				int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
				if (lastVisiblePosition == -1 ||
						(positionStart >= (friendlyMessageCount - 1) &&
								lastVisiblePosition == (positionStart - 1))) {
					rvMessagesList.scrollToPosition(positionStart);
				}
			}
		});
		rvMessagesList.setLayoutManager(mLinearLayoutManager);
		rvMessagesList.setAdapter(mChatAdapter);

	}

	/**
	 * click attached photo in chat
	 * @param view your view
	 * @param position pos
	 * @param nameUser sender name
	 * @param urlPhotoUser photo profile url sender
	 * @param urlPhotoClick clicked photo in chat url
	 */
	@Override
	public void clickImageChat(View view, int position,String nameUser,String urlPhotoUser,String urlPhotoClick) {
		//        Intent intent = new Intent(this,FullScreenImageActivity.class);
		//        intent.putExtra("urlPhotoClick",urlPhotoClick);
		//        startActivity(intent);
		FragmentManager fm = mMainActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		//ft.setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right);
		ft.replace(R.id.contentRoot, new ChatPhotoViewFragment(), "ChatPhotoViewFragment");
		ft.addToBackStack(null);
		ft.commit();
	}


	/**
	 * click attached geoposition in chat
	 * @param view your view
	 * @param position pos
	 * @param latitude latitude
	 * @param longitude longitude
	 */
	@Override
	public void clickMapChat (View view, int position, String latitude, String longitude) {
		String uri = String.format("geo:%s,%s?z=17&q=%s,%s", latitude,longitude,latitude,longitude);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		startActivity(intent);
	}

	/*
	 * Send plain text msg to chat if edittext is not empty
	 * else shake animation
	 */
	private void sendMessageClick () {
		if (edMessageWrite.getText().length() > 0) {
			ChatModel chatModel = new ChatModel(mUserChatModel, edMessageWrite.getText().toString());
			mFirestore.collection(GENERAL_COLLECTION_REFERENCE).document(CHAT_REFERENCE).collection(SECONDARY_COLLECTION_REFERENCE).document().set(chatModel);
			edMessageWrite.setText(null);
		}
		else edMessageWrite.startAnimation(AnimationUtils.loadAnimation(mMainActivity, R.anim.horizontal_shake));
	}


	/*
	 * Sends the file to the firebase from gallery
	 */
	private void sendFileFirebase(StorageReference storageReference, final Uri file){
		if (storageReference != null){
			final String name = DateFormat.format("dd-mm-yyyy_hhmmss", new Date()).toString();
			StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
			UploadTask uploadTask = imageGalleryRef.putFile(file);
			// Continue with the task to get the download URL
			uploadTask.continueWithTask(task -> {
				if (task.isSuccessful()) return imageGalleryRef.getDownloadUrl();
					//handle error
				else {
					Log.d(TAG, "urlTask isn't successful");
					return null;
				}
			}).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					Uri downloadUrl = task.getResult();
					Log.d(TAG, "onSuccess sendFileFirebase");
					FileModel fileModel = new FileModel("img", downloadUrl.toString(), name, "");
					ChatModel messageModel = new ChatModel(mUserChatModel, "", fileModel);
					mFirestore.collection(GENERAL_COLLECTION_REFERENCE)
							.document(CHAT_REFERENCE)
							.collection(SECONDARY_COLLECTION_REFERENCE)
							.document()
							.set(messageModel);
				}
				else Log.d(TAG, "sendfile from gallery complete listener isn't succsessful");
			});

		}
		else Log.d(TAG,"storageReference is null");

	}


	/*
	 * Send the file to firebase from camera
	 */
	private void sendFileFirebase(StorageReference storageReference, final File file){
		if (storageReference != null){
			Uri photoURI = FileProvider.getUriForFile(mMainActivity,
													  BuildConfig.APPLICATION_ID + ".provider",
													  file);
			UploadTask uploadTask = storageReference.putFile(photoURI);
			uploadTask.continueWithTask(task -> {
				if (task.isSuccessful()) return storageReference.getDownloadUrl();
				else {
					Log.d(TAG,"urlTask isn't successful");
					return null;
				}
			}).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					Uri downloadUrl = task.getResult();
					Log.d(TAG,"onSuccess sendFileFirebase");
					FileModel fileModel = new FileModel("img", downloadUrl.toString(), file.getName(), file.length()+"");
					ChatModel chatModel = new ChatModel(mUserChatModel, "", fileModel);
					mFirestore.collection(GENERAL_COLLECTION_REFERENCE)
							.document(CHAT_REFERENCE)
							.collection(SECONDARY_COLLECTION_REFERENCE)
							.document().set(chatModel);
				} else Log.d(TAG,"sendfile from camera completelistener isn't succsessful");
			});
		}
		else Log.d(TAG,"storageReference is null");
	}


    /*
     Get user location
     */
	//    private void locationPlacesIntent(){
	//        try {
	//            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
	//            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
	//        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
	//            e.printStackTrace();
	//        }
	//    }

	/*
	 * ################################## NOT WORKING ##################################################
	 * Upload photo taken by camera
	 */
	private void photoCameraIntent(){
		String namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
		mFilePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
									   namePhoto +"camera.jpg");
		Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri photoURI = FileProvider.getUriForFile(mMainActivity,
												  BuildConfig.APPLICATION_ID + ".provider",
												  mFilePathImageCamera);
		it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
		startActivityForResult(it, IMAGE_CAMERA_REQUEST);
	}

	/*
	 * Upload photo in gallery
	 */
	private void photoGalleryIntent(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Get Photo From "), IMAGE_GALLERY_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		StorageReference storageRef = mStorage
				.getReferenceFromUrl(URL_STORAGE_REFERENCE)
				.child(FOLDER_STORAGE_IMG);

		if (requestCode == IMAGE_GALLERY_REQUEST){
			if (resultCode == RESULT_OK){
				Uri selectedImageUri = data.getData();
				if (selectedImageUri != null){
					sendFileFirebase(storageRef, selectedImageUri);
				}
				else Toast.makeText(mMainActivity, "Photo uri is null", Toast.LENGTH_SHORT).show();
			}
		}
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == RESULT_OK)
			{
				if (mFilePathImageCamera != null && mFilePathImageCamera.exists())
				{
					StorageReference imageCameraRef = storageRef.child(mFilePathImageCamera.getName() + "_camera");
					sendFileFirebase(imageCameraRef, mFilePathImageCamera);
				} else Toast.makeText(mMainActivity, "filePathImageCamera is null or filePathImageCamera isn't exists", Toast.LENGTH_SHORT).show();
			}
			//        }else if (requestCode == PLACE_PICKER_REQUEST){
			//            if (resultCode == RESULT_OK) {
			//                Place place = PlacePicker.getPlace(this, data);
			//                if (place!=null){
			//                    LatLng latLng = place.getLatLng();
			//                    MapModel mapModel = new MapModel(latLng.latitude+"", latLng.longitude+"");
			//                    MessageModel chatModel = new MessageModel(userChatModel, Calendar
			//                            .getInstance().getTime().getTime()+"", mapModel);
			//                    mFirestore.child(CHAT_REFERENCE).push().setValue(chatModel);
			//                }else{
			//                    //PLACE IS NULL
			//                }
			//            }

		}
	}


	@Override
	public void onStart() {
		super.onStart();
		mChatAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		mChatAdapter.stopListening();
	}

}
