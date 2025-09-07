package com.buhzzi.wuej_0.kit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.buhzzi.wuej_0.SettingActivity
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteRecursively
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.notExists
import kotlin.io.path.outputStream
import kotlin.io.path.readAttributes

class DownloadCaching {
	companion object {
		private val downloadCachingDirPath
			get() = StackedActivity.topActivity.filesDir.toPath() / "download_caching"

		private fun concatXyz(x: Int, y: Int, z: Int) = arrayOf(x, y, z).joinToString(".") { it.toString(16).padStart(8) }

		init {
			downloadCachingDirPath.createDirectory()
		}

		fun calcCachedSize() = downloadCachingDirPath.listDirectoryEntries()
			.asSequence()
			.filter { it.isDirectory() }
			.flatMap { it.listDirectoryEntries().asSequence().filter { item -> item.isRegularFile() } }
			.map { it.fileSize() }
			.sum()

		fun writeMapTile(source: String, x: Int, y: Int, z: Int, bmp: Bitmap) = (downloadCachingDirPath / source).runCatching {
			if (notExists()) {
				createDirectory()
			}
			(this / concatXyz(x, y, z)).outputStream().use {
				bmp.compress(Bitmap.CompressFormat.PNG, 100, it)
			}
			trimMapTiles()
			true
		}.onFailure {
			it.printStackTrace()
		}.isSuccess

		fun readMapTile(source: String, x: Int, y: Int, z: Int) = (downloadCachingDirPath / source).takeIf {
			it.exists()
		}?.run {
			this / concatXyz(x, y, z)
		}?.takeIf {
			it.exists()
		}?.run {
			inputStream().use {
				BitmapFactory.decodeStream(it)
			}
		}

		fun listFilesByAccessTime() = downloadCachingDirPath.listDirectoryEntries()
			.asSequence()
			.filter { it.isDirectory() }
			.flatMap { it.listDirectoryEntries().asSequence().filter { item -> item.isRegularFile() } }
			.sortedBy { it.readAttributes<BasicFileAttributes>().lastAccessTime().toMillis() }

		fun clearMapTiles() =
			@OptIn(ExperimentalPathApi::class)
			downloadCachingDirPath.deleteRecursively()

		fun trimMapTiles() {
			val exceedSize = calcCachedSize() - SettingActivity.downloadCaching
			if (exceedSize > 0) {
				var deletedSize = 0L
				for (file in listFilesByAccessTime()) {
					deletedSize += file.fileSize()
					file.deleteExisting()
					deletedSize < exceedSize || break
				}
			}
		}
	}
}
