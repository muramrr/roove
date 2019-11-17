package com.mmdev.roove.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/* Created by A on 17.11.2019.*/

/**
 * This is the documentation block about the class
 */

fun AppCompatActivity.showToastText(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG)
	.show()