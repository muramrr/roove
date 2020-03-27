/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 19:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.remote.entity

import com.mmdev.business.core.BaseUserInfo
import java.util.*

/**
 * reports data class
 */

data class Report (val reportType: ReportType,
                   val reportedUser: BaseUserInfo,
                   val dateReported: Date = Date(),
                   var reportId: String = "") {

	enum class ReportType {
		INELIGIBLE_PHOTOS, DISRESPECTFUL_BEHAVIOR, FAKE
	}
}