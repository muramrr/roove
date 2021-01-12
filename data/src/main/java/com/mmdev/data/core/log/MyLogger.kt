/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

package com.mmdev.data.core.log

import android.util.Log
import com.mmdev.data.core.log.MyLogger.Default

/**
 * May be used to create a custom logging solution to override the [Default] behaviour.
 */

interface MyLogger {
	
	/**
	 * @param tag used to identify the source of a log message.
	 * @param message the message to be logged.
	 */
	fun logWarn(tag: String, message: String)
	
	fun logError(tag: String, message: String)
	
	fun logDebug(tag: String, message: String)
	
	fun logInfo(tag: String, message: String)
	
	fun logWtf(tag: String, message: String)
	
	/**
	 * Debug implementation of [MyLogger].
	 */
	object Debug : MyLogger {
		
		override fun logWarn(tag: String, message: String) {
			Log.w(tag, message)
		}
		
		override fun logError(tag: String, message: String) {
			Log.e(tag, message)
		}
		
		override fun logDebug(tag: String, message: String) {
			Log.d(tag, message)
		}
		
		override fun logInfo(tag: String, message: String) {
			Log.i(tag, message)
		}
		
		override fun logWtf(tag: String, message: String) {
			Log.wtf(tag, message)
		}
	}
	
	/**
	 * Default implementation of [MyLogger].
	 * No messages to Logcat
	 */
	object Default : MyLogger {
		override fun logWarn(tag: String, message: String) {}
		override fun logError(tag: String, message: String) {}
		override fun logDebug(tag: String, message: String) {}
		override fun logInfo(tag: String, message: String) {}
		override fun logWtf(tag: String, message: String) {}
	}
}