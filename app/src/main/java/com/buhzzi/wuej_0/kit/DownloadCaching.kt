package com.buhzzi.wuej_0.kit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.buhzzi.wuej_0.SettingActivity
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

class DownloadCaching {
	companion object {
		private val downloadCachingDir
			get() = File(StackedActivity.topActivity.filesDir, "download_caching")

		private fun concatXyz(x: Int, y: Int, z: Int) = arrayOf(x, y, z).joinToString(".") { it.toString(16).padStart(8) }

		init {
			downloadCachingDir.mkdir()
		}

		fun calcCachedSize() = downloadCachingDir.listFiles { item -> item.isDirectory }
			?.asSequence()
			?.flatMap { it.listFiles { item -> item.isFile }?.asSequence() ?: emptySequence() }
			?.map { it.length() }
			?.sum()
			?: 0L

		fun writeMapTile(source: String, x: Int, y: Int, z: Int, bmp: Bitmap) = File(downloadCachingDir, source).runCatching {
			exists() || mkdir()
			File(this, concatXyz(x, y, z)).outputStream().use {
				bmp.compress(Bitmap.CompressFormat.PNG, 100, it)
			}
			trimMapTiles()
			true
		}.onFailure {
			it.printStackTrace()
		}.isSuccess

		fun readMapTile(source: String, x: Int, y: Int, z: Int) = File(downloadCachingDir, source).takeIf {
			it.exists()
		}?.run {
			File(this, concatXyz(x, y, z))
		}?.takeIf {
			it.exists()
		}?.run {
			inputStream().use {
				BitmapFactory.decodeStream(it)
			}
		}

		fun listFilesByAccessTime() = downloadCachingDir.listFiles { item -> item.isDirectory }
			?.asSequence()
			?.flatMap { it.listFiles { item -> item.isFile }?.asSequence() ?: emptySequence() }
			?.sortedBy { Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).lastAccessTime().toMillis() }
			?: emptySequence()

		fun clearMapTiles() = downloadCachingDir.listFiles()?.run {
			all(File::deleteRecursively)
		} ?: false

		fun trimMapTiles() {
			val exceedSize = calcCachedSize() - SettingActivity.downloadCaching
			if (exceedSize > 0) {
				var deletedSize = 0L
				for (file in listFilesByAccessTime()) {
					deletedSize += file.length()
					file.delete()
					deletedSize < exceedSize || break
				}
			}
		}
	}
}
