/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.01.20 14:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Add an Interceptor to your own OkHttpClient.
 * This is where you create your DispatchingProgressManager instance.
 * This is where you provide your own OkHttpProgressResponseBody with the DispatchingProgressManager instance.
 * Glide provides a OkHttpUrlLoader.Factory class for you to conveniently replace Glide's default networking library.
 */


@GlideModule
class MyGlideApp : AppGlideModule(){


	override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
		super.registerComponents(context, glide, registry)
		val client = OkHttpClient.Builder()
			.addNetworkInterceptor { chain ->
				val request = chain.request()
				val response = chain.proceed(request)
				val listener = DispatchingProgressManager()
				response.newBuilder()
					.body(OkHttpProgressResponseBody(request.url(), response.body()!!, listener))
					.build()
			}
			.build()
		glide.registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
	}
}
