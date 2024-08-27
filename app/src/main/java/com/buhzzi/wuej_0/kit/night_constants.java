package com.buhzzi.wuej_0.kit;

import android.content.Context;
import android.content.res.Configuration;

public class night_constants {
	public static boolean is_night;
	public static int color_back = 0xffffffff;
	public static int color_fore = 0xff000000;
	public static void init(Context ctx) {
		is_night = (ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
		if (is_night) {
			color_back = 0xff000000;
			color_fore = 0xffffffff;
		}
	}
}
