package com.mmdev.meetups.ui.chat;
/* Created by A on 06.06.2019.*/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mmdev.meetups.R;
import com.mmdev.meetups.models.ChatModel;
import com.mmdev.meetups.models.UserChatModel;
import com.mmdev.meetups.utils.CircleTransform;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This is the documentation block about the class
 */

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatModel, ChatAdapter.ChatViewHolder> {

	private static final int RIGHT_MSG = 0;
	private static final int LEFT_MSG = 1;
	private static final int RIGHT_MSG_IMG = 2;
	private static final int LEFT_MSG_IMG = 3;

	private String mUserName;
	private ClickChatAttachmentsFirebase mClickChatAttachmentsFirebase;
	private Context context; //for toast messages

	/**
	 * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
	 * FirestoreRecyclerOptions} for configuration options.
	 *
	 * @param options query options in {@link ChatFragment}
	 */
	public ChatAdapter (@NonNull FirestoreRecyclerOptions<ChatModel> options, String userName, ClickChatAttachmentsFirebase clickChatAttachmentsFirebase, Context context) {
		super(options);
		mUserName = userName;
		mClickChatAttachmentsFirebase = clickChatAttachmentsFirebase;
		this.context = context;
	}

	/**
	 * Create a new instance of the ViewHolder
	 * in this case we are using a custom views
	 * for each type of message in database
	 * we displaying different designed layouts
	 */
	@NonNull
	@Override
	public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view;
		switch (viewType) {
			case (RIGHT_MSG):
				view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.fragment_chat_item_message_right,parent,false);
				break;
			case (LEFT_MSG):
				view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.fragment_chat_item_message_left,parent,false);
				break;
			case (RIGHT_MSG_IMG):
				view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.fragment_chat_item_message_right_img,parent,false);
				break;
			case (LEFT_MSG_IMG):
				view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.fragment_chat_item_message_left_img,parent,false);
				break;
			default:
				view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.message_item, parent, false);
				break;
		}
		return new ChatViewHolder(view);

	}

	@Override
	protected void onBindViewHolder (@NonNull ChatViewHolder viewHolder, int position, @NonNull ChatModel chatModel) {
		viewHolder.setIvUser(chatModel.getSenderUserModel().getMainPhotoUrl());
		viewHolder.setTextMessage(chatModel.getMessage());
		if (chatModel.getTimestamp()!=null)
			viewHolder.setTvTimestamp(convertTimestamp(chatModel.getTimestamp()));
		else viewHolder.setTvTimestamp("");


		//Toast.makeText(context, chatModel.getSenderUserModel().toString(), Toast.LENGTH_LONG).show();

		viewHolder.tvIsLocation(View.GONE);
		if (chatModel.getFileModel() != null){
			viewHolder.tvIsLocation(View.GONE);
			viewHolder.setIvChatPhoto(chatModel.getFileModel().getUrl());
		}
//        else if(model.getMapModel() != null){
//            viewHolder.setIvChatPhoto(uiUtils.local(model.getMapModel().getLatitude(), model
//                    .getMapModel().getLongitude()));
//            viewHolder.tvIsLocation(View.VISIBLE);
//        }

	}

	@Override
	public int getItemViewType(int position) {
		ChatModel chatModel = getItem(position);

		if (chatModel.getMapModel() != null){
			if (chatModel.getSenderUserModel()
					.getName()
					.equals(mUserName)) return RIGHT_MSG_IMG;
			else return LEFT_MSG_IMG;

		}else if (chatModel.getFileModel() != null){
			if (chatModel.getFileModel().getType().equals("img")
					&& chatModel.getSenderUserModel()
					.getName()
					.equals(mUserName)) return RIGHT_MSG_IMG;
			else return LEFT_MSG_IMG;

		}else if (chatModel.getSenderUserModel()
				.getName()
				.equals(mUserName)) return RIGHT_MSG;
		else return LEFT_MSG;
	}

	//test
	public void changeSenderName(UserChatModel userChatModel){
		mUserName = userChatModel.getName();
	}

	class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


		TextView tvTextMessage, tvTimestamp, tvLocation;
		ImageView ivUser, ivChatPhoto;

		ChatViewHolder (View view) {
			super(view);
			tvTextMessage = itemView.findViewById(R.id.item_message_tvMessage);
			tvTimestamp = itemView.findViewById(R.id.item_message_tvTimestamp);
			ivUser = itemView.findViewById(R.id.item_message_ivUserPic);
			ivChatPhoto = itemView.findViewById(R.id.img_chat);
			tvLocation = itemView.findViewById(R.id.tvLocation);
		}


		/* handle image or map attachment click */
		@Override
		public void onClick(View view) {
			int position = getAdapterPosition();
			ChatModel chatModel = getItem(position);
			if (chatModel.getMapModel() != null) mClickChatAttachmentsFirebase.clickMapChat(view,position,
					chatModel.getMapModel().getLatitude(),
					chatModel.getMapModel().getLongitude());
			else mClickChatAttachmentsFirebase.clickImageChat(view, position,
					chatModel.getSenderUserModel().getName(),
					chatModel.getSenderUserModel().getMainPhotoUrl(),
					chatModel.getFileModel().getUrl());
		}


		/* sets user profile pic in ImgView binded layout */
		void setIvUser (String urlPhotoUser){
			if (ivUser == null) return;
			Glide.with(ivUser.getContext()).load(urlPhotoUser)
					.centerCrop()
					.transform(new CircleTransform()).override(35, 35)
					.into(ivUser);
		}


		/* sets text message in TxtView binded layout */
		void setTextMessage (String message){
			if (tvTextMessage == null) return;
			tvTextMessage.setText(message);
		}


		/* set timestamp in TxtView located below message with time when this message was sent */
		void setTvTimestamp (String timestamp){
			if (tvTimestamp == null) return;
			tvTimestamp.setText(timestamp);
		}


		/* set photo that user sends in chat */
		void setIvChatPhoto (String url){
			if (ivChatPhoto == null) return;
			Glide.with(ivChatPhoto.getContext())
					.load(url)
					.override(100, 100)
					.fitCenter()
					.into(ivChatPhoto);
			ivChatPhoto.setOnClickListener(this);
		}

		void tvIsLocation (int visible){
			if (tvLocation == null) return;
			tvLocation.setVisibility(visible);
		}

	}

	/**
	 * parsing timestamp to display in traditional format
	 * @param date timestamp made by firestore
	 * @return string in format hh:mm AM/PM
	 */
	private String convertTimestamp (Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.ENGLISH);
		return sdf.format(calendar.getTime());
	}

}
