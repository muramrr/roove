package com.mmdev.meetups.ui.card;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mmdev.meetups.R;
import com.mmdev.meetups.models.ProfileModel;
import com.mmdev.meetups.utils.GlideApp;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder>
{

	private List<ProfileModel> mUsersList;

	CardStackAdapter (List<ProfileModel> mUsersList) { this.mUsersList = mUsersList; }

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.fragment_card_item, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder (@NonNull ViewHolder holder, final int position) {
		ProfileModel user = mUsersList.get(position);
		holder.tvNameCard.setText(user.getName());
		holder.tvCityCard.setText(user.getCity());
		GlideApp.with(holder.tvImageCard)
				.load(user.getMainPhotoUrl())
				.into(holder.tvImageCard);
		holder.itemView
				.setOnClickListener
						(v -> Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show());

	}

	@Override
	public int getItemCount () { return mUsersList.size(); }

	@Override
	public long getItemId (int position) { return position; }

	ProfileModel getSwipeProfile (int position){ return mUsersList.get(position); }

	static class ViewHolder extends RecyclerView.ViewHolder
	{
		private final TextView tvNameCard;
		private final TextView tvCityCard;
		private final ImageView tvImageCard;

		private ViewHolder (@NonNull final View itemView) {
			super(itemView);
			tvNameCard = itemView.findViewById(R.id.fragment_card_item_text_name);
			tvCityCard = itemView.findViewById(R.id.fragment_card_item_text_city);
			tvImageCard = itemView.findViewById(R.id.fragment_card_item_img_photo);
		}

	}

}
