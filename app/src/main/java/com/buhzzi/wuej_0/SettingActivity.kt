package com.buhzzi.wuej_0

import android.location.LocationManager
import android.os.Bundle
import android.text.format.Formatter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import com.buhzzi.wuej_0.kit.DownloadCaching
import com.buhzzi.wuej_0.kit.FontConstants
import com.buhzzi.wuej_0.kit.MetricsRelative
import com.buhzzi.wuej_0.kit.StackedActivity

class SettingActivity : StackedActivity() {
	companion object {
		var provider = LocationManager.FUSED_PROVIDER
			private set
		val providerNames = mapOf(
			LocationManager.NETWORK_PROVIDER to topActivity.getString(R.string.setting_provider_network),
			LocationManager.GPS_PROVIDER to topActivity.getString(R.string.setting_provider_gps),
			LocationManager.PASSIVE_PROVIDER to topActivity.getString(R.string.setting_provider_passive),
			LocationManager.FUSED_PROVIDER to topActivity.getString(R.string.setting_provider_fused)
		)
		var minTime = 4096L
			private set
		val minTimeNames = mapOf(
			0L to topActivity.getString(R.string.setting_min_time_0),
			4096L to topActivity.getString(R.string.setting_min_time_1),
			16384L to topActivity.getString(R.string.setting_min_time_2),
			65536L to topActivity.getString(R.string.setting_min_time_3)
		)
		var minDistance = 4F
			private set
		val minDistanceNames = mapOf(
			0F to topActivity.getString(R.string.setting_min_distance_0),
			4F to topActivity.getString(R.string.setting_min_distance_1),
			16F to topActivity.getString(R.string.setting_min_distance_2),
			64F to topActivity.getString(R.string.setting_min_distance_3)
		)
		var serverAddr = topActivity.getString(R.string.setting_server_addr_default)
			private set
		var downloadCaching = 0x10000000 // 256 MiB
			private set
	}
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.setting_activity)

		initProvider()
		initMinTime()
		initMinDistance()
		initServerAddr()
		initDownloadCaching()
	}
	private fun <ItemT> shadeButtons(shadedButtons: Map<ItemT, View>, selected: ItemT) = shadedButtons.forEach { (item, view) ->
		view.setBackgroundColor(getColor(if (item == selected)
			com.google.android.material.R.color.design_default_color_primary
		else
			com.google.android.material.R.color.design_default_color_primary_variant
		))
	}
	private fun initProvider() {
		val priorityLay = findViewById<LinearLayout>(R.id.providerLinearLayout)
		val px4dp = MetricsRelative.dpToPx(4F)
		lateinit var shadedButtons: Map<String, View>
		shadedButtons = providerNames.mapValues { (someProvider, someName) -> TextView(this).apply {
			layoutParams = LinearLayout.LayoutParams(
				0, ViewGroup.LayoutParams.WRAP_CONTENT, 1F
			).apply {
				setMargins(px4dp)
			}
			setPadding(px4dp)
			gravity = Gravity.CENTER
			text = someName
			setTextColor(getColor(com.google.android.material.R.color.design_default_color_on_primary))
			typeface = FontConstants.xeuTf
			textSize = 16F
			setOnClickListener {
				provider = someProvider
				shadeButtons(shadedButtons, someProvider)
			}
			priorityLay.addView(this)
		} }
		shadeButtons(shadedButtons, provider)
	}
	private fun initMinTime() {
		val minTimeLay = findViewById<LinearLayout>(R.id.minTimeLinearLayout)
		val px4dp = MetricsRelative.dpToPx(4F)
		lateinit var shadedButtons: Map<Long, View>
		shadedButtons = minTimeNames.mapValues { (someMinTime, someName) -> TextView(this).apply {
			layoutParams = LinearLayout.LayoutParams(
				0, ViewGroup.LayoutParams.WRAP_CONTENT, 1F
			).apply {
				setMargins(px4dp)
			}
			setPadding(px4dp)
			gravity = Gravity.CENTER
			text = someName
			setTextColor(getColor(com.google.android.material.R.color.design_default_color_on_primary))
			typeface = FontConstants.xeuTf
			textSize = 16F
			setOnClickListener {
				minTime = someMinTime
				shadeButtons(shadedButtons, someMinTime)
			}
			minTimeLay.addView(this)
		} }
		shadeButtons(shadedButtons, minTime)
	}
	private fun initMinDistance() {
		val minDistanceLay = findViewById<LinearLayout>(R.id.minDistanceLinearLayout)
		val px4dp = MetricsRelative.dpToPx(4F)
		lateinit var shadedButtons: Map<Float, View>
		shadedButtons = minDistanceNames.mapValues { (someMinDistance, someName) -> TextView(this).apply {
			layoutParams = LinearLayout.LayoutParams(
				0, ViewGroup.LayoutParams.WRAP_CONTENT, 1F
			).apply {
				setMargins(px4dp)
			}
			setPadding(px4dp)
			gravity = Gravity.CENTER
			text = someName
			setTextColor(getColor(com.google.android.material.R.color.design_default_color_on_primary))
			typeface = FontConstants.xeuTf
			textSize = 16F
			setOnClickListener {
				minDistance = someMinDistance
				shadeButtons(shadedButtons, someMinDistance)
			}
			minDistanceLay.addView(this)
		}}
		shadeButtons(shadedButtons, minDistance)
	}
	private fun initServerAddr() {
		findViewById<EditText>(R.id.serverAddrEditText).addTextChangedListener { text ->
			serverAddr = text.toString()
		}
	}
	private fun initDownloadCaching() {
		val downloadCachingTxt = findViewById<TextView>(R.id.downloadCachingTextView)
		val downloadCachingSeekBar = findViewById<SeekBar>(R.id.downloadCachingSeekBar)
		downloadCachingSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
				downloadCaching = progress
				downloadCachingTxt.text = Formatter.formatFileSize(this@SettingActivity, progress.toLong())
			}
			override fun onStartTrackingTouch(seekBar: SeekBar?) { }
			override fun onStopTrackingTouch(seekBar: SeekBar?) { }
		})
		findViewById<TextView>(R.id.downloadCachedTextView).text = getString(R.string.setting_download_cached, Formatter.formatFileSize(this, DownloadCaching.calcCachedSize()))
		findViewById<Button>(R.id.clearDownloadCachingButton).setOnClickListener {
			DownloadCaching.clearMapTiles()
			recreate()
		}
		findViewById<Button>(R.id.trimDownloadCachingButton).setOnClickListener {
			DownloadCaching.trimMapTiles()
			recreate()
		}
		downloadCachingSeekBar.progress = downloadCaching
		downloadCachingTxt.text = Formatter.formatFileSize(this, downloadCaching.toLong())
	}
}
