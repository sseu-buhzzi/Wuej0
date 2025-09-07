package com.buhzzi.wuej_0

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.buhzzi.wuej_0.kit.StackedActivity

class MainActivity : StackedActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main_activity)

		bindActivityEntryWithViewId(R.id.mapsButton, WuejMapsActivity::class.java)
		bindActivityEntryWithViewId(R.id.settingButton, SettingActivity::class.java)
		bindActivityEntryWithViewId(R.id.updateButton, UpdateActivity::class.java)
	}
	private fun <ActivityT : Activity> bindActivityEntryWithViewId(id: Int, cls: Class<ActivityT>) = findViewById<View>(id).setOnClickListener {
		startActivity(Intent(this, cls))
	}
	private fun ignoreBatteryOptimization() = startActivity(
		Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
	)
}
