package com.buhzzi.wuej_0.kit

class ByteRelative {
	companion object {
		fun shortToBytes(short: Short) = byteArrayOf(
			short.toByte(),
			(short.toInt() ushr 0x8).toByte(),
		)

		fun intToBytes(int: Int) = byteArrayOf(
			int.toByte(),
			(int ushr 0x8).toByte(),
			(int ushr 0x10).toByte(),
			(int ushr 0x18).toByte(),
		)

		fun longToBytes(long: Long) = byteArrayOf(
			long.toByte(),
			(long ushr 0x8).toByte(),
			(long ushr 0x10).toByte(),
			(long ushr 0x18).toByte(),
			(long ushr 0x20).toByte(),
			(long ushr 0x28).toByte(),
			(long ushr 0x30).toByte(),
			(long ushr 0x38).toByte(),
		)
	}
}
