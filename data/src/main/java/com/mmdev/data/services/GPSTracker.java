package com.mmdev.data.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSTracker
		extends Service
		implements LocationListener {
	//private final Context context;
	// flag for GPS status
	boolean isGPSEnabled = false;
	// flag for network status
	boolean isNetworkEnabled = false;
	// flag for GPS status
	boolean canGetLocation = false;
	Location location; // location
	double latitude = 0; // latitude
	double longitude = 0; // longitude
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2; // 2 minutes
	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker () {}

	public Location getLocation (Context context) {
		PackageManager pm = context.getPackageManager();
		locationManager = (LocationManager) context
				.getSystemService(LOCATION_SERVICE);

		if (locationManager != null) {
			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		if (!isGPSEnabled && !isNetworkEnabled) {
			this.canGetLocation = false;
		} else {
			this.canGetLocation = true;
//            if ((isNetworkEnabled && pm.checkPermission(Manifest.permission.INTERNET, context.getPackageName()) == PackageManager.PERMISSION_GRANTED)){
//                locationManager.requestLocationUpdates(
//                        LocationManager.NETWORK_PROVIDER,
//                        MIN_TIME_BW_UPDATES,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                if (locationManager != null) {
//                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    if (location != null) {
//                        latitude = location.getLatitude();
//                        longitude = location.getLongitude();
//                        return location;
//                    }
//                }
//            }
			// if GPS Enabled get lat/long using GPS Services
			if (isGPSEnabled && pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER,
						MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				if (locationManager != null) {
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (location != null) {
						latitude = location.getLatitude();
						longitude = location.getLongitude();
						return location;
					}
				}
			}
		}
		return location;
	}
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}
	/**
	 * Function to get latitude
	 */

	public String getCity(Context context)
	{
		double latitude;
		double longitude;
		String mCity = "";
		List<Address> addresses;
		location = getLocation(context);
		// check if GPS enabled
		if(location!=null){
			latitude = getLatitude();
			longitude = getLongitude();
			//gps.stopUsingGPS();
			// \n is for new line
			Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
				mCity = addresses.get(0).getLocality();
			} catch (IOException e) {
				e.printStackTrace();
				mCity = "Error getting city";
			}
		}
		//else showSettingsAlert(context);
		return mCity;
	}

	public double getLatitude() { return latitude; }
	/**
	 * Function to get longitude
	 */
	public double getLongitude() { return longitude; }
	/**
	 * Function to check GPS/wifi enabled
	 *
	 * @return boolean
	 */
	public boolean canGetLocation() { return this.canGetLocation; }
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 */
	public void showSettingsAlert(Context context) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		// Setting Dialog Title
		alertDialog.setTitle("Location settings");
		// Setting Dialog Message
		alertDialog.setMessage("Location is not enabled. Do you want to go to settings menu?");
		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", (dialog, which) -> {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(intent);
		});
		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
		// Showing Alert Message
		alertDialog.show();
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

}