package com.buhzzi.wuej_0.kit

import android.util.DisplayMetrics
import kotlin.math.roundToInt

class MetricsRelative {
	companion object {
		val displayMetrics: DisplayMetrics
			get() = StackedActivity.topActivity.resources.displayMetrics

		fun dpToPx(dp: Float): Int {
			return (dp * displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).roundToInt()
		}

		fun pxToDp(px: Int): Float {
			return (px * DisplayMetrics.DENSITY_DEFAULT).toFloat() / displayMetrics.densityDpi
		}
	}
}
