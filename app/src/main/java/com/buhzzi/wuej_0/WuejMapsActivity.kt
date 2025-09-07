package com.buhzzi.wuej_0

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.buhzzi.wuej_0.kit.ByteRelative
import com.buhzzi.wuej_0.kit.FontConstants
import com.buhzzi.wuej_0.kit.LocationConverter
import com.buhzzi.wuej_0.kit.LocationRelative
import com.buhzzi.wuej_0.kit.OrientationRelative
import com.buhzzi.wuej_0.kit.RequestHelper
import com.buhzzi.wuej_0.kit.RsaRelative
import com.buhzzi.wuej_0.kit.ServerRelative
import com.buhzzi.wuej_0.kit.SocketRequestHelper
import com.buhzzi.wuej_0.kit.StackedActivity
import com.buhzzi.wuej_0.kit.StreamHelper
import com.buhzzi.wuej_0.kit.WuejMapsDrawers
import com.buhzzi.wuej_0.kit.WuejMapsView
import org.json.JSONObject
import java.util.Base64
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.math.max

class WuejMapsActivity : StackedActivity() {
	private var firstTime = true
	private lateinit var mapsView: WuejMapsView
	private var cntlVisibility = View.VISIBLE
	private lateinit var cntlCcImg: ImageView
	private lateinit var cntlArrowRightImg: ImageView
	private lateinit var cntlArrowUpImg: ImageView
	private lateinit var cntlArrowLeftImg: ImageView
	private lateinit var cntlArrowDownImg: ImageView
	private lateinit var cntlRotaRcwImg: ImageView
	private lateinit var cntlRotaCwImg: ImageView
	private lateinit var cntlZoomInImg: ImageView
	private lateinit var cntlZoomOutImg: ImageView
	private lateinit var centralCursorImg: ImageView
	private lateinit var infoLay: LinearLayout
	private lateinit var longiTxt: TextView
	private lateinit var latiTxt: TextView
	private val locatingHandler = Handler(Looper.getMainLooper())
	private lateinit var locatingRunnable: Runnable

	private lateinit var xuh: String
	private lateinit var lorif: ByteArray
	private lateinit var locup: ByteArray
	private lateinit var chaschig: Array<Array<String>>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if (ensureLocatingPermission()) {
			initWithLocatingPermission()
		}
	}

	override fun onDestroy() {
		super.onDestroy()

		if (::locatingRunnable.isInitialized) {
			locatingHandler.removeCallbacks(locatingRunnable)
		}

		LocationRelative.stopUpdatingLocation()
		OrientationRelative.stopUpdatingOrientation()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String?>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		if (requestCode == REQUEST_LOCATING_PERMISSION_CODE) {
			if (
				grantResults.isNotEmpty() &&
				grantResults[0] == PackageManager.PERMISSION_GRANTED
			) {
				initWithLocatingPermission()
			}
		}
	}

	private fun initWithLocatingPermission() {
		setContentView(R.layout.wuej_maps_activity)
		initMapsView()
		initXuh()
		initCntlViews()
		initInfo()

		// You should turn off background battery saver to continuously call this lambda.
		locatingRunnable = Runnable {
			locatingHandler.postDelayed(locatingRunnable, 512)
			LocationRelative.location?.let {
				if (firstTime) {
					firstTime = false
					locateMyself()
				}
				mapsView.loadInScreenMaps()
				longiTxt.text = LocationConverter.longitudeToFrac(it.longitude)
				latiTxt.text = LocationConverter.latitudeToFrac(it.latitude)

				if (::lorif.isInitialized && ::locup.isInitialized) {
					setAndGetLocations(it)
				}
			} ?: run {
				longiTxt.text = getString(R.string.maps_unknown_longitude)
				latiTxt.text = getString(R.string.maps_unknown_latitude)
			}
			centralCursorImg.rotation = OrientationRelative.azimuth
		}
		locatingHandler.post(locatingRunnable)

		LocationRelative.startUpdatingLocation()
		OrientationRelative.startUpdatingOrientation()
	}

	private fun initMapsView() {
		mapsView = findViewById(R.id.mapsView)
	}

	private fun getCntlOnTouch(dPixInOne: Int, dXInOne: Int, dYInOne: Int, dRotation: Float): (View, MotionEvent) -> Boolean {
		return { cntlOnTouchView, cntlOnTouchEvent ->
			cntlOnTouchView.performClick()
			when (cntlOnTouchEvent.action) {
				MotionEvent.ACTION_DOWN -> {
					cntlOnTouchView.alpha = 0.5F
					mapsView.dPixInOne = dPixInOne
					mapsView.dXInOne = dXInOne
					mapsView.dYInOne = dYInOne
					mapsView.dRotation = dRotation
					true
				}

				MotionEvent.ACTION_UP -> {
					cntlOnTouchView.alpha = 1F
					mapsView.dPixInOne = 0
					mapsView.dXInOne = 0
					mapsView.dYInOne = 0
					mapsView.dRotation = 0F
					true
				}

				else -> false
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun initCntlViews() {
		cntlCcImg = findViewById(R.id.cntlCcImageView)
		cntlCcImg.setImageBitmap(WuejMapsDrawers.drawCntlCc(cntlVisibility))
		cntlCcImg.setOnClickListener {
			mapsView.loadInScreenMaps()
			cntlVisibility = if (cntlVisibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
			cntlCcImg.setImageBitmap(WuejMapsDrawers.drawCntlCc(cntlVisibility))
			cntlArrowRightImg.visibility = cntlVisibility
			cntlArrowUpImg.visibility = cntlVisibility
			cntlArrowLeftImg.visibility = cntlVisibility
			cntlArrowDownImg.visibility = cntlVisibility
			cntlRotaRcwImg.visibility = cntlVisibility
			cntlRotaCwImg.visibility = cntlVisibility
			cntlZoomInImg.visibility = cntlVisibility
			cntlZoomOutImg.visibility = cntlVisibility
			centralCursorImg.visibility = cntlVisibility
			infoLay.visibility = cntlVisibility
		}
		cntlCcImg.setOnLongClickListener {
			locateMyself()
			true
		}
		cntlArrowRightImg = findViewById(R.id.cntlArrowRightImageView)
		cntlArrowRightImg.setImageBitmap(WuejMapsDrawers.drawCntlArrow())
		cntlArrowRightImg.setOnTouchListener(getCntlOnTouch(0, 1, 0, 0f))

		cntlArrowUpImg = findViewById(R.id.cntlArrowUpImageView)
		cntlArrowUpImg.setImageBitmap(WuejMapsDrawers.drawCntlArrow())
		cntlArrowUpImg.setOnTouchListener(getCntlOnTouch(0, 0, -1, 0f))

		cntlArrowLeftImg = findViewById(R.id.cntlArrowLeftImageView)
		cntlArrowLeftImg.setImageBitmap(WuejMapsDrawers.drawCntlArrow())
		cntlArrowLeftImg.setOnTouchListener(getCntlOnTouch(0, -1, 0, 0f))

		cntlArrowDownImg = findViewById(R.id.cntlArrowDownImageView)
		cntlArrowDownImg.setImageBitmap(WuejMapsDrawers.drawCntlArrow())
		cntlArrowDownImg.setOnTouchListener(getCntlOnTouch(0, 0, 1, 0f))

		cntlRotaRcwImg = findViewById(R.id.cntlRotaRcwImageView)
		cntlRotaRcwImg.setImageBitmap(WuejMapsDrawers.drawCntlRota())
		cntlRotaRcwImg.setOnTouchListener(getCntlOnTouch(0, 0, 0, 1f))

		cntlRotaCwImg = findViewById(R.id.cntlRotaCwImageView)
		cntlRotaCwImg.setImageBitmap(WuejMapsDrawers.drawCntlRota())
		cntlRotaCwImg.setOnTouchListener(getCntlOnTouch(0, 0, 0, -1f))

		cntlZoomInImg = findViewById(R.id.cntlZoomInImageView)
		cntlZoomInImg.setImageBitmap(WuejMapsDrawers.drawCntlZoomIn())
		cntlZoomInImg.setOnTouchListener(getCntlOnTouch(1, 0, 0, 0f))

		cntlZoomOutImg = findViewById(R.id.cntlZoomOutImageView)
		cntlZoomOutImg.setImageBitmap(WuejMapsDrawers.drawCntlZoomOut())
		cntlZoomOutImg.setOnTouchListener(getCntlOnTouch(-1, 0, 0, 0f))

		centralCursorImg = findViewById(R.id.centralCursorImageView)
		centralCursorImg.setImageBitmap(WuejMapsDrawers.drawCentralCursor())
	}

	private fun initInfo() {
		infoLay = findViewById(R.id.infoLinearLayout)
		val infoTxtList = listOf(
			getString(R.string.maps_unknown_longitude),
			getString(R.string.maps_unknown_latitude),
			SettingActivity.providerNames[SettingActivity.provider],
			SettingActivity.minTimeNames[SettingActivity.minTime],
			SettingActivity.minDistanceNames[SettingActivity.minDistance],
		).map { name ->
			TextView(this).apply {
				layoutParams = LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
				)
				setBackgroundColor(0x80000000.toInt())
				text = name
				typeface = FontConstants.xeuTf
				setTextColor(0xffffffff.toInt())
				textSize = 16F
				infoLay.addView(this)
			}
		}
		longiTxt = infoTxtList[0]
		latiTxt = infoTxtList[1]
	}

	private fun initXuh() {
		(filesDir.toPath() / "wuej_xuh")
			.takeIf { it.exists() }
			?.readBytes()
			?.run {
				val nmLn = (this[0].toUInt() or (this[1].toUInt() shl 8)).toInt()
				xuh = decodeToString(2, nmLn + 2)
				val otLn = this[nmLn + 2].toUByte().toInt() + 1024
				lorif = copyOfRange(nmLn + 3, otLn + nmLn + 3)
				locup = copyOfRange(otLn + nmLn + 3, size)
			}
		chaschig = run {
			(filesDir.toPath() / "chaschig")
				.takeIf { it.exists() }
				?.readBytes()
				?: "{}".encodeToByteArray()
		}.run { // hashes
			JSONObject(decodeToString())
		}.run { keys().asSequence().map { k -> arrayOf(k, getString(k)) }.toList().toTypedArray() }
	}

	private fun setAndGetLocations(location: Location) = (arrayOf(
		WuejMapsView.longitudeInOne(location.longitude).toUInt(),
		WuejMapsView.latitudeInOne(location.latitude).toUInt(),
	) + OrientationRelative.orientation.map { f -> f.toRawBits().toUInt() })
		.joinToString(",")
		.toByteArray()
		.run { RsaRelative.encrypt(locup, this) }
		.run {
			Base64.getEncoder().encodeToString(this)
				.apply { print(this) }
		}
		.run {
			mapOf(
				"task" to "location_240830_set_and_get",
				"name" to chaschig.first { p -> p[1] == xuh }[0],
				"loca" to this,
			)
		}
		.run { JSONObject(this) }
		.toString()
		.toByteArray()
		.run {
			RsaRelative.encrypt(
				resources.openRawResource(R.raw._240828)
					.run { StreamHelper.readInputStream(this) },
				this,
			)
		}
		.run {
			ServerRelative.magic +
				ByteRelative.intToBytes(this.size + 16) +
				ServerRelative.jsonMagic +
				this
		}
		.run {
			SocketRequestHelper.request(
				String(ServerRelative.getTshr16()),
				SettingActivity.serverAddr + "/",
				emptyList(),
				this,
				(RequestHelper.json_callback { code, _, body ->
					runOnUiThread {
						code == 200 || return@runOnUiThread
						mapsView.contactNames = body.keys().asSequence().map { k ->
							chaschig.first { m -> m[0] == k }[1]
						}.toList().toTypedArray()
						mapsView.contactLocaArr = body.keys().asSequence().map { chasch ->
							val (xInOne, yInOne) = body.getString(chasch)
								.run { Base64.getDecoder().decode(this) }
								.run { RsaRelative.decrypt(lorif, this) }
								.decodeToString()
								.split(',')
							((xInOne.toULong() shl 32) or yInOne.toULong()).toLong()
						}.toList().toTypedArray().toLongArray()
					}
				}).toCallback(),
			)
		}

	private fun ensureLocatingPermission(): Boolean {
		if (
			ActivityCompat.checkSelfPermission(
				this, Manifest.permission.ACCESS_FINE_LOCATION,
			) == PackageManager.PERMISSION_GRANTED
		) {
			return true
		}
		ActivityCompat.requestPermissions(
			this,
			arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
			REQUEST_LOCATING_PERMISSION_CODE,
		)
		return false
	}

	private fun locateMyself() = LocationRelative.location?.let {
		mapsView.setCameraLocation(it)
		mapsView.pixInOne = max(mapsView.pixInOne, 0x20000.toDouble())
	} ?: run {
		Toast.makeText(this, R.string.maps_location_unavailable, Toast.LENGTH_SHORT).show()
	}

	companion object {
		private const val REQUEST_LOCATING_PERMISSION_CODE = 0x24022116
	}
}
