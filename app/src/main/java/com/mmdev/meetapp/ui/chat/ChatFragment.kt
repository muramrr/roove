package com.mmdev.meetapp.ui.chat

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mmdev.meetapp.BuildConfig
import com.mmdev.meetapp.R
import com.mmdev.meetapp.models.ChatModel
import com.mmdev.meetapp.models.FileModel
import com.mmdev.meetapp.models.ProfileModel
import com.mmdev.meetapp.models.UserChatModel
import com.mmdev.meetapp.ui.main.MainActivity
import com.mmdev.meetapp.ui.main.ProfileViewModel
import java.io.File
import java.util.*

/* Created by A on 10.07.2019.*/

/**
 * This is the documentation block about the class
 */

class ChatFragment : Fragment(), ClickChatAttachmentsFirebase {
	private var CHAT_REFERENCE = ""

	private var mMainActivity: MainActivity? = null

	// Firebase
	private var mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
	private var mStorage: FirebaseStorage = FirebaseStorage.getInstance()

	// POJO models
	private var mProfileModel: ProfileModel? = null
	private var mUserChatModel: UserChatModel? = null

	// Views UI
	private var rvMessagesList: RecyclerView? = null
	private var edMessageWrite: EditText? = null
	private var ivAttachments: ImageView? = null
	private var ivSendMessage: ImageView? = null
	private var mLinearLayoutManager: LinearLayoutManager? = null
	private var mChatAdapter: ChatAdapter? = null

	// File
	private var mFilePathImageCamera: File? = null


	//static fields
	companion object {

		private const val IMAGE_GALLERY_REQUEST = 1
		private const val IMAGE_CAMERA_REQUEST = 2
		private const val PLACE_PICKER_REQUEST = 3

		private val TAG = MainActivity::class.java.simpleName
		//static final String CHAT_REFERENCE = "chatmodel";
		private const val GENERAL_COLLECTION_REFERENCE = "chats"
		private const val SECONDARY_COLLECTION_REFERENCE = "messages"
		private const val URL_STORAGE_REFERENCE = "gs://meetups-c34b0.appspot.com"
		private const val FOLDER_STORAGE_IMG = "images"

		// Gallery Permissions
		private const val REQUEST_STORAGE = 1
		private val PERMISSIONS_STORAGE =
			arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

		// Camera Permission
		private const val REQUEST_CAMERA = 2
		private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_chat, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		activity?.let { mMainActivity = it as MainActivity }
		edMessageWrite = view.findViewById(R.id.editTextMessage)
		rvMessagesList = view.findViewById(R.id.messageRecyclerView)
		ivAttachments = view.findViewById(R.id.buttonAttachments)
		ivSendMessage = view.findViewById(R.id.buttonMessage)
		setupViews()
		val mProfileViewModel = ViewModelProviders.of(mMainActivity!!).get(ProfileViewModel::class.java)
		mProfileModel = mProfileViewModel.getProfileModel(mMainActivity).value
		if (mProfileModel != null) {
			mUserChatModel = UserChatModel(
				mProfileModel!!.name,
				mProfileModel!!.gender,
				mProfileModel!!.mainPhotoUrl,
				mProfileModel!!.userId
			)
			CHAT_REFERENCE = "user_chat_" + mProfileModel!!.userId
		}
		readMessagesFirebase()
	}

	/*
	 * setup managers and adapters for views
	 */
	private fun setupViews() {
		mLinearLayoutManager = LinearLayoutManager(mMainActivity)
		mLinearLayoutManager!!.stackFromEnd = true
		ivAttachments!!.setOnClickListener { photoCameraClick() }
		ivSendMessage!!.setOnClickListener { sendMessageClick() }
	}


	/*
	 * Checks if the app has permissions to OPEN CAMERA and take photos
	 * If the app does not has permission then the user will be prompted to grant permissions
	 */
	private fun photoCameraClick() {
		// Check if we have needed permissions
		var result: Int
		val listPermissionsNeeded = ArrayList<String>()
		for (permission in PERMISSIONS_CAMERA) {
			result = ActivityCompat.checkSelfPermission(mMainActivity!!, permission)
			if (result != PackageManager.PERMISSION_GRANTED) listPermissionsNeeded.add(permission)
		}
		if (listPermissionsNeeded.isNotEmpty()) requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA)
		else photoCameraIntent()
	}

	/*
	 * Checks if the app has permissions to READ user files
	 * If the app does not has permission then the user will be prompted to grant permissions
	 */
	private fun photoGalleryClick() {
		if (ActivityCompat.checkSelfPermission(mMainActivity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			requestPermissions(PERMISSIONS_STORAGE, REQUEST_STORAGE)
		else photoGalleryIntent()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		// If request is cancelled, the result arrays are empty.
		if (requestCode == REQUEST_CAMERA)
		// permission was granted
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) photoCameraIntent()
		if (requestCode == REQUEST_STORAGE)
		// permission was granted
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				photoGalleryIntent()
	}

	/*
	 * Read collections chatmodel Firebase
	 */
	private fun readMessagesFirebase() {
		val query = mFirestore.collection(GENERAL_COLLECTION_REFERENCE)
			.document(CHAT_REFERENCE)
			.collection(SECONDARY_COLLECTION_REFERENCE)
			.orderBy("timestamp")

		val options = FirestoreRecyclerOptions.Builder<ChatModel>()
			.setQuery(query, ChatModel::class.java)
			.build()

		mChatAdapter = ChatAdapter(options, mUserChatModel?.name!!, this)
		mChatAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
			override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
				super.onItemRangeInserted(positionStart, itemCount)
				val friendlyMessageCount = mChatAdapter!!.itemCount
				val lastVisiblePosition = mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
				if (lastVisiblePosition == -1 || positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1) {
					rvMessagesList!!.scrollToPosition(positionStart)
				}
			}
		})
		rvMessagesList!!.layoutManager = mLinearLayoutManager
		rvMessagesList!!.adapter = mChatAdapter

	}

	/**
	 * click attached photo in chat
	 * @param view your view
	 * @param position pos
	 * @param nameUser sender name
	 * @param urlPhotoUser photo profile url sender
	 * @param urlPhotoClick clicked photo in chat url
	 */
	override fun clickImageChat(view: View, position: Int, nameUser: String, urlPhotoUser: String, urlPhotoClick: String) {
		val intent = Intent(mMainActivity, FullScreenImageActivity::class.java)
		intent.putExtra("urlPhotoClick", urlPhotoClick)
		startActivity(intent)
	}


	/**
	 * click attached geoposition in chat
	 * @param view your view
	 * @param position pos
	 * @param latitude latitude
	 * @param longitude longitude
	 */
	override fun clickMapChat(view: View, position: Int, latitude: String, longitude: String) {
		val uri = String.format("geo:%s,%s?z=17&q=%s,%s", latitude, longitude, latitude, longitude)
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
		startActivity(intent)
	}

	/*
	 * Send plain text msg to chat if edittext is not empty
	 * else shake animation
	 */
	private fun sendMessageClick() {
		if (edMessageWrite!!.text.isNotEmpty()) {
			val chatModel = ChatModel(mUserChatModel, edMessageWrite!!.text.toString())
			mFirestore
				.collection(GENERAL_COLLECTION_REFERENCE).document(CHAT_REFERENCE)
				.collection(SECONDARY_COLLECTION_REFERENCE).document()
				.set(chatModel)
			edMessageWrite!!.text = null
		} else
			edMessageWrite!!.startAnimation(AnimationUtils.loadAnimation(mMainActivity, R.anim.edittext_horizontal_shake))
	}


	/*
	 * Sends the fileModel to the firebase from gallery
	 */
	private fun sendFileFirebase(storageReference: StorageReference?, file: Uri) {
		if (storageReference != null) {
			val name = DateFormat.format("dd-mm-yyyy_hhmmss", Date()).toString()
			val imageGalleryRef = storageReference.child(name + "_gallery")
			val uploadTask = imageGalleryRef.putFile(file)
			// Continue with the task to get the download URL
			uploadTask.continueWithTask { task ->
				return@continueWithTask (if (task.isSuccessful) imageGalleryRef.downloadUrl
				else {
					Log.d(TAG, "urlTask isn't successful")
					null
				}) //handle error
			}.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					val downloadUrl = task.result
					Log.d(TAG, "onSuccess sendFileFirebase")
					val fileModel = FileModel("img", downloadUrl!!.toString(), name, "")
					val messageModel = ChatModel(mUserChatModel, fileModel = fileModel)
					mFirestore.collection(GENERAL_COLLECTION_REFERENCE)
						.document(CHAT_REFERENCE)
						.collection(SECONDARY_COLLECTION_REFERENCE)
						.document()
						.set(messageModel)
				} else Log.d(TAG, "sendfile from gallery complete listener isn't succsessful")
			}

		} else Log.d(TAG, "storageReference is null")

	}


	/*
	 * Send the fileModel to firebase from camera
	 */
	private fun sendFileFirebase(storageReference: StorageReference, file: File) {
		val photoURI = FileProvider.getUriForFile(mMainActivity!!, BuildConfig.APPLICATION_ID + ".provider", file)

		val uploadTask = storageReference.putFile(photoURI)
		uploadTask.continueWithTask<Uri> { task ->
			if (!task.isSuccessful) task.exception?.let { throw it }
			return@continueWithTask storageReference.downloadUrl

		}
			.addOnCompleteListener { task ->
			if (task.isSuccessful) {
				val downloadUrl = task.result
				Log.d(TAG, "onSuccess sendFileFirebase")
				val fileModel = FileModel("img", downloadUrl!!.toString(), file.name, file.length().toString() + "")
				val chatModel = ChatModel(mUserChatModel, fileModel = fileModel)
				mFirestore.collection(GENERAL_COLLECTION_REFERENCE)
					.document(CHAT_REFERENCE)
					.collection(SECONDARY_COLLECTION_REFERENCE)
					.document().set(chatModel)
			} else Log.d(TAG, "sendfile from camera completelistener isn't succsessful")
		}
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
	 * Upload photo taken by camera
	 */
	private fun photoCameraIntent() {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
		mFilePathImageCamera = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
			namePhoto + "camera.jpg")
		val it = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		val photoURI = FileProvider.getUriForFile(mMainActivity!!, BuildConfig.APPLICATION_ID + ".provider",
			mFilePathImageCamera!!)
		it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
		startActivityForResult(it, IMAGE_CAMERA_REQUEST)
	}

	/*
	 * Upload photo in gallery
	 */
	private fun photoGalleryIntent() {
		val intent = Intent()
		intent.type = "image/*"
		intent.action = Intent.ACTION_GET_CONTENT
		startActivityForResult(Intent.createChooser(intent, "Get Photo From "), IMAGE_GALLERY_REQUEST)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		val storageRef = mStorage
			.getReferenceFromUrl(URL_STORAGE_REFERENCE)
			.child(FOLDER_STORAGE_IMG)

		if (requestCode == IMAGE_GALLERY_REQUEST) {
			if (resultCode == RESULT_OK) {
				val selectedImageUri = data!!.data
				if (selectedImageUri != null) { sendFileFirebase(storageRef, selectedImageUri) }
				else Toast.makeText(mMainActivity, "Photo uri is null", Toast.LENGTH_SHORT).show()
			}
		}
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {
				if (mFilePathImageCamera != null && mFilePathImageCamera!!.exists()) {
					val imageCameraRef = storageRef.child(mFilePathImageCamera!!.name + "_camera")
					sendFileFirebase(imageCameraRef, mFilePathImageCamera!!)
				} else
					Toast.makeText(mMainActivity,
						"filePathImageCamera is null or filePathImageCamera isn't exists",
						Toast.LENGTH_SHORT)
						.show()
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

	override fun onStart() {
		super.onStart()
		mChatAdapter?.startListening()
	}

	override fun onStop() {
		super.onStop()
		mChatAdapter?.stopListening()
	}



}
