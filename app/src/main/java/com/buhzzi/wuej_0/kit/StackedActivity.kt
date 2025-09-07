package com.buhzzi.wuej_0.kit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class StackedActivity : AppCompatActivity() {
	companion object {
		private val activityStack = mutableListOf<StackedActivity>()
		val topActivity
			get() = activityStack[activityStack.size - 1]
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		activityStack.add(this)
	}

	override fun onDestroy() {
		super.onDestroy()

		activityStack.removeAt(activityStack.size - 1)
	}
}
