package com.mmdev.meetups.utils;

import android.location.Location;

import com.mmdev.meetups.services.LocationService;

import androidx.lifecycle.LiveData;

public class LocationLiveData extends LiveData<Location>
{

	LocationService.LocationListener locationListener = new LocationService.LocationListener("Provider") {
		@Override
		public void onLocationChanged(Location location) { setValue(location); }
	};

	@Override
	protected void onActive() {
		LocationService.addListener(locationListener);
	}

	@Override
	protected void onInactive() {
		LocationService.removeListener(locationListener);
	}

}
