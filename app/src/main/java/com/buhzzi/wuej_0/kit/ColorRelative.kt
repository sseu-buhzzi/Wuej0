package com.buhzzi.wuej_0.kit

import android.content.res.Configuration

class ColorRelative {
	companion object {
		val inNight
			get() = StackedActivity.topActivity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
		val colorBack
			get() = (if (inNight) 0xff000000 else 0xffffffff).toInt()
		val colorFore
			get() = (if (inNight) 0xffffffff else 0xff000000).toInt()
	}
}
