package com.buhzzi.wuej_0

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.buhzzi.wuej_0.kit.StackedActivity

class MainActivity : StackedActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main_activity)

		startActivityWithViewId(R.id.mapsButton, WuejMapsActivity::class.java)
		startActivityWithViewId(R.id.settingButton, SettingActivity::class.java)
	}
	private fun <ActivityT : Activity> startActivityWithViewId(id: Int, cls: Class<ActivityT>) = findViewById<View>(id).setOnClickListener {
		startActivity(Intent(this, cls))
	}
}
