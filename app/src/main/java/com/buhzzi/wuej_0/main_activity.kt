package com.buhzzi.wuej_0

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.buhzzi.wuej_0.kit.font_constants
import com.buhzzi.wuej_0.kit.location_relative
import com.buhzzi.wuej_0.kit.night_constants
import com.buhzzi.wuej_0.kit.window_constants

class main_activity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		night_constants.init(this)
		window_constants.init(this)
		font_constants.init(this)

		setContentView(R.layout.main_activity)
		findViewById<Button>(R.id.start_Button).setOnClickListener {
			startActivity(Intent(this, wuej_maps_activity::class.java))
		}

		location_relative.start_updating_location(this)
	}

	override fun onDestroy() {
		super.onDestroy()

		location_relative.stop_updating_location(this)
	}
}
