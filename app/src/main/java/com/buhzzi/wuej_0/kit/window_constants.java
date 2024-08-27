package com.buhzzi.wuej_0.kit;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class window_constants {
	public static final DisplayMetrics display_metrics = new DisplayMetrics();
	public static void init(Context ctx) {
		ctx.getSystemService(WindowManager.class).getDefaultDisplay().getMetrics(display_metrics);
	}
}
