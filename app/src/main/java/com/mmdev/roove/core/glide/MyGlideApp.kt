/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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
