package com.mmdev.meetups.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.mmdev.meetups.ui.adapters.ChatAdapter;
import com.mmdev.meetups.ui.adapters.ClickChatAttachmentsFirebase;
import com.mmdev.meetups.ui.fragments.ChatPhotoViewFragment;
import com.mmdev.meetups.viewmodels.ProfileViewModel;

import java.io.File;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, ClickChatAttachmentsFirebase
{
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;

    private static final String TAG = MainActivity.class.getSimpleName();
    //static final String CHAT_REFERENCE = "chatmodel";
    private static final  String GENERAL_COLLECTION_REFERENCE = "chats";
    private static final String SECONDARY_COLLECTION_REFERENCE = "messages";
    private static final String URL_STORAGE_REFERENCE = "gs://meetups-c34b0.appspot.com";
    private static final String FOLDER_STORAGE_IMG = "images";
    private String CHAT_REFERENCE = "";


    //Firebase and GoogleApiClient
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;


    //Class Model
    private ProfileModel mProfile;
    private UserChatModel userChatModel;

    //Views UI
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText edMessage;
    private ChatAdapter chatAdapter;

    //File
    private File filePathImageCamera;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        checkConnection();
        bindViews();
        getFirebaseInstances();
        verifyStoragePermissions();
        ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mProfile = profileViewModel.getProfileModel(this).getValue();
        if (mProfile!=null) {
            userChatModel = new UserChatModel(mProfile.getName(),mProfile.getGender(), mProfile.getMainPhotoUrl(), mProfile.getUserID());
            CHAT_REFERENCE = "user_chat_"+ mProfile.getUserID();

        }
        readMessagesFirebase();
    }

    /*
     * check auth
     */
    private void checkConnection(){
        authStateListener = ((@NonNull FirebaseAuth firebaseAuth) -> {
            FirebaseUser signedInUser = firebaseAuth.getCurrentUser();
            if (signedInUser == null) {
                Intent authIntent = new Intent(ChatActivity.this, AuthActivity.class);
                startActivity(authIntent);
                finish();
            }
        });
    }

    /*
     * Link views with Java API
     */
    private void bindViews(){
        edMessage = findViewById(R.id.editTextMessage);
        rvListMessage = findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
    }

    /*
     * Init firebase instances
     * auth, storage, firestore
     */
    private void getFirebaseInstances(){
        firebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    /*
     * Checks if the app has permission to write to device mStorage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this,
                                              PERMISSIONS_STORAGE,
                                              REQUEST_EXTERNAL_STORAGE);
        }
        // we already have permission, lets go ahead and call camera intent
        else Toast.makeText(this,"Permissions granted", Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

        chatAdapter = new ChatAdapter(options, userChatModel.getName(), this, this);
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = chatAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                }
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(chatAdapter);

    }

    /*
     * change users to test
     */
    public void MessagesClick (MenuItem item) {
        if (mProfile.getName().equals(userChatModel.getName())){
            userChatModel.setName("Daria Roman");
            userChatModel.setGender("female");
            userChatModel.setID("drugoe_id");
            userChatModel.setMainPhotoUrl("https://pp.userapi.com/c630222/v630222593/498d9/eesJsAFEF8c.jpg");
        }
        else {
            userChatModel.setName(mProfile.getName());
            userChatModel.setGender(mProfile.getGender());
            userChatModel.setID(mProfile.getUserID());
            userChatModel.setMainPhotoUrl(mProfile.getMainPhotoUrl());
        }
        chatAdapter.changeSenderName(userChatModel);
        chatAdapter.notifyDataSetChanged();
    }


    public void attachClick (View view) { photoGalleryIntent(); }


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
        FragmentManager fm = getSupportFragmentManager();
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


    /**
     * Send plain text msg to chat
     */
    public void sendMessage (View view) {
        ChatModel chatModel = new ChatModel(userChatModel, edMessage.getText().toString());
        mFirestore.collection(GENERAL_COLLECTION_REFERENCE)
                .document(CHAT_REFERENCE)
                .collection(SECONDARY_COLLECTION_REFERENCE)
                .document().set(chatModel);
        edMessage.setText(null);

    }


    /**
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
                    ChatModel messageModel = new ChatModel(userChatModel, "", fileModel);
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


    /**
     * Send the file to firebase from camera
     */
    private void sendFileFirebase(StorageReference storageReference, final File file){
        if (storageReference != null){
            Uri photoURI = FileProvider.getUriForFile(this,
                                                      BuildConfig.APPLICATION_ID + ".provider",
                                                      file);
            UploadTask uploadTask = storageReference.putFile(photoURI);
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
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
                    ChatModel chatModel = new ChatModel(userChatModel, "", fileModel);
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

    /**
     * ##################################NOT WORKING##################################################
     * Upload photo taken by camera
     */
    private void photoCameraIntent(){
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                       nomeFoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(this,
                                                  BuildConfig.APPLICATION_ID + ".provider",
                                                  filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    /**
     * Upload photo in gallery
     */
    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Get Photo From "), IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                else Toast.makeText(this, "Photo uri is null", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == IMAGE_CAMERA_REQUEST){
            if (resultCode == RESULT_OK){
                if (filePathImageCamera != null && filePathImageCamera.exists()){
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName()+"_camera");
                    sendFileFirebase(imageCameraRef, filePathImageCamera);
                }
                else Toast.makeText(this, "filePathImageCamera is null or filePathImageCamera isn't exists", Toast.LENGTH_SHORT).show();
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

    /*
     * google play connection
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == REQUEST_EXTERNAL_STORAGE)
            // permission was granted
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                photoCameraIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        chatAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
        chatAdapter.stopListening();
    }
}
