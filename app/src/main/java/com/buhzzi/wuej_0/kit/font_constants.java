package com.buhzzi.wuej_0.kit;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.buhzzi.wuej_0.R;

public class font_constants {
	public static Typeface xeu_tf;
	public static void init(Context ctx) {
		xeu_tf = ResourcesCompat.getFont(ctx, R.font.xeufont_ascii);
	}
}
