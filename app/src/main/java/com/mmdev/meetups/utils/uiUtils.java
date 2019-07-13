package com.mmdev.meetups.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class uiUtils
{
	private static Handler handler = new Handler(Looper.getMainLooper());

	public static void showSafeToast(final Context context, final String toastMessage) {
		runOnMain(() -> Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show());
	}

	private static boolean isMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	private static void runOnMain(final @NonNull Runnable runnable) {
		if (isMainThread()) runnable.run();
		else handler.post(runnable);
	}

	public static String local(String latitudeFinal,String longitudeFinal){
		return "https://maps.googleapis.com/maps/api/staticmap?center="+latitudeFinal+","+longitudeFinal+"&zoom=18&size=280x280&markers=color:red|"+latitudeFinal+","+longitudeFinal;
	}
}
