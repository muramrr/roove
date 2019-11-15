package com.mmdev.roove.ui.feed.misc

import com.mmdev.roove.utils.models.ProfileModel
import java.util.*

object FeedManager {

	fun generateUsers(): List<ProfileModel> {
		val gender1 = "male"
		val gender2 = "female"
		val users: MutableList<ProfileModel> = ArrayList()
		val photoURLs = ArrayList<String>()
		photoURLs.add("https://pp.userapi.com/c638424/v638424593/15ad9/SiQb3lYQQrQ.jpg")
		users.add(ProfileModel("Daria Roman",
		                       "Kyiv",
		                       gender1,
		                       gender2,
		                       "https://pp.userapi.com/c638424/v638424593/15ad9/SiQb3lYQQrQ.jpg",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/NYyCqdBOKwc/600x800")
		users.add(ProfileModel("Fushimi Inari Shrine",
		                       "Kyoto",
		                       gender1,
		                       gender2,
		                       "https://source.unsplash.com/NYyCqdBOKwc/600x800",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/buF62ewDLcQ/600x800")
		users.add(ProfileModel("Bamboo Forest",
		                       "Kyoto",
		                       gender1,
		                       gender2,
		                       "https://source.unsplash.com/buF62ewDLcQ/600x800",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/THozNzxEP3g/600x800")
		users.add(ProfileModel("Brooklyn Bridge",
		                       "New York",
		                       gender1,
		                       gender2,
		                       "https://source.unsplash.com/THozNzxEP3g/600x800",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/USrZRcRS2Lw/600x800")
		users.add(ProfileModel("Empire State Building",
		                       "New York",
		                       gender2,
		                       gender1,
		                       "https://source.unsplash.com/USrZRcRS2Lw/600x800",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/PeFk7fzxTdk/600x800")
		users.add(ProfileModel("The statue of Liberty",
		                       "New York",
		                       gender2,
		                       gender1,
		                       "https://source.unsplash.com/PeFk7fzxTdk/600x800",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/LrMWHKqilUw/600x800")
		users.add(ProfileModel("Louvre Museum",
		                       "Paris",
		                       gender2,
		                       gender1,
		                       "https://source.unsplash.com/LrMWHKqilUw/600x800",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/HN-5Z6AmxrM/600x800")
		users.add(ProfileModel("Eiffel Tower",
		                       "Paris",
		                       gender2,
		                       gender1,
		                       "https://source.unsplash.com/HN-5Z6AmxrM/600x800",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/CdVAUADdqEc/600x800")
		users.add(ProfileModel("Big Ben",
		                       "London",
		                       gender2,
		                       gender1,
		                       "https://source.unsplash.com/CdVAUADdqEc/600x800",
		                       photoURLs,
		                       generateRandomID()))
		photoURLs.clear()
		photoURLs.add("https://source.unsplash.com/AWh9C-QjhE4/600x800")
		users.add(ProfileModel("Great Wall of China",
		                       "China",
		                       gender2,
		                       gender2,
		                       "https://source.unsplash.com/AWh9C-QjhE4/600x800",
		                       photoURLs,
		                       generateRandomID()))
		return users
	}

	/*
    generate random uid
     */
	private fun generateRandomID(): String {
		val leftLimit = 97 // letter 'a'

		val rightLimit = 122 // letter 'z'

		val targetStringLength = 10
		val random = Random()
		val buffer = StringBuilder(targetStringLength)
		for (i in 0 until targetStringLength) {
			val randomLimitedInt =
				leftLimit + (random.nextFloat() * (rightLimit - leftLimit + 1)).toInt()
			buffer.append(randomLimitedInt.toChar())
		}
		return buffer.toString()
	}
}