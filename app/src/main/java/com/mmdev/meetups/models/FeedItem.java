package com.mmdev.meetups.models;

public class FeedItem
{
	private String feedPublisherName;
	private int feedPublisherPhotoId;
	private String feedType;
	private String feedSharedTime;
	private int feedContentImageView;
	private String feedContentDescription;
	private int leftAvailSlots;
	private int feedLikesCount;
	private int feedCommentsCount;
	private int feedSharesCount;
	private boolean liked;

	public FeedItem(){}

	public FeedItem(String feedPublisherName,
					int feedPublisherPhotoId,
					String feedType, String feedSharedTime,
					int feedContentImageView, String feedContentDescription,
					int leftAvailSlots,
					int feedLikesCount,
					int feedCommentsCount, int feedSharesCount, boolean liked) {

		this.feedPublisherName = feedPublisherName;
		this.feedPublisherPhotoId = feedPublisherPhotoId;
		this.feedType = feedType;
		this.feedSharedTime = feedSharedTime;
		this.feedContentImageView = feedContentImageView;
		this.feedContentDescription = feedContentDescription;
		this.leftAvailSlots = leftAvailSlots;
		this.feedLikesCount = feedLikesCount;
		this.feedCommentsCount = feedCommentsCount;
		this.feedSharesCount = feedSharesCount;
		this.liked = liked;
	}

	public boolean isLiked() {
		return liked;
	}

	public String getFeedPublisherName() {
		return feedPublisherName;
	}

	public int getFeedPublisherPhotoId() {
		return feedPublisherPhotoId;
	}

	public String getFeedType() {
		return feedType;
	}

	public String getFeedSharedTime() {
		return feedSharedTime;
	}

	public int getFeedContentImageView() {
		return feedContentImageView;
	}

	public String getFeedContentDescription() {
		return feedContentDescription;
	}

	public int getLeftAvailSlots() {
		return leftAvailSlots;
	}

	public int getFeedLikesCount() {
		return feedLikesCount;
	}

	public int getFeedCommentsCount() {
		return feedCommentsCount;
	}

	public int getFeedSharesCount() {
		return feedSharesCount;
	}

}
