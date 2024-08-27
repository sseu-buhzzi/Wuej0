package com.buhzzi.wuej_0.kit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat

class location_relative private constructor() {
	companion object {
		lateinit var location: Location
		private val location_listener = object : LocationListener {
			override fun onLocationChanged(location: Location) {
				this@Companion.location = location
				print("onLocationChanged")
				print(location)
			}
			override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
				print("onStatusChanged")
				print(provider)
				print(status)
				print(extras)
			}
			override fun onProviderEnabled(provider: String) {
				print("onProviderEnabled")
				print(provider)
			}
			override fun onProviderDisabled(provider: String) {
				print("onProviderDisabled")
				print(provider)
			}
		}
		private fun get_location_manager(ctx: Context): LocationManager {
			return ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		}
		fun start_updating_location(ctx: Context) {
			if (ActivityCompat.checkSelfPermission(
					ctx, Manifest.permission.ACCESS_FINE_LOCATION
			) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
			}
			get_location_manager(ctx).requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1024, 0F, location_listener)
		}
		fun stop_updating_location(ctx: Context) {
			get_location_manager(ctx).removeUpdates(location_listener)
		}
	}
}
