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

import com.mmdev.data.BuildConfig

/**
 * Own solution for logging operations
 * Good enough to not use Timber or any other third-party loggers
 */

interface DebugConfig {
	
	val isEnabled: Boolean
	val logger: MyLogger
	
	object Default : DebugConfig {
		override val isEnabled: Boolean = BuildConfig.DEBUG
		override val logger: MyLogger = if (isEnabled) MyLogger.Debug else MyLogger.Default
	}
	
	object Enabled : DebugConfig {
		override val isEnabled: Boolean = true
		override val logger: MyLogger = MyLogger.Debug
	}
}