package com.buhzzi.wuej_0;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import com.buhzzi.wuej_0.kit.font_constants;
import com.buhzzi.wuej_0.kit.night_constants;
import com.buhzzi.wuej_0.kit.window_constants;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class wuej_maps_view extends android.view.View {
	public static class MapCacheEntry {
		android.graphics.Bitmap bmp;
		CharSequence lyrs;
		int x;
		int y;
		int z;
		long ref_time = System.currentTimeMillis();

		public MapCacheEntry(final android.graphics.Bitmap bmp, final CharSequence lyrs, final int x, final int y, final int z) {
			this.bmp = bmp;
			this.lyrs = lyrs;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public boolean hit(final CharSequence lyrs, final int x, final int y, final int z) {
			return this.x == x && this.y == y && this.z == z && this.lyrs.equals(lyrs);
		}
	}
	public static class BitmapWithBounds {
		android.graphics.Bitmap bmp = null;
		android.graphics.Rect bounds = new android.graphics.Rect();
	}
	public static android.graphics.Bitmap no_data_bmp = android.graphics.Bitmap.createBitmap(128, 128, android.graphics.Bitmap.Config.ARGB_8888);
	static {
		final android.graphics.Canvas no_data_cv = new android.graphics.Canvas(no_data_bmp);
		no_data_cv.drawColor(night_constants.color_back);
		final String no_data_str = "NO DATA";
		final android.graphics.Paint paint = new android.graphics.Paint();
		paint.setColor(night_constants.color_fore);
		paint.setTextSize(32);
		paint.setTypeface(font_constants.xeu_tf);
		final android.graphics.Rect bounds = new android.graphics.Rect();
		paint.getTextBounds(no_data_str, 0, no_data_str.length(), bounds);
		no_data_cv.drawText(no_data_str, (no_data_bmp.getWidth() - (bounds.right - bounds.left)) >> 1, (no_data_bmp.getHeight() + (bounds.bottom - bounds.top)) >> 1, paint);
		paint.setStyle(android.graphics.Paint.Style.STROKE);
		paint.setColor(0xffff0000);
		no_data_cv.drawRect(0, 0, no_data_bmp.getWidth(), no_data_bmp.getHeight(), paint);
	}
	public static android.graphics.Paint contact_paint = new android.graphics.Paint();
	static {
		contact_paint.setColor(0xffff0000);
		contact_paint.setStyle(android.graphics.Paint.Style.STROKE);
		contact_paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
		contact_paint.setStrokeWidth(4);
		contact_paint.setTextSize(64);
		contact_paint.setTypeface(font_constants.xeu_tf);
	}
	public static int MAP_CACHE_SET_NUMB = 256;
	public static int MAP_CACHE_ASSOCIATIVITY = 4;
	public static int MAPS_GRID_COL_NUMB = 5;
	public static int MAPS_GRID_ROW_NUMB = 9;
	public static int FETCHING_LIST_LENGTH = 16;
	public double pix_in_one = 2048;
	public long x_in_one = 0;
	public long y_in_one = 0;
	public float rotation = 0;
	public int d_pix_in_one = 0;
	public int d_x_in_one = 0;
	public int d_y_in_one = 0;
	public float d_rotation = 0;
	public MapCacheEntry[][] map_cache = new MapCacheEntry[MAP_CACHE_SET_NUMB][MAP_CACHE_ASSOCIATIVITY];
	public BitmapWithBounds[][] maps_grid = new BitmapWithBounds[MAPS_GRID_COL_NUMB][MAPS_GRID_ROW_NUMB];
	public String[] contact_names;
	public long[] contact_locas;
//    public java.util.concurrent.Semaphore semaphore = new java.util.concurrent.Semaphore(16);
	public boolean[] fetching_list = new boolean[FETCHING_LIST_LENGTH];
	public android.os.Handler motion_handler = new android.os.Handler(android.os.Looper.getMainLooper());
	public Runnable motion_runnable = () -> {
		final double d_x_in_one = ((long) this.d_x_in_one << 36) / this.pix_in_one;
		final double d_y_in_one = ((long) this.d_y_in_one << 36) / this.pix_in_one;
		final double sin_rotation = Math.sin(this.rotation);
		final double cos_rotation = Math.cos(this.rotation);
		this.pix_in_one = Double.min(Double.max(this.pix_in_one * Math.pow(1.0625, this.d_pix_in_one), 2048), 2147483648L);
		this.x_in_one = this.x_in_one + (long) (d_x_in_one * cos_rotation + d_y_in_one * sin_rotation) & 0x00000000ffffffffL;
		this.y_in_one = this.y_in_one + (long) (d_y_in_one * cos_rotation - d_x_in_one * sin_rotation) & 0x00000000ffffffffL;
		this.rotation += this.d_rotation * 0.0625F;
		this.rotation = this.rotation >= 0 ? this.rotation % 6.2831855F : this.rotation % 6.2831855F + 6.2831855F;
		if (((this.d_pix_in_one | Float.floatToRawIntBits(this.d_rotation)) | (Double.doubleToRawLongBits(this.d_x_in_one) | Double.doubleToRawLongBits(this.d_y_in_one))) != 0) {
			this.load_in_screen_maps();
		}
		this.motion_handler.postDelayed(this.motion_runnable, 64);
	};
	public wuej_maps_view(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);

		for (int x_ndx = 0; x_ndx < MAPS_GRID_COL_NUMB; ++x_ndx) {
			for (int y_ndx = 0; y_ndx < MAPS_GRID_ROW_NUMB; ++y_ndx) {
				this.maps_grid[x_ndx][y_ndx] = new BitmapWithBounds();
			}
		}

		this.contact_names = new String[0];
		this.contact_locas = new long[0];
	}
	public static int get_map_cache_set(final int x, final int y) {
		return x & 0x0f | y << 4 & 0xf0;
	}
	public static long coord_in_one(final android.location.Location location) {
		return longitude_in_one(location.getLongitude()) << 32 | latitude_in_one(location.getLatitude());
	}
	public static long longitude_in_one(final double longi) {
		return (long) (longi * 11930464.711111112) + 0x0000000080000000L;
	}
	public static long latitude_in_one(final double lati) {
		return (long) (Math.log(Math.tan(lati * 0.008726646259971648 + 0.7853981633974483)) * -683565275.5764316) + 0x0000000080000000L;
	}
	@Override public void onAttachedToWindow() {
		super.onAttachedToWindow();

		this.motion_handler.post(this.motion_runnable);
	}
	@Override public void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		this.motion_handler.removeCallbacks(this.motion_runnable);
	}

	@Override public void onDraw(@androidx.annotation.NonNull final android.graphics.Canvas cv) {
		super.onDraw(cv);

		final float rotation = this.rotation * 57.29578F;
		cv.save();
		cv.translate(window_constants.display_metrics.widthPixels >>> 1, window_constants.display_metrics.heightPixels >>> 1);
		cv.rotate(rotation);
		for (final BitmapWithBounds[] maps_col : this.maps_grid) {
			for (final BitmapWithBounds map : maps_col) {
				cv.drawBitmap(map.bmp == null ? no_data_bmp : map.bmp, null, map.bounds, null);
//                cv.drawRect(map.bounds, __TEST_red_edge_paint);
			}
		}
		{
			final long pix_one = (long) this.pix_in_one;
			for (int ndx = 0; ndx < this.contact_names.length; ++ndx) {
				final int x_diff = (int) (((this.contact_locas[ndx] >>> 32) - this.x_in_one) * pix_one >>> 32);
				final int y_diff = (int) (((this.contact_locas[ndx] & 0x00000000ffffffffL) - this.y_in_one) * pix_one >>> 32);
				cv.save();
				cv.translate(x_diff, y_diff);
				cv.rotate(-rotation);
				cv.drawLine(0, 0, 0, -128, contact_paint);
				cv.drawText(this.contact_names[ndx], 8, -64, contact_paint);
				cv.restore();
			}
		}
		cv.restore();
	}
	public void set_camera_location(final android.location.Location location) {
		this.x_in_one = longitude_in_one(location.getLongitude());
		this.y_in_one = latitude_in_one(location.getLatitude());
		this.load_in_screen_maps();
	}
	// Rotate and load
	public void load_in_screen_maps() {
		final long one_px = (long) this.pix_in_one;
		final int z = 63 - Long.numberOfLeadingZeros((MAPS_GRID_ROW_NUMB - 1) * one_px / window_constants.display_metrics.heightPixels);
//        final int z = 8;
		final int size_px = (int) (one_px >>> z);
		final int x = (int) (this.x_in_one >>> 32 - z);
		final int diff_x_px = (int) ((this.x_in_one * one_px >>> 32) - (x * one_px >>> z));
		final int y = (int) (this.y_in_one >>> 32 - z);
		final int diff_y_px = (int) ((this.y_in_one * one_px >>> 32) - (y * one_px >>> z));

//        final int x_off_end = (MAPS_GRID_COL_NUMB >>> 1) + 1;
//        final int x_off_beg = x_off_end - MAPS_GRID_COL_NUMB;
//        final int y_off_end = (MAPS_GRID_ROW_NUMB >>> 1) + 1;
//        final int y_off_beg = y_off_end - MAPS_GRID_ROW_NUMB;
//        for (int x_off = x_off_beg; x_off < x_off_end; ++x_off) {
//            for (int y_off = y_off_beg; y_off < y_off_end; ++y_off) {
//                final android.graphics.Bitmap bmp = this.fetch_map("s", x + x_off & (1 << z) - 1, y + y_off & (1 << z) - 1, z);
//                final BitmapWithBounds map = this.maps_grid[x_off - x_off_beg][y_off - y_off_beg];
//                map.bmp = bmp == null ? no_data_bmp : bmp;
//                map.bounds.left = x_off * size_px - diff_x_px;
//                map.bounds.top = y_off * size_px - diff_y_px;
//                map.bounds.right = map.bounds.left + size_px;
//                map.bounds.bottom = map.bounds.top + size_px;
//            }
//        }
		final int x_base = (1 - MAPS_GRID_COL_NUMB) >> 1;
		final int y_base = (1 - MAPS_GRID_ROW_NUMB) >> 1;
//        final boolean[][] grid_walked = new boolean[MAPS_GRID_COL_NUMB][MAPS_GRID_ROW_NUMB];
//        int unwalked_count = MAPS_GRID_COL_NUMB * MAPS_GRID_ROW_NUMB;
//        float theta = 0;
//        float rho;
////try {
//        do {
//            rho = (float) (theta / java.lang.Math.PI * 0.25);
//            final int x_off = (int) (java.lang.Math.sin(theta) * rho);
//            final int y_off = (int) (java.lang.Math.cos(theta) * rho);
//            final int x_ndx = x_off - x_base;
//            final int y_ndx = y_off - y_base;
//            if (x_ndx >= 0 && y_ndx >= 0 && x_ndx < MAPS_GRID_COL_NUMB && y_ndx < MAPS_GRID_ROW_NUMB && !grid_walked[x_ndx][y_ndx]) {
//                grid_walked[x_ndx][y_ndx] = true;
//                --unwalked_count;
//                final android.graphics.Bitmap bmp = this.fetch_map("s", x + x_off & (1 << z) - 1, y + y_off & (1 << z) - 1, z);
//                final BitmapWithBounds map = this.maps_grid[x_ndx][y_ndx];
//                map.bmp = bmp == null ? no_data_bmp : bmp;
//                map.bounds.left = x_off * size_px - diff_x_px;
//                map.bounds.top = y_off * size_px - diff_y_px;
//                map.bounds.right = map.bounds.left + size_px;
//                map.bounds.bottom = map.bounds.top + size_px;
//            }
//            theta += rho < 1 ? java.lang.Math.PI : 1 / rho;
//        } while (unwalked_count > 0 && rho < MAPS_GRID_ROW_NUMB);
////} catch (final java.lang.Exception e) {
////    e.printStackTrace();
////}
		int x_off = 0;
		int y_off = 0;
		int ward = 0;
		while (true) {
			final int x_ndx = x_off - x_base;
			final int y_ndx = y_off - y_base;
			final int x_exceed = Integer.compareUnsigned(x_ndx, MAPS_GRID_COL_NUMB);
			final int y_exceed = Integer.compareUnsigned(y_ndx, MAPS_GRID_ROW_NUMB);
			if ((x_exceed & y_exceed) < 0) {
				final BitmapWithBounds map = this.maps_grid[x_ndx][y_ndx];
//                map.bmp = null;
				map.bmp = transit_to_fetch_map_but_who_knows_whats_from_240827("w", x, x_off, y, y_off, z);
				map.bounds.left = x_off * size_px - diff_x_px;
				map.bounds.top = y_off * size_px - diff_y_px;
				map.bounds.right = map.bounds.left + size_px;
				map.bounds.bottom = map.bounds.top + size_px;
			}
			if ((x_exceed | y_exceed) > 0) {
				break;
			}
			switch (ward = ward & 3) {
				case 0:
				++x_off;
				ward += x_off + y_off == 1 ? 1 : 0;
				continue;
				case 1:
				++y_off;
				ward += x_off == y_off ? 1 : 0;
				continue;
				case 2:
				--x_off;
				ward += x_off + y_off == 0 ? 1 : 0;
				continue;
				case 3:
				--y_off;
				ward += x_off == y_off ? 1 : 0;
			}
		}
		this.invalidate();
	}
	private Bitmap transit_to_fetch_map_but_who_knows_whats_from_240827(CharSequence lyrs, int x, int x_off, int y, int y_off, int z) {
		return fetch_map(lyrs, x + x_off & (1 << z) - 1, y + y_off & (1 << z) - 1, z);
	}
	public android.graphics.Bitmap fetch_map(final CharSequence lyrs, final int x, final int y, final int z) {
		final int map_cache_set_ndx = get_map_cache_set(x, y);
		final int map_cache_ent_ndx;
		{
			final MapCacheEntry[] map_cache_set = this.map_cache[map_cache_set_ndx];
			int ndx = -1;
			int evict_ndx = 0;
			while (true) {
				if (++ndx == MAP_CACHE_ASSOCIATIVITY) {
					map_cache_ent_ndx = evict_ndx;
					break;
				}
				if (map_cache_set[ndx] == null) {
					map_cache_ent_ndx = ndx;
					break;
				}
				if (map_cache_set[ndx].hit(lyrs, x, y, z)) {
					return map_cache_set[ndx].bmp;
				}
				if (map_cache_set[ndx].ref_time < map_cache_set[evict_ndx].ref_time) {
					evict_ndx = ndx;
				}
			}
		}
		final int fetching_ndx;
		{
			int ndx = -1;
			do {
				if (++ndx == this.fetching_list.length) {
					return null;
				}
			} while (this.fetching_list[ndx]);
			this.fetching_list[fetching_ndx = ndx] = true;
		}
		new Thread(() -> {
			try {
				final android.graphics.Bitmap bmp = request_tile(lyrs, x, y, z);
				assert bmp != null;
				this.map_cache[map_cache_set_ndx][map_cache_ent_ndx] = new MapCacheEntry(bmp, lyrs, x, y, z);
				this.load_in_screen_maps();
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
//                this.semaphore.release();
				this.fetching_list[fetching_ndx] = false;
			}
		}).start();
		return null;
	}
	public static Bitmap request_tile(CharSequence lyrs, int x, int y, int z) throws Exception {
		@SuppressLint("DefaultLocale") final String url = String.format(
			"https://t2.tianditu.gov.cn/vec_w/wmts?tk=e2615b864327530e863275603fee58b3&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=%s&FORMAT=tiles&TILECOL=%d&TILEROW=%d&TILEMATRIX=%d",
			lyrs, x, y, z
		);
//		final Map<String, String> fields = map_helper.of(
//			"accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
//			"accept-encoding", "gzip, deflate, br, zstd",
//			"accept-language", "en-US,en;q=0.9,en-GB;q=0.8",
//			"cache-control", "max-age=0",
//			"connection", "keep-alive",
//			"cookie", "HWWAFSESID=a8b5d24a08fb8a530e1; HWWAFSESTIME=1724769572530",
//			"host", "t2.tianditu.gov.cn",
//			"sec-ch-ua", "\"Not)A;Brand\";v=\"99\", \"Microsoft Edge\";v=\"127\", \"Chromium\";v=\"127\"",
//			"sec-ch-ua-mobile", "?0",
//			"sec-ch-ua-platform", "\"Windows\"",
//			"sec-fetch-dest", "document",
//			"sec-fetch-mode", "navigate",
//			"sec-fetch-site", "none",
//			"sec-fetch-user", "?1",
//			"upgrade-insecure-requests", "1",
//			"user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36 Edg/127.0.0.0"
//		);
		final HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
		try {
			conn.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36 Edg/127.0.0.0");
			conn.connect();
			try (final InputStream is = conn.getInputStream()) {
				return BitmapFactory.decodeStream(is);
			}
		} finally {
			conn.disconnect();
		}
	}
}
