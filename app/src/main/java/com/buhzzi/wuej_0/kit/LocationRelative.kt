package com.buhzzi.wuej_0.kit

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.buhzzi.wuej_0.SettingActivity

class LocationRelative private constructor() {
	companion object {
		var location: Location? = null
			private set
		private val listener = { location: Location -> this.location = location }
		private val locationManager
			get() = StackedActivity.topActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		@SuppressLint("MissingPermission") fun startUpdatingLocation() {
			locationManager.requestLocationUpdates(SettingActivity.provider, SettingActivity.minTime, SettingActivity.minDistance, listener)
		}
		fun stopUpdatingLocation() {
			locationManager.removeUpdates(listener)
		}
	}
}
