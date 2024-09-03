package com.buhzzi.wuej_0.kit

abstract class WuejMapsSource private constructor(val srcName: String) {
	companion object {
		// 天地圖
		val srcTianDihTuq = object : WuejMapsSource("tianditu-img_w-w") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://t${ (Math.random() * 8).toInt() }.tianditu.gov.cn", "/img_w/wmts",
					"SERVICE", "WMTS",
					"REQUEST", "GetTile",
					"VERSION", "1.0.0",
					"LAYER", "img",
					"STYLE", "DEFAULT",
					"TILEMATRIXSET", "w",
					"FORMAT", "tiles",
					"TILEMATRIX", z.toString(),
					"TILEROW", y.toString(),
					"TILECOL", x.toString(),
					"tk", "e2615b864327530e863275603fee58b3" // 來自北京
				).toString() }
		}
		// 吉林一號
		val srcGiqLinqI = object : WuejMapsSource("chrmingglobe-china2023_5_shield") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 20 }
				?.run { SocketRequestHelper.buildUrl(
					"https://tile.charmingglobe.com", "/tile/china2023_5_shield/tms/$z/$x/${1.shl(z) - (y + 1)}",
					"token", "Bearer%20a84a40c81f784490a4c5689187054abf"
				).toString() }
		}
		// 四維地球
		val srcSuihYweiDihKjou = object : WuejMapsSource("siweiearth-satellite") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://aoweiservice.siweiearth.com", "/map/new/satellite/$z/$y/$x"
				).toString() }
		}
		val srcArcGIS = object : WuejMapsSource("arcgisonline-World_Imagery") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { true }
				?.run { SocketRequestHelper.buildUrl(
					"https://server.arcgisonline.com", "/ArcGIS/rest/services/World_Imagery/MapServer/tile/$z/$y/$x"
				).toString() }
		}
		val srcYandex = object : WuejMapsSource("yandex-sat") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 18 }
				?.run { SocketRequestHelper.buildUrl(
					"https://core-sat.maps.yandex.net", "/tiles",
					"l", "sat",
					"x", x.toString(),
					"y", y.toString(),
					"z", z.toString()
				).toString() }
		}
		val srcBing = object : WuejMapsSource("bing-virtualearth") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 20 }
				?.run { SocketRequestHelper.buildUrl(
					"https://ecn.t${ (Math.random() * 8).toInt() }.tiles.virtualearth.net", "/tiles/a${ getQuadKey(x, y, z) }.jpeg",
					"g", "1"
				).toString() }
		}
		// 高德
		val srcAutoNaviStyle_7 = object : WuejMapsSource("autonavi-style_7") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://webrd0${ (Math.random() * 4).toInt() }.is.autonavi.com", "/appmaptile",
					"size", "1",
					"scale", "1",
					"style", "7",
					"x", x.toString(),
					"y", y.toString(),
					"z", z.toString()
				).toString() }
		}
		// 高德
		val srcAutoNaviStyle_8 = object : WuejMapsSource("autonavi-style_8") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://webrd0${ (Math.random() * 4).toInt() }.is.autonavi.com", "/appmaptile",
					"size", "1",
					"scale", "1",
					"style", "8",
					"x", x.toString(),
					"y", y.toString(),
					"z", z.toString()
				).toString() }
		}
		// 騰訊
		val srcTencent = object : WuejMapsSource("tencent-gtimg-realtimerender") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://rt${ (Math.random() * 4).toInt() }.map.gtimg.com", "/realtimerender",
					"z", z.toString(),
					"x", x.toString(),
					"y", y.toString(),
					"type", "vector"
				).toString() }
		}
		// 騰訊
		val srcTencentStyleid_2 = object : WuejMapsSource("tencent-gtimg-realtimerender") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://rt${ (Math.random() * 4).toInt() }.map.gtimg.com", "/realtimerender",
					"z", z.toString(),
					"x", x.toString(),
					"y", y.toString(),
					"type", "vector",
					"styleid", "2"
				).toString() }
		}
		// 騰訊
		val srcTencentStyleid_3 = object : WuejMapsSource("tencent-gtimg-realtimerender") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://rt${ (Math.random() * 4).toInt() }.map.gtimg.com", "/realtimerender",
					"z", z.toString(),
					"x", x.toString(),
					"y", y.toString(),
					"type", "vector",
					"styleid", "3"
				).toString() }
		}
		// 騰訊
		val srcTencentStyleid_4 = object : WuejMapsSource("tencent-gtimg-realtimerender") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://rt${ (Math.random() * 4).toInt() }.map.gtimg.com", "/realtimerender",
					"z", z.toString(),
					"x", x.toString(),
					"y", y.toString(),
					"type", "vector",
					"styleid", "4"
				).toString() }
		}
		// 騰訊
		val srcTencentStyleid_8 = object : WuejMapsSource("tencent-gtimg-realtimerender") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://rt${ (Math.random() * 4).toInt() }.map.gtimg.com", "/realtimerender",
					"z", z.toString(),
					"x", x.toString(),
					"y", y.toString(),
					"type", "vector",
					"styleid", "8"
				).toString() }
		}
		// 騰訊
		val srcTencentStyleid_9 = object : WuejMapsSource("tencent-gtimg-realtimerender") {
			override fun getTileUrlStr(x: Int, y: Int, z: Int) = takeIf { z < 19 }
				?.run { SocketRequestHelper.buildUrl(
					"https://rt${ (Math.random() * 4).toInt() }.map.gtimg.com", "/realtimerender",
					"z", z.toString(),
					"x", x.toString(),
					"y", (1.shl(z) - (y + 1)).toString(),
					"type", "vector",
					"styleid", "9"
				).toString() }
		}
		private fun getQuadKey(x: Int, y: Int, z: Int) = (z - 1).downTo(0).map {
			y.ushr(it).and(1).shl(1)
				.or(x.ushr(it).and(1)).
				digitToChar()
		}.joinToString("")
	}
	abstract fun getTileUrlStr(x: Int, y: Int, z: Int): String?
}
