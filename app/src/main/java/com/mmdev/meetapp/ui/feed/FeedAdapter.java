package com.mmdev.meetapp.ui.feed;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.mmdev.meetapp.R;
import com.mmdev.meetapp.databinding.FragmentFeedItemBinding;
import com.mmdev.meetapp.models.FeedItem;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedItemHolder> {

	private List<FeedItem> mFeedItems;

	FeedAdapter(List<FeedItem> mFeedItems) {
		this.mFeedItems = mFeedItems;
	}

	@Override
	@NonNull
	public FeedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		FragmentFeedItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_feed_item, parent, false);
		return new FeedItemHolder(binding);
	}

	@Override
	public void onBindViewHolder(@NonNull FeedItemHolder holder, int position) {
		FeedItem feedItem = mFeedItems.get(position);
		if (feedItem != null)
			holder.bind(mFeedItems.get(position));
	}

	@Override
	public int getItemCount() { return mFeedItems != null ? mFeedItems.size() : 0; }

	static class FeedItemHolder extends RecyclerView.ViewHolder {

		FragmentFeedItemBinding binding;

		FeedItemHolder(FragmentFeedItemBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		void bind(FeedItem feedItem) {
			binding.setFeedItem(feedItem);
			binding.executePendingBindings();
		}


	}

}
