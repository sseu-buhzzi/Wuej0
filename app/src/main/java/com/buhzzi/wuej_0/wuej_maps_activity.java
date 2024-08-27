package com.buhzzi.wuej_0;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.buhzzi.wuej_0.kit.location_relative;
import com.buhzzi.wuej_0.kit.night_constants;
import com.buhzzi.wuej_0.kit.server_relative;
import com.buhzzi.wuej_0.kit.window_constants;

public class wuej_maps_activity extends AppCompatActivity {
	public wuej_maps_view maps_view;
	public int cntl_visibility = View.VISIBLE;
	public android.widget.ImageView cntl_cc_ImageView;
	public android.widget.ImageView cntl_arrow_right_ImageView;
	public android.widget.ImageView cntl_arrow_up_ImageView;
	public android.widget.ImageView cntl_arrow_left_ImageView;
	public android.widget.ImageView cntl_arrow_down_ImageView;
	public android.widget.ImageView cntl_rota_rcw_ImageView;
	public android.widget.ImageView cntl_rota_cw_ImageView;
	public android.widget.ImageView cntl_zoom_in_ImageView;
	public android.widget.ImageView cntl_zoom_out_ImageView;
	public android.widget.ImageView central_cursor_ImageView;
	public android.os.Handler locating_handler;
	@android.annotation.SuppressLint({"MissingPermission"}) public Runnable locating_runnable = () -> {
//		getSystemService(android.location.LocationManager.class).getCurrentLocation(android.location.LocationManager.GPS_PROVIDER, null, getMainExecutor(), (final android.location.Location user_loca_gps) -> {
//			if (user_loca_gps == null) {
//				getSystemService(android.location.LocationManager.class).getCurrentLocation(android.location.LocationManager.NETWORK_PROVIDER,  null, getMainExecutor(), (final android.location.Location user_loca_network) -> {
//					if (user_loca_network == null) {
//						return;
//					}
//					try {
//						update_location(wuej_maps_view.coord_in_one(user_loca_network), 0x7774656e /* "netw" */ );
//					} catch (final Exception e) { }
//				});
//				return;
//			}
//			try {
//				update_location(wuej_maps_view.coord_in_one(user_loca_gps), 0x00737067 /* "gps\0" */);
//			} catch (final Exception e) { }
//		});
		maps_view.x_in_one = wuej_maps_view.longitude_in_one(location_relative.location.getLongitude());
		maps_view.y_in_one = wuej_maps_view.latitude_in_one(location_relative.location.getLatitude());
		locating_handler.postDelayed(this.locating_runnable, 1024);
	};
	@Override public void onCreate(final android.os.Bundle saved_instance_state) {
		super.onCreate(saved_instance_state);

		server_relative.init(this);

		try {
			setContentView(R.layout.wuej_maps_activity);
			init_maps_view();
			init_cntl_views();

			(locating_handler = new android.os.Handler(android.os.Looper.getMainLooper())).post(locating_runnable);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	@Override public void onDestroy() {
		super.onDestroy();

		locating_handler.removeCallbacks(locating_runnable);
	}
//    @java.lang.Override public void onRequestPermissionsResult(final int request_code, final java.lang.String[] permissions, final int[] grant_results) {
//        super.onRequestPermissionsResult(request_code, permissions, grant_results);
//
//        switch (request_code) {
//            case 0x240221:
////            android.content.pm.PackageManager.
//        }
//    }
	public void init_maps_view() throws Exception {
		maps_view = findViewById(R.id.maps_view);
		if (ensure_locating_permission()) {
			@android.annotation.SuppressLint({"MissingPermission"}) final android.location.Location user_loca = getSystemService(android.location.LocationManager.class).getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
			if (user_loca != null) {
				maps_view.set_camera_location(user_loca);
				return;
			}
		}
		maps_view.load_in_screen_maps();
	}
	public View.OnTouchListener get_cntl_on_touch(int d_pix_in_one, int d_x_in_one, int d_y_in_one, float d_rotation) {
		return (cntl_on_touch_view, cntl_on_touch_event) -> {
			cntl_on_touch_view.performClick();

			switch (cntl_on_touch_event.getAction()) {
				case android.view.MotionEvent.ACTION_DOWN:
				cntl_on_touch_view.setAlpha(0.5F);
				maps_view.d_pix_in_one = d_pix_in_one;
				maps_view.d_x_in_one = d_x_in_one;
				maps_view.d_y_in_one = d_y_in_one;
				maps_view.d_rotation = d_rotation;
				return true;

				case android.view.MotionEvent.ACTION_UP:
				cntl_on_touch_view.setAlpha(1);
				maps_view.d_pix_in_one = 0;
				maps_view.d_x_in_one = 0;
				maps_view.d_y_in_one = 0;
				maps_view.d_rotation = 0;
				return true;
			}
			return false;
		};
	}
	@android.annotation.SuppressLint({"ClickableViewAccessibility"}) public void init_cntl_views() {
		cntl_cc_ImageView = findViewById(R.id.cntl_cc_ImageView);
		cntl_cc_ImageView.setImageBitmap(draw_cntl_cc());
		cntl_cc_ImageView.setOnClickListener((final android.view.View cntl_cc_on_touch_view) -> {
			maps_view.load_in_screen_maps();
			cntl_visibility = cntl_visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
			cntl_arrow_right_ImageView.setVisibility(cntl_visibility);
			cntl_arrow_up_ImageView.setVisibility(cntl_visibility);
			cntl_arrow_left_ImageView.setVisibility(cntl_visibility);
			cntl_arrow_down_ImageView.setVisibility(cntl_visibility);
			cntl_rota_rcw_ImageView.setVisibility(cntl_visibility);
			cntl_rota_cw_ImageView.setVisibility(cntl_visibility);
			cntl_zoom_in_ImageView.setVisibility(cntl_visibility);
			cntl_zoom_out_ImageView.setVisibility(cntl_visibility);
			central_cursor_ImageView.setVisibility(cntl_visibility);
			cntl_cc_ImageView.setImageBitmap(draw_cntl_cc());
		});

		cntl_arrow_right_ImageView = findViewById(R.id.cntl_arrow_right_ImageView);
		cntl_arrow_right_ImageView.setImageBitmap(draw_cntl_arrow());
		cntl_arrow_right_ImageView.setOnTouchListener(get_cntl_on_touch(0, 1, 0, 0));

		cntl_arrow_up_ImageView = findViewById(R.id.cntl_arrow_up_ImageView);
		cntl_arrow_up_ImageView.setImageBitmap(draw_cntl_arrow());
		cntl_arrow_up_ImageView.setOnTouchListener(get_cntl_on_touch(0, 0, -1, 0));

		cntl_arrow_left_ImageView = findViewById(R.id.cntl_arrow_left_ImageView);
		cntl_arrow_left_ImageView.setImageBitmap(draw_cntl_arrow());
		cntl_arrow_left_ImageView.setOnTouchListener(get_cntl_on_touch(0, -1, 0, 0));

		cntl_arrow_down_ImageView = findViewById(R.id.cntl_arrow_down_ImageView);
		cntl_arrow_down_ImageView.setImageBitmap(draw_cntl_arrow());
		cntl_arrow_down_ImageView.setOnTouchListener(get_cntl_on_touch(0, 0, 1, 0));

		cntl_rota_rcw_ImageView = findViewById(R.id.cntl_rota_rcw_ImageView);
		cntl_rota_rcw_ImageView.setImageBitmap(draw_cntl_rota());
		cntl_rota_rcw_ImageView.setOnTouchListener(get_cntl_on_touch(0, 0, 0, 1));

		cntl_rota_cw_ImageView = findViewById(R.id.cntl_rota_cw_ImageView);
		cntl_rota_cw_ImageView.setImageBitmap(draw_cntl_rota());
		cntl_rota_cw_ImageView.setOnTouchListener(get_cntl_on_touch(0, 0, 0, -1));

		cntl_zoom_in_ImageView = findViewById(R.id.cntl_zoom_in_ImageView);
		cntl_zoom_in_ImageView.setImageBitmap(draw_cntl_zoom_in());
		cntl_zoom_in_ImageView.setOnTouchListener(get_cntl_on_touch(1, 0, 0, 0));

		cntl_zoom_out_ImageView = findViewById(R.id.cntl_zoom_out_ImageView);
		cntl_zoom_out_ImageView.setImageBitmap(draw_cntl_zoom_out());
		cntl_zoom_out_ImageView.setOnTouchListener(get_cntl_on_touch(-1, 0, 0, 0));

		central_cursor_ImageView = findViewById(R.id.central_cursor_ImageView);
		central_cursor_ImageView.setImageBitmap(draw_central_cursor());
	}
	public android.graphics.Bitmap draw_cntl_cc() {
		final int size = window_constants.display_metrics.widthPixels >>> 3;
		final android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
			size, size,
			android.graphics.Bitmap.Config.ARGB_8888
		);
		final boolean cntl_folded = cntl_visibility != View.VISIBLE;
		final android.graphics.Canvas cv = new android.graphics.Canvas(bmp);
		cv.drawColor(cntl_folded ? 0x40808080 : 0xc0808080);
		final android.graphics.Paint paint = new android.graphics.Paint();
		paint.setColor(night_constants.color_fore);
		if (!cntl_folded) {
			paint.setStyle(android.graphics.Paint.Style.STROKE);
			paint.setStrokeWidth(4);
		}
		cv.drawCircle(size >>> 1, size >>> 1, size * 3 >>> 3, paint);
		return bmp;
	}
	public static android.graphics.Bitmap draw_cntl_arrow() {
		final int size = window_constants.display_metrics.widthPixels >>> 3;
		final android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
			size, size,
			android.graphics.Bitmap.Config.ARGB_8888
		);
		final android.graphics.Canvas cv = new android.graphics.Canvas(bmp);
		cv.drawColor(0x80808080);
		final android.graphics.Path path = new android.graphics.Path();
		path.moveTo(size >>> 2, size >>> 2);
		path.lineTo(size * 3 >>> 2, size >>> 1);
		path.lineTo(size >>> 2, size * 3 >>> 2);
		final android.graphics.Paint paint = new android.graphics.Paint();
		paint.setColor(night_constants.color_fore);
		paint.setStyle(android.graphics.Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
		cv.drawPath(path, paint);
		return bmp;
	}
	public static android.graphics.Bitmap draw_cntl_rota() {
		final int size = window_constants.display_metrics.widthPixels >>> 3;
		final android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
			size, size,
			android.graphics.Bitmap.Config.ARGB_8888
		);
		final android.graphics.Canvas cv = new android.graphics.Canvas(bmp);
		cv.drawColor(0x40808080);
		final android.graphics.Path path = new android.graphics.Path();
		path.arcTo(size >>> 4, size * -1.9375F, size * 2.9375F, size * 0.9375F, 150, -30, true);
		path.lineTo(size >>> 2, size * 3 >>> 2);
		final android.graphics.Paint paint = new android.graphics.Paint();
		paint.setColor(night_constants.color_fore);
		paint.setStyle(android.graphics.Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
		cv.drawPath(path, paint);
		return bmp;
	}
	public static android.graphics.Bitmap draw_cntl_zoom_in() {
		final int size = window_constants.display_metrics.widthPixels >>> 3;
		final android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
			size, size,
			android.graphics.Bitmap.Config.ARGB_8888
		);
		final android.graphics.Canvas cv = new android.graphics.Canvas(bmp);
		cv.drawColor(0x40808080);
		final android.graphics.Path path = new android.graphics.Path();
		path.moveTo(size >>> 1, size >>> 2);
		path.lineTo(size >>> 1, size * 3 >>> 2);
		path.moveTo(size >>> 2, size >>> 1);
		path.lineTo(size * 3 >>> 2, size >>> 1);
		final android.graphics.Paint paint = new android.graphics.Paint();
		paint.setColor(night_constants.color_fore);
		paint.setStyle(android.graphics.Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
		cv.drawPath(path, paint);
		return bmp;
	}
	public static android.graphics.Bitmap draw_cntl_zoom_out() {
		final int size = window_constants.display_metrics.widthPixels >>> 3;
		final android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
			size, size,
			android.graphics.Bitmap.Config.ARGB_8888
		);
		final android.graphics.Canvas cv = new android.graphics.Canvas(bmp);
		cv.drawColor(0x40808080);
		final android.graphics.Path path = new android.graphics.Path();
		path.moveTo(size >>> 2, size >>> 1);
		path.lineTo(size * 3 >>> 2, size >>> 1);
		final android.graphics.Paint paint = new android.graphics.Paint();
		paint.setColor(night_constants.color_fore);
		paint.setStyle(android.graphics.Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
		cv.drawPath(path, paint);
		return bmp;
	}
	public static android.graphics.Bitmap draw_central_cursor() {
		final int size = window_constants.display_metrics.widthPixels >>> 4;
		final android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(
			size, size,
			android.graphics.Bitmap.Config.ARGB_8888
		);
		final android.graphics.Canvas cv = new android.graphics.Canvas(bmp);
		cv.drawColor(0x00000000);
		final android.graphics.Path path = new android.graphics.Path();
		path.moveTo(size >>> 1, 0);
		path.lineTo(size >>> 1, size * 3 >>> 3);
		path.moveTo(size >>> 1, size * 5 >>> 3);
		path.lineTo(size >>> 1, size);
		path.moveTo(0, size >>> 1);
		path.lineTo(size * 3 >>> 3, size >>> 1);
		path.moveTo(size * 5 >>> 3, size >>> 1);
		path.lineTo(size, size >>> 1);
		final android.graphics.Paint paint = new android.graphics.Paint();
		paint.setColor(0xffff0000);
		paint.setStyle(android.graphics.Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
		cv.drawPath(path, paint);
		return bmp;
	}
	public boolean ensure_locating_permission() {
		if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0x24022116);
		return false;
	}
	public void update_location(final long coord_one, final int type) { }
}
