package com.buhzzi.wuej_0.kit

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
			return positiveCoordinateToDms(abs(longi)) + if (longi > 0)
				'E'
			else if (longi < 0)
				'W'
			else
				""
		}
		fun latitudeToDms(lati: Double): String {
			return positiveCoordinateToDms(abs(lati)) + if (lati > 0)
				'N'
			else if (lati < 0)
				'S'
			else
				""
		}
		fun longitudeToFrac(longi: Double): String {
			return String.format("%.6f°", abs(longi)) + if (longi > 0)
				'E'
			else if (longi < 0)
				'W'
			else
				""
		}
		fun latitudeToFrac(lati: Double): String {
			return String.format("%.6f°", abs(lati)) + if (lati > 0)
				'N'
			else if (lati < 0)
				'S'
			else
				""
		}
	}
}
