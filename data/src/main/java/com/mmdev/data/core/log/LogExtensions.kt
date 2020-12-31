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

package com.mmdev.data.core.log


fun logWarn(tag: String = "mylogs", message: String) =
	DebugConfig.Default.logger.logWarn(tag, message)
fun logWarn(clazz: Class<Any>, message: String) =
	logWarn("mylogs_${clazz.simpleName}", message)



fun logError(tag: String = "mylogs", message: String) =
	DebugConfig.Default.logger.logError(tag, message)
fun logError(clazz: Class<Any>, message: String) =
	logError("mylogs_${clazz.simpleName}", message)



fun logDebug(tag: String = "mylogs", message: String) =
	DebugConfig.Default.logger.logDebug(tag, message)
fun logDebug(clazz: Class<Any>, message: String) =
	logDebug("mylogs_${clazz.simpleName}", message)



fun logInfo(tag: String = "mylogs", message: String) =
	DebugConfig.Default.logger.logInfo(tag, message)
fun logInfo(clazz: Class<Any>, message: String) =
	logInfo("mylogs_${clazz.simpleName}", message)



fun logWtf(tag: String = "mylogs", message: String) =
	DebugConfig.Default.logger.logWtf(tag, message)
fun logWtf(clazz: Class<Any>, message: String) =
	logWtf("mylogs_${clazz.simpleName}", message)
