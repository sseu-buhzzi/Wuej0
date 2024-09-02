package com.buhzzi.wuej_0.kit

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class OrientationRelative {
	companion object {
		val orientation = FloatArray(3)
			get() = field.copyOf()
		val azimuth
			get() = orientation[0]
		val pitch
			get() = orientation[1]
		val roll
			get() = orientation[2]
		private val listener = object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				when (event?.sensor?.type) {
					Sensor.TYPE_ACCELEROMETER -> gravityArr = event.values
					Sensor.TYPE_MAGNETIC_FIELD -> geomagneticArr = event.values
				}
				if (gravityArr != null && geomagneticArr != null) {
					val rotation_mat = FloatArray(9)
					val inclination_mat = FloatArray(9)
					if (SensorManager.getRotationMatrix(rotation_mat, inclination_mat, gravityArr, geomagneticArr)) {
						SensorManager.getOrientation(rotation_mat, orientation)
					}
				}
			}
			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

		}
		private val sensorManager
			get() = StackedActivity.topActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
		private var gravityArr: FloatArray? = null
		private var geomagneticArr: FloatArray? = null
		fun startUpdatingOrientation() {
			val sm = sensorManager
			sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
				sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
			}
			sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
				sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
			}
		}
		fun stopUpdatingOrientation() {
			sensorManager.unregisterListener(listener)
		}
	}
}
