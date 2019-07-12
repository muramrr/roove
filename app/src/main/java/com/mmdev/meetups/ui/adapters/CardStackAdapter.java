package com.mmdev.meetups.ui.adapters;

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

	private List<ProfileModel> users;

	public CardStackAdapter (List<ProfileModel> users) { this.users = users; }

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.fragment_card_item_spot, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder (@NonNull ViewHolder holder, int position)
	{
		ProfileModel user = users.get(position);
		holder.name.setText(user.getName());
		holder.city.setText(user.getCity());
		GlideApp.with(holder.image)
				.load(user.getMainPhotoUrl())
				.into(holder.image);
		holder.itemView.setOnClickListener(v -> Toast.makeText(v.getContext(), user.getName(), Toast.LENGTH_SHORT).show());
	}

	@Override
	public int getItemCount () { return users.size(); }

	public void setSpots(List<ProfileModel> users) {
		this.users = users;
	}

	public List<ProfileModel> getSpots()
	{
		return users;
	}

	static class ViewHolder extends RecyclerView.ViewHolder
	{
		private final TextView name;
		private final TextView city;
		private final ImageView image;

		private ViewHolder (@NonNull final View itemView)
		{
			super(itemView);
			name = itemView.findViewById(R.id.item_name);
			city = itemView.findViewById(R.id.item_city);
			image = itemView.findViewById(R.id.item_image);
		}

	}

}
