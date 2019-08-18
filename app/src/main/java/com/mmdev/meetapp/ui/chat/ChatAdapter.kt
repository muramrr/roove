package com.mmdev.meetapp.ui.chat

/* Created by A on 06.06.2019.*/

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mmdev.meetapp.R
import com.mmdev.meetapp.models.ChatModel
import com.mmdev.meetapp.utils.CircleTransform
import com.mmdev.meetapp.utils.GlideApp
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create a new RecyclerView adapter that listens to a Firestore Query.  See [ ] for configuration options.
 *
 * @param options query options in [ChatFragment]
 */

class ChatAdapter constructor(options: FirestoreRecyclerOptions<ChatModel>, private val mUserName: String,
                              private val mClickChatAttachmentsFirebase: ClickChatAttachmentsFirebase) :

	FirestoreRecyclerAdapter<ChatModel, ChatAdapter.ChatViewHolder>(options) {

	companion object {

		private const val RIGHT_MSG = 0
		private const val LEFT_MSG = 1
		private const val RIGHT_MSG_IMG = 2
		private const val LEFT_MSG_IMG = 3
	}

	/**
	 * Create a new instance of the ViewHolder
	 * in this case we are using a custom views
	 * for each type of message in database
	 * we displaying different layouts
	 */
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
		val view: View = if (viewType == RIGHT_MSG || viewType == RIGHT_MSG_IMG) LayoutInflater.from(parent.context)
				.inflate(R.layout.fragment_chat_item_right, parent, false)
			else LayoutInflater.from(parent.context)
				.inflate(R.layout.fragment_chat_item_left, parent, false)

		return ChatViewHolder(view)

	}


	override fun onBindViewHolder(viewHolder: ChatViewHolder, position: Int, chatModel: ChatModel) {
		viewHolder.setMessageType(getItemViewType(position))
		chatModel.senderUserModel?.mainPhotoUrl?. let { viewHolder.setIvUserAvatar(it) }
		chatModel.message?. let { viewHolder.setTextMessage(it) }
		chatModel.fileModel?.fileUrl?. let { viewHolder.setIvChatPhoto(it) }

		if (chatModel.timestamp != null) viewHolder.setTvTimestamp(convertTimestamp(chatModel.timestamp))
		else viewHolder.setTvTimestamp("")

		//Toast.makeText(context, chatModel.getSenderUserModel().toString(), Toast.LENGTH_LONG).show();



		//        else if(model.getMapModel() != null){
		//            viewHolder.setIvChatPhoto(uiUtils.local(model.getMapModel().getLatitude(), model
		//                    .getMapModel().getLongitude()));
		//            viewHolder.tvIsLocation(View.VISIBLE);
		//        }

	}

	override fun getItemViewType(position: Int): Int {
		val (senderUserModel, _, fileModel, _) = getItem(position)
		return if (fileModel != null)
			if (fileModel.fileType == "img" && senderUserModel!!.name == mUserName) RIGHT_MSG_IMG
			else LEFT_MSG_IMG
		else
			if (senderUserModel!!.name == mUserName) RIGHT_MSG
			else LEFT_MSG
	}

	/* TODO: USE FOR -DEBUG ONLY */
	//	public void changeSenderName(UserChatModel userChatModel){
	//		mUserName = userChatModel.getName();
	//	}

	inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

		private var tvTextMessage: TextView = itemView.findViewById(R.id.item_message_tvMessage)
		private var tvTimestamp: TextView = itemView.findViewById(R.id.item_message_tvTimestamp)
		private var ivUserAvatar: ImageView = itemView.findViewById(R.id.item_message_ivUserPic)
		private var ivChatPhoto: ImageView = itemView.findViewById(R.id.img_chat)

		fun setMessageType(messageType: Int) {
			when (messageType) {
				RIGHT_MSG -> ivChatPhoto.visibility = View.GONE
				LEFT_MSG -> ivChatPhoto.visibility = View.GONE
				RIGHT_MSG_IMG -> tvTextMessage.visibility = View.GONE
				LEFT_MSG_IMG -> tvTextMessage.visibility = View.GONE
			}
		}


		/* handle image or map attachment click */
		override fun onClick(view: View) {
			val chatModel: ChatModel = getItem(adapterPosition)

			if (chatModel.mapModel != null) mClickChatAttachmentsFirebase.clickMapChat(view, adapterPosition,
				chatModel.mapModel!!.latitude,
				chatModel.mapModel!!.longitude)
			else mClickChatAttachmentsFirebase.clickImageChat(
					view, adapterPosition,
					chatModel.senderUserModel!!.name,
					chatModel.senderUserModel!!.mainPhotoUrl,
					chatModel.fileModel!!.fileUrl)
		}

		/* sets user profile pic in ImgView binded layout */
		fun setIvUserAvatar(urlPhotoUser: String) {
			Glide.with(ivUserAvatar.context).load(urlPhotoUser)
				.centerCrop()
				.transform(CircleTransform()).override(35, 35)
				.into(ivUserAvatar)
		}

		/* sets text message in TxtView binded layout */
		fun setTextMessage(message: String?) { tvTextMessage.text = message }

		/* set timestamp in TxtView located below message with time when this message was sent */
		fun setTvTimestamp(timestamp: String) { tvTimestamp.text = timestamp }

		/* set photo that user sends in chat */
		fun setIvChatPhoto(url: String) {
			GlideApp.with(ivChatPhoto.context)
				.load(url)
				.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
				.override(250, 250)
				.into(ivChatPhoto)
			ivChatPhoto.setOnClickListener(this)
		}

	}

	/**
	 * parsing timestamp to display in traditional format
	 * @param date timestamp made by firestore
	 * @return string in format hh:mm AM/PM
	 */
	private fun convertTimestamp(date: Date?): String {
		val calendar = Calendar.getInstance()
		calendar.time = date
		return SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.ENGLISH).format(calendar.time)
	}



}
