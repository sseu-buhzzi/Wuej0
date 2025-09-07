package com.buhzzi.wuej_0.kit

import android.annotation.SuppressLint
import kotlin.math.abs

class LocationConverter {
	companion object {
		fun positiveCoordinateToDms(coord: Double): String {
			var rest = coord
			val d = rest.toInt()
			rest = (rest - d) * 60
			val m = rest.toInt()
			rest = (rest - m) * 60
			val s = rest.toInt()
			return "$d°$m'$s\""
		}

		fun longitudeToDms(longi: Double): String {
			return positiveCoordinateToDms(abs(longi)) + when {
				longi > 0 -> 'E'
				longi < 0 -> 'W'
				else -> ""
			}
		}

		fun latitudeToDms(lati: Double): String {
			return positiveCoordinateToDms(abs(lati)) + when {
				lati > 0 -> 'N'
				lati < 0 -> 'S'
				else -> ""
			}
		}

		@SuppressLint("DefaultLocale")
		fun longitudeToFrac(longi: Double): String {
			return String.format("%.6f°", abs(longi)) + when {
				longi > 0 -> 'E'
				longi < 0 -> 'W'
				else -> ""
			}
		}

		@SuppressLint("DefaultLocale")
		fun latitudeToFrac(lati: Double): String {
			return String.format("%.6f°", abs(lati)) + when {
				lati > 0 -> 'N'
				lati < 0 -> 'S'
				else -> ""
			}
		}
	}
}
