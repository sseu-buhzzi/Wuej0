package com.buhzzi.wuej_0.kit


import java.net.Socket
import java.net.URL

class SocketRequestHelper {
	companion object {
		fun buildHttpMessage(method: String, path: String, fields: List<Array<String>>) =
			"$method $path HTTP/1.1\r\n${fields.joinToString { pair -> "${pair[0]}: ${pair[1]}\r\n" }}\r\n"

		fun buildUrl(address: String, path: String, vararg queries: String): StringBuilder = StringBuilder(address)
			.append(path)
			.run {
				if (queries.size >= 2) {
					append('?')
					append(queries[0])
					append('=')
					append(queries[1])
					(2 .. queries.size - 2 step 2).forEach {
						append('&')
						append(queries[it])
						append('=')
						append(queries[it + 1])
					}
				}
				this
			}

		fun request(method: String, url: String, fields: List<Array<String>>, body: ByteArray, cb: RequestHelper.callback) {
			Thread {
				val urlUrl = URL(url)
				val rspMessageData: ByteArray
				val code: Int
				val msg: String
				val rspFields: List<Array<String>>
				val rspBody: ByteArray
				try {
					Socket(urlUrl.host, urlUrl.port.takeIf { it != -1 } ?: urlUrl.defaultPort).use { clientSock ->
						clientSock.getOutputStream().use { clientOs ->
							clientSock.getInputStream().use { clientIs ->
								buildHttpMessage(method, urlUrl.path, fields).run {
									clientOs.write(toByteArray())
								}
								clientOs.write(body)
								rspMessageData = StreamHelper.readInputStream(clientIs)
							}
						}
					}
					val bodyBegin = "\r\n\r\n".toByteArray().run {
						rspMessageData.indices.first { i ->
							i + 4 < rspMessageData.size && rspMessageData.copyOfRange(i, i + 4) contentEquals this
						}
					} + 4
					val rspLines = rspMessageData.sliceArray(0 until bodyBegin).decodeToString().split("\r\n")
					rspLines[0].split(" ").run {
						code = this[1].toInt()
						msg = this[2]
					}
					rspFields = rspLines.drop(1).mapNotNull { line ->
						line.split(':', limit = 2).takeIf { it.size == 2 }?.toTypedArray()
					}
					rspBody = rspMessageData.copyOfRange(bodyBegin, rspMessageData.size)
				} catch (e: Exception) {
					e.printStackTrace()
					cb.fail(e)
					return@Thread
				}
				cb.succ(RequestHelper.result(code, msg, rspFields, rspBody))
			}.start()
		}
	}
}
