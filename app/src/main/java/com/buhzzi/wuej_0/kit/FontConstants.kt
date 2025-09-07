package com.buhzzi.wuej_0.kit

import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.buhzzi.wuej_0.R


class FontConstants {
	companion object {
		val xeuTf: Typeface
			get() = ResourcesCompat.getFont(StackedActivity.topActivity, R.font.xeufont_ascii)!!
	}
}
