package com.buhzzi.wuej_0

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class UpdateActivity : AppCompatActivity() {
	private lateinit var downloadPickerLauncher: ActivityResultLauncher<String>
	private lateinit var downloadButton: Button
	private lateinit var downloadAddr: String
	private lateinit var installPickerLauncher: ActivityResultLauncher<Array<String>>
	private lateinit var installButton: Button
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.update_activity)
		initDownload()
		initInstall()
	}
	private fun initDownload() {
		downloadPickerLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
			Thread {
				try {
					uri ?: throw Exception("Failed opening destination file")
					val conn: HttpURLConnection = URL(downloadAddr).openConnection() as HttpURLConnection
					try {
						conn.connect()
						conn.inputStream.use { remoteIs ->
							contentResolver.openOutputStream(uri).use { localOs ->
								localOs ?: throw Exception("Failed opening destination file")
								var b: Int
								while (remoteIs.read().also { b = it } != -1)
									localOs.write(b)
								localOs.flush()
							}
						}
					} finally {
						conn.disconnect()
					}
				} catch (e: Exception) {
					runOnUiThread {
						Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
					}
				}
				runOnUiThread {
					downloadButton.isEnabled = true
				}
			}.start()
		}
		downloadButton = findViewById(R.id.downloadButton)
		downloadButton.setOnClickListener {
			downloadAddr = findViewById<EditText>(R.id.downloadAddrEditText).text.toString()
			try {
				val downloadName = Uri.parse(downloadAddr).lastPathSegment ?: throw Exception("Invalid download address")
				downloadPickerLauncher.launch(downloadName)
				downloadButton.isEnabled = false
			} catch (e: Exception) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
			}
		}
	}
	private fun initInstall() {
		installPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
			try {
				uri ?: throw Exception("Failed opening APK file")
				startActivity(Intent(Intent.ACTION_VIEW).apply {
					setDataAndType(uri, "application/vnd.android.package-archive")
					addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION.or(Intent.FLAG_ACTIVITY_NEW_TASK))
				})
			} catch (e: Exception) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
			}
			installButton.isEnabled = true
		}
		installButton = findViewById(R.id.installButton)
		installButton.setOnClickListener {
			try {
				installPickerLauncher.launch(arrayOf("application/vnd.android.package-archive"))
				installButton.isEnabled = false
			} catch (e: Exception) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
			}
		}
	}
}
