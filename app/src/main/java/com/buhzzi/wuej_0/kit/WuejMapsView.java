package com.buhzzi.wuej_0.kit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;

import com.buhzzi.wuej_0.SettingActivity;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WuejMapsView extends View {
	public static class MapCacheEntry {
		Bitmap bmp;
		CharSequence lyrs;
		int x;
		int y;
		int z;
		long refTime = System.currentTimeMillis();

		public MapCacheEntry(final Bitmap bmp, final CharSequence lyrs, final int x, final int y, final int z) {
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
		Bitmap bmp = null;
		Rect bounds = new Rect();
	}

	public static Bitmap noDataBmp = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);

	static {
		final Canvas no_data_cv = new Canvas(noDataBmp);
		no_data_cv.drawColor(ColorRelative.Companion.getColorBack());
		final String no_data_str = "NO DATA";
		final Paint paint = new Paint();
		paint.setColor(ColorRelative.Companion.getColorFore());
		paint.setTextSize(32);
		paint.setTypeface(FontConstants.Companion.getXeuTf());
		final Rect bounds = new Rect();
		paint.getTextBounds(no_data_str, 0, no_data_str.length(), bounds);
		no_data_cv.drawText(no_data_str, (noDataBmp.getWidth() - (bounds.right - bounds.left)) >> 1, (noDataBmp.getHeight() + (bounds.bottom - bounds.top)) >> 1, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xffff0000);
		no_data_cv.drawRect(0, 0, noDataBmp.getWidth(), noDataBmp.getHeight(), paint);
	}

	public static Paint contactStrokePaint = new Paint();
	public static Paint contactFillPaint = new Paint();

	static {
		contactStrokePaint.setColor(0xffff0000);
		contactStrokePaint.setStyle(Paint.Style.STROKE);
		contactStrokePaint.setStrokeCap(Paint.Cap.ROUND);
		contactStrokePaint.setStrokeWidth(4);
		contactStrokePaint.setTextSize(64);
		contactStrokePaint.setTypeface(FontConstants.Companion.getXeuTf());

		contactFillPaint.setColor(0xffff0000);
		contactFillPaint.setStyle(Paint.Style.FILL);
		contactFillPaint.setTextSize(64);
		contactFillPaint.setTypeface(FontConstants.Companion.getXeuTf());
	}

	private int mapsViewWidth;
	private int mapsViewHeight;
	public static int MAP_CACHE_SET_NUMB = 256;
	public static int MAP_CACHE_ASSOCIATIVITY = 4;
	public static int MAPS_GRID_COL_NUMB = 5;
	public static int MAPS_GRID_ROW_NUMB = 9;
	public static int FETCHING_LIST_LENGTH = 16;
	public double pixInOne = 4096;
	public long xInOne = 0;
	public long yInOne = 0;
	public float rotation = 0;
	public int dPixInOne = 0;
	public int dXInOne = 0;
	public int dYInOne = 0;
	public float dRotation = 0;
	public MapCacheEntry[][] mapCache = new MapCacheEntry[MAP_CACHE_SET_NUMB][MAP_CACHE_ASSOCIATIVITY];
	public BitmapWithBounds[][] mapsGrid = new BitmapWithBounds[MAPS_GRID_COL_NUMB][MAPS_GRID_ROW_NUMB];
	private float lastTouchedX = 0F;
	private float lastTouchedY = 0F;
	private boolean multiPointerLock = false; // Lock the slide action when stop zooming (though only one pointer was in the screen).
	private final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(StackedActivity.Companion.getTopActivity(), new ScaleGestureDetector.OnScaleGestureListener() {
		@Override
		public boolean onScale(@NonNull ScaleGestureDetector detector) {
			dPixInOne = (int) (Math.log(detector.getScaleFactor()) * 128);
			return true;
		}

		@Override
		public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
			dPixInOne /= 2;
		}
	});
	public String[] contactNames;
	public long[] contactLocaArr;
	//    public java.util.concurrent.Semaphore semaphore = new java.util.concurrent.Semaphore(16);
	public boolean[] fetchingList = new boolean[FETCHING_LIST_LENGTH];
	public Handler motionHandler = new Handler(Looper.getMainLooper());
	public Runnable motionRunnable = () -> {
		this.motionHandler.postDelayed(this.motionRunnable, 64);
		final double d_x_in_one = ((long) this.dXInOne << 36) / this.pixInOne;
		final double d_y_in_one = ((long) this.dYInOne << 36) / this.pixInOne;
		final double sin_rotation = Math.sin(this.rotation);
		final double cos_rotation = Math.cos(this.rotation);
		this.pixInOne = Double.min(Double.max(this.pixInOne * Math.pow(1.0625, this.dPixInOne), 2048), 0x80000000L);
		this.xInOne = this.xInOne + (long) (d_x_in_one * cos_rotation + d_y_in_one * sin_rotation) & 0x00000000ffffffffL;
		this.yInOne = this.yInOne + (long) (d_y_in_one * cos_rotation - d_x_in_one * sin_rotation) & 0x00000000ffffffffL;
		this.rotation += this.dRotation * 0.0625F;
		this.rotation = this.rotation >= 0 ? this.rotation % 6.2831855F : this.rotation % 6.2831855F + 6.2831855F;
		if (((this.dPixInOne | Float.floatToRawIntBits(this.dRotation)) | (Double.doubleToRawLongBits(this.dXInOne) | Double.doubleToRawLongBits(this.dYInOne))) != 0) {
			this.loadInScreenMaps();
		}
	};

	public WuejMapsView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);

		for (int x_ndx = 0; x_ndx < MAPS_GRID_COL_NUMB; ++x_ndx) {
			for (int y_ndx = 0; y_ndx < MAPS_GRID_ROW_NUMB; ++y_ndx) {
				this.mapsGrid[x_ndx][y_ndx] = new BitmapWithBounds();
			}
		}

		this.contactNames = new String[0];
		this.contactLocaArr = new long[0];
	}

	public static int getMapCacheSet(final int x, final int y) {
		return x & 0x0f | y << 4 & 0xf0;
	}

	public static long coordInOne(final android.location.Location location) {
		return longitudeInOne(location.getLongitude()) << 32 | latitudeInOne(location.getLatitude());
	}

	// 2^{31}\left(\frac\lambda{180}\right)
	public static long longitudeInOne(final double longi) {
		return (long) (longi * 11930464.711111112) + 0x0000000080000000L;
	}

	// 2^{31}\left(1-\frac1\pi \ln\left(tan\left(\frac{\pi\phi}{360}+\frac\pi4\right)\right)\right)
	public static long latitudeInOne(final double lati) {
		return (long) (Math.log(Math.tan(lati * 0.008726646259971648 + 0.7853981633974483)) * -683565275.5764316) + 0x0000000080000000L;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		this.motionHandler.post(this.motionRunnable);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		this.motionHandler.removeCallbacks(this.motionRunnable);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mapsViewWidth = w;
		mapsViewHeight = h;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		scaleGestureDetector.onTouchEvent(event);
		final float x = event.getX();
		final float y = event.getY();

		if (!multiPointerLock) {
			dPixInOne = 0;
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastTouchedX = x;
				lastTouchedY = y;
				break;
			case MotionEvent.ACTION_UP:
				dXInOne /= 2;
				dYInOne /= 2;
				multiPointerLock = false;
				break;
			case MotionEvent.ACTION_MOVE:
				if (multiPointerLock |= event.getPointerCount() > 1) {
					dXInOne = dYInOne = 0;
				} else {
					dXInOne = (int) ((lastTouchedX - x) * 0.5F);
					dYInOne = (int) ((lastTouchedY - y) * 0.5F);
					lastTouchedX = x;
					lastTouchedY = y;
				}
		}
		return true;
	}

	@Override
	public void onDraw(@androidx.annotation.NonNull final Canvas cv) {
		super.onDraw(cv);

		final float rotation = this.rotation * 57.29578F;
		cv.save();
		cv.translate(mapsViewWidth >>> 1, mapsViewHeight >>> 1);
		cv.rotate(rotation);
		for (final BitmapWithBounds[] mapsCol : this.mapsGrid) {
			for (final BitmapWithBounds map : mapsCol) {
				cv.drawBitmap(map.bmp == null ? noDataBmp : map.bmp, null, map.bounds, null);
//                cv.drawRect(map.bounds, __TEST_red_edge_paint);
			}
		}
		{
			final long pixOne = (long) this.pixInOne;
			for (int ndx = 0; ndx < this.contactNames.length; ++ndx) {
				final int xDiff = (int) (((this.contactLocaArr[ndx] >>> 32) - this.xInOne) * pixOne >>> 32);
				final int yDiff = (int) (((this.contactLocaArr[ndx] & 0x00000000ffffffffL) - this.yInOne) * pixOne >>> 32);
				cv.save();
				cv.translate(xDiff, yDiff);
				cv.rotate(-rotation);
				cv.drawLine(0, 0, 0, -128, contactStrokePaint);
				cv.drawText(this.contactNames[ndx], 8, -64, contactFillPaint);
				cv.restore();
			}
		}
		cv.restore();
	}

	public void setCameraLocation(@NonNull final android.location.Location location) {
		this.xInOne = longitudeInOne(location.getLongitude());
		this.yInOne = latitudeInOne(location.getLatitude());
		this.loadInScreenMaps();
	}

	// Rotate and load
	public void loadInScreenMaps() {
		final long onePx = (long) pixInOne;
		final long correctedXInOne = xInOne + SettingActivity.Companion.getMapsSource().getOffsetXInOne();
		final long correctedYInOne = yInOne + SettingActivity.Companion.getMapsSource().getOffsetYInOne();
		final int z = 63 - Long.numberOfLeadingZeros((MAPS_GRID_ROW_NUMB - 1) * onePx / mapsViewHeight);
//        final int z = 8;
		final int sizePx = (int) (onePx >>> z);
		final int x = (int) (correctedXInOne >>> 32 - z);
		final int diffXPx = (int) ((correctedXInOne * onePx >>> 32) - (x * onePx >>> z));
		final int y = (int) (correctedYInOne >>> 32 - z);
		final int diffYPx = (int) ((correctedYInOne * onePx >>> 32) - (y * onePx >>> z));

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
		final int xBase = (1 - MAPS_GRID_COL_NUMB) >> 1;
		final int yBase = (1 - MAPS_GRID_ROW_NUMB) >> 1;
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
		int xOff = 0;
		int yOff = 0;
		int ward = 0;
		while (true) {
			final int xNdx = xOff - xBase;
			final int yNdx = yOff - yBase;
			final int xExceed = Integer.compareUnsigned(xNdx, MAPS_GRID_COL_NUMB);
			final int yExceed = Integer.compareUnsigned(yNdx, MAPS_GRID_ROW_NUMB);
			if ((xExceed & yExceed) < 0) {
				final BitmapWithBounds map = this.mapsGrid[xNdx][yNdx];
//                map.bmp = null;
				map.bmp = transitToFetchMapButWhoKnowsWhatsFrom240827("w", x, xOff, y, yOff, z);
				map.bounds.left = xOff * sizePx - diffXPx;
				map.bounds.top = yOff * sizePx - diffYPx;
				map.bounds.right = map.bounds.left + sizePx;
				map.bounds.bottom = map.bounds.top + sizePx;
			}
			if ((xExceed | yExceed) > 0) {
				break;
			}
			switch (ward = ward & 3) {
				case 0:
					++xOff;
					ward += xOff + yOff == 1 ? 1 : 0;
					continue;
				case 1:
					++yOff;
					ward += xOff == yOff ? 1 : 0;
					continue;
				case 2:
					--xOff;
					ward += xOff + yOff == 0 ? 1 : 0;
					continue;
				case 3:
					--yOff;
					ward += xOff == yOff ? 1 : 0;
			}
		}
		this.invalidate();
	}

	private Bitmap transitToFetchMapButWhoKnowsWhatsFrom240827(CharSequence lyrs, int x, int x_off, int y, int y_off, int z) {
		// (1 << z) - 1 is the mask of no excess bits in current zoom level.
		return fetchMap(lyrs, x + x_off & (1 << z) - 1, y + y_off & (1 << z) - 1, z);
	}

	public Bitmap fetchMap(final CharSequence lyrs, final int x, final int y, final int z) {
		final int mapCacheSetNdx = getMapCacheSet(x, y);
		final int mapCacheEntNdx;
		{
			final MapCacheEntry[] map_cache_set = this.mapCache[mapCacheSetNdx];
			int ndx = -1;
			int evict_ndx = 0;
			while (true) {
				if (++ndx == MAP_CACHE_ASSOCIATIVITY) {
					mapCacheEntNdx = evict_ndx;
					break;
				}
				if (map_cache_set[ndx] == null) {
					mapCacheEntNdx = ndx;
					break;
				}
				if (map_cache_set[ndx].hit(lyrs, x, y, z)) {
					return map_cache_set[ndx].bmp;
				}
				if (map_cache_set[ndx].refTime < map_cache_set[evict_ndx].refTime) {
					evict_ndx = ndx;
				}
			}
		}
		final int fetching_ndx;
		{
			int ndx = -1;
			do {
				if (++ndx == this.fetchingList.length) {
					return null;
				}
			} while (this.fetchingList[ndx]);
			this.fetchingList[fetching_ndx = ndx] = true;
		}
		new Thread(() -> {
			try {
				final Bitmap bmp = requestTile(lyrs, x, y, z);
				assert bmp != null;
				this.mapCache[mapCacheSetNdx][mapCacheEntNdx] = new MapCacheEntry(bmp, lyrs, x, y, z);
				this.loadInScreenMaps();
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
//                this.semaphore.release();
				this.fetchingList[fetching_ndx] = false;
			}
		}).start();
		return null;
	}

	public static Bitmap requestTile(CharSequence lyrs, int x, int y, int z) throws Exception {
//		@SuppressLint("DefaultLocale") final String url = String.format(
////			"https://t2.tianditu.gov.cn/vec_w/wmts?tk=e2615b864327530e863275603fee58b3&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=%s&FORMAT=tiles&TILECOL=%d&TILEROW=%d&TILEMATRIX=%d",
//			"https://t2.tianditu.gov.cn/vec_w/wmts?tk=e2615b864327530e863275603fee58b3&TILEMATRIXSET=%s&TILECOL=%d&TILEROW=%d&TILEMATRIX=%d",
//			lyrs, x, y, z
//		);
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
		final String source = SettingActivity.Companion.getMapsSource().getSrcName();
		{
			final Bitmap bmp = DownloadCaching.Companion.readMapTile(source, x, y, z);
			if (bmp != null) {
				return bmp;
			}
		}

		final HttpsURLConnection conn = (HttpsURLConnection) new URL(
			SettingActivity.Companion.getMapsSource().getTileUrlStr(x, y, z)
		).openConnection();
		try {
			conn.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36 Edg/127.0.0.0");
			conn.connect();
			try (final InputStream is = conn.getInputStream()) {
				final Bitmap bmp = BitmapFactory.decodeStream(is);
				DownloadCaching.Companion.writeMapTile(source, x, y, z, bmp);
				return bmp;
			}
		} finally {
			conn.disconnect();
		}
	}
}
