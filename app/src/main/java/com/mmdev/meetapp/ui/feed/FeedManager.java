package com.mmdev.meetapp.ui.feed;

import com.mmdev.meetapp.R;
import com.mmdev.meetapp.models.FeedItem;
import com.mmdev.meetapp.models.ProfileModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FeedManager {

	static List<FeedItem> generateDummyFeeds () {
		List<FeedItem> feedItems = new ArrayList<>();
		feedItems.add(new FeedItem("Wan Clem",
				R.drawable.feed_content_driving_a_car,
				"Posted",
				"2hr",
				"https://images.unsplash.com/photo-1569183839911-5a9e0ef9c74e?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max",
				"A very nice mercedez Benz",
				15));

		feedItems.add(new FeedItem("Sean Parker",
				R.drawable.feed_content_man_in_suit,
				"Shared",
				"3hr",
				"",
				"Men with class",
				19));

		feedItems.add(new FeedItem("Ivanka TimberLake",
				R.drawable.feed_content_girl_jogging,
				"Shared",
				"4hr",
				"https://images.unsplash.com/photo-1568841228566-455c6533e892?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max",
				"Awesome feed_content_joggers",
				74));

		feedItems.add(new FeedItem("Angelina Blanca",
				R.drawable.feed_content_descent,
				"Posted",
				"5hr",
				"",
				"Nice pair of feed_content_shoes",
				18));

		feedItems.add(new FeedItem("Bradly Gates",
				R.drawable.feed_content_riding_bycle,
				"Posted",
				"6hr",
				"https://images.unsplash.com/photo-1568481694572-585cff34ee84?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max",
				"A very nice power bike",
				15));

		feedItems.add(new FeedItem("Sean Parker",
				R.drawable.feed_content_man_in_suit,
				"Shared",
				"3hr",
				"",
				"I was born in an empty sea, My tears created oceans Producing tsunami waves with emotions Patrolling the open seas of an unknown galaxy I was floating in front of who I am physically Spiritually paralyzing mind body and soul It gives me energy when I'm lyrically exercising I gotta spit 'til the story is told in a dream by celestial bodies Follow me baby",
				19));

		feedItems.add(new FeedItem("Sean Parker",
				R.drawable.feed_content_man_in_suit,
				"Shared",
				"3hr",
				"",
				"Men with class",
				19));

		feedItems.add(new FeedItem("Sean Parker",
				R.drawable.feed_content_man_in_suit,
				"Shared",
				"3hr",
				"",
				"Men with class",
				19));

		feedItems.add(new FeedItem("Sean Parker",
				R.drawable.feed_content_man_in_suit,
				"Shared",
				"3hr",
				"",
				"Men with class",
				19));

		feedItems.add(new FeedItem("Sean Parker",
				R.drawable.feed_content_man_in_suit,
				"Shared",
				"3hr",
				"",
				"Men with class",
				19));

		feedItems.add(new FeedItem("Sean Parker",
				R.drawable.feed_content_man_in_suit,
				"Shared",
				"3hr",
				"",
				"Men with class",
				19));

		return feedItems;
	}

	public static List<ProfileModel> generateUsers()
	{
		String gender1 = "male";
		String gender2 = "female";
		List<ProfileModel> users = new ArrayList<>();
		ArrayList<String> photoURLs = new ArrayList<>();
		photoURLs.add("https://pp.userapi.com/c638424/v638424593/15ad9/SiQb3lYQQrQ.jpg");
		users.add(new ProfileModel("Daria Roman", "Kyiv",
				gender1, gender2,
				"https://pp.userapi.com/c638424/v638424593/15ad9/SiQb3lYQQrQ.jpg",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/NYyCqdBOKwc/600x800");
		users.add(new ProfileModel("Fushimi Inari Shrine", "Kyoto",
				gender1, gender2,
				"https://source.unsplash.com/NYyCqdBOKwc/600x800",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/buF62ewDLcQ/600x800");
		users.add(new ProfileModel("Bamboo Forest", "Kyoto",
				gender1, gender2,
				"https://source.unsplash.com/buF62ewDLcQ/600x800",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/THozNzxEP3g/600x800");
		users.add(new ProfileModel("Brooklyn Bridge", "New York",
				gender1, gender2,
				"https://source.unsplash.com/THozNzxEP3g/600x800",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/USrZRcRS2Lw/600x800");
		users.add(new ProfileModel("Empire State Building", "New York",
				gender2, gender1,
				"https://source.unsplash.com/USrZRcRS2Lw/600x800",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/PeFk7fzxTdk/600x800");
		users.add(new ProfileModel("The statue of Liberty", "New York",
				gender2, gender1,
				"https://source.unsplash.com/PeFk7fzxTdk/600x800",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/LrMWHKqilUw/600x800");
		users.add(new ProfileModel("Louvre Museum", "Paris",
				gender2, gender1,
				"https://source.unsplash.com/LrMWHKqilUw/600x800",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/HN-5Z6AmxrM/600x800");
		users.add(new ProfileModel("Eiffel Tower", "Paris",
				gender2, gender1,
				"https://source.unsplash.com/HN-5Z6AmxrM/600x800",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/CdVAUADdqEc/600x800");
		users.add(new ProfileModel("Big Ben", "London",
				gender2, gender1,
				"https://source.unsplash.com/CdVAUADdqEc/600x800",
				photoURLs, generateRandomID()));
		photoURLs.clear();
		photoURLs.add("https://source.unsplash.com/AWh9C-QjhE4/600x800");
		users.add(new ProfileModel("Great Wall of China", "China",
				gender2, gender2,
				"https://source.unsplash.com/AWh9C-QjhE4/600x800",
				photoURLs, generateRandomID()));
		return users;
	}

    /*
    generate random uid
     */
	private static String generateRandomID() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = leftLimit + (int)
					(random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		return buffer.toString();
	}


}
