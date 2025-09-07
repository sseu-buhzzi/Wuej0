package com.buhzzi.wuej_0.kit

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import androidx.core.graphics.createBitmap

class WuejMapsDrawers {
	companion object {
		fun drawCntlCc(cntlVisibility: Int): Bitmap {
			val size = MetricsRelative.displayMetrics.widthPixels ushr 3
			val bmp = createBitmap(size, size)
			val cntlFolded = cntlVisibility != View.VISIBLE
			val cv = Canvas(bmp)
			cv.drawColor(if (cntlFolded) 0x40808080 else -0x3f7f7f80)
			val paint = Paint()
			paint.color = ColorRelative.colorFore
			if (!cntlFolded) {
				paint.style = Paint.Style.STROKE
				paint.strokeWidth = 4F
			}
			cv.drawCircle((size ushr 1).toFloat(), (size ushr 1).toFloat(), (size * 3 ushr 3).toFloat(), paint)
			return bmp
		}

		fun drawCntlArrow(): Bitmap {
			val size = MetricsRelative.displayMetrics.widthPixels ushr 3
			val bmp = createBitmap(size, size)
			val cv = Canvas(bmp)
			cv.drawColor(-0x7f7f7f80)
			val path = Path()
			path.moveTo((size ushr 2).toFloat(), (size ushr 2).toFloat())
			path.lineTo((size * 3 ushr 2).toFloat(), (size ushr 1).toFloat())
			path.lineTo((size ushr 2).toFloat(), (size * 3 ushr 2).toFloat())
			val paint = Paint()
			paint.color = ColorRelative.colorFore
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = 4F
			paint.strokeCap = Paint.Cap.ROUND
			cv.drawPath(path, paint)
			return bmp
		}

		fun drawCntlRota(): Bitmap {
			val size = MetricsRelative.displayMetrics.widthPixels ushr 3
			val bmp = createBitmap(size, size)
			val cv = Canvas(bmp)
			cv.drawColor(0x40808080)
			val path = Path()
			path.arcTo((size ushr 4).toFloat(), size * -1.9375F, size * 2.9375F, size * 0.9375F, 150F, -30F, true)
			path.lineTo((size ushr 2).toFloat(), (size * 3 ushr 2).toFloat())
			val paint = Paint()
			paint.color = ColorRelative.colorFore
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = 4F
			paint.strokeCap = Paint.Cap.ROUND
			cv.drawPath(path, paint)
			return bmp
		}

		fun drawCntlZoomIn(): Bitmap {
			val size = MetricsRelative.displayMetrics.widthPixels ushr 3
			val bmp = createBitmap(size, size)
			val cv = Canvas(bmp)
			cv.drawColor(0x40808080)
			val path = Path()
			path.moveTo((size ushr 1).toFloat(), (size ushr 2).toFloat())
			path.lineTo((size ushr 1).toFloat(), (size * 3 ushr 2).toFloat())
			path.moveTo((size ushr 2).toFloat(), (size ushr 1).toFloat())
			path.lineTo((size * 3 ushr 2).toFloat(), (size ushr 1).toFloat())
			val paint = Paint()
			paint.color = ColorRelative.colorFore
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = 4F
			paint.strokeCap = Paint.Cap.ROUND
			cv.drawPath(path, paint)
			return bmp
		}

		fun drawCntlZoomOut(): Bitmap {
			val size = MetricsRelative.displayMetrics.widthPixels ushr 3
			val bmp = createBitmap(size, size)
			val cv = Canvas(bmp)
			cv.drawColor(0x40808080)
			val path = Path()
			path.moveTo((size ushr 2).toFloat(), (size ushr 1).toFloat())
			path.lineTo((size * 3 ushr 2).toFloat(), (size ushr 1).toFloat())
			val paint = Paint()
			paint.color = ColorRelative.colorFore
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = 4F
			paint.strokeCap = Paint.Cap.ROUND
			cv.drawPath(path, paint)
			return bmp
		}

		fun drawCentralCursor(): Bitmap {
			val size = MetricsRelative.displayMetrics.widthPixels ushr 4
			val unit = (size ushr 3).toFloat()
			val bmp = createBitmap(size, size)
			val cv = Canvas(bmp)
			cv.drawColor(0x00000000)
			val path = Path()
			path.moveTo(unit * 4, 0F)
			path.lineTo(unit * 4, unit * 3)
			path.moveTo(unit * 4, 0F)
			path.lineTo(unit * 1, unit * 7)
			path.lineTo(unit * 3, unit * 5)
			path.moveTo(unit * 4, 0F)
			path.lineTo(unit * 7, unit * 7)
			path.lineTo(unit * 5, unit * 5)
			val paint = Paint()
			paint.color = -0x10000
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = 4F
			paint.strokeCap = Paint.Cap.ROUND
			cv.drawPath(path, paint)
			return bmp
		}
	}
}