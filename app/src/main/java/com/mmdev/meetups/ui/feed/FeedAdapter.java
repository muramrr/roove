package com.mmdev.meetups.ui.feed;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mmdev.meetups.R;
import com.mmdev.meetups.databinding.ActivityMainFeedItemBinding;
import com.mmdev.meetups.models.FeedItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedItemHolder> {

	private List<FeedItem> feedItems;

	public FeedAdapter(List<FeedItem> feedItems) {
		this.feedItems = feedItems;
	}

	@Override
	@NonNull
	public FeedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		ActivityMainFeedItemBinding binding = DataBindingUtil.inflate(layoutInflater,
				R.layout.fragment_feed_item, parent, false);
		return new FeedItemHolder(binding);
	}

	@Override
	public void onBindViewHolder(@NonNull FeedItemHolder holder, int position) {
		FeedItem feedItem = feedItems.get(position);
		if (feedItem != null)
			holder.bind(feedItems.get(position));
	}

	@Override
	public int getItemCount() { return feedItems != null ? feedItems.size() : 0; }

	static class FeedItemHolder extends RecyclerView.ViewHolder {

		ActivityMainFeedItemBinding binding;

		FeedItemHolder(ActivityMainFeedItemBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		void bind(FeedItem feedItem) {
			binding.setFeedItem(feedItem);
			binding.executePendingBindings();
		}


	}

}
