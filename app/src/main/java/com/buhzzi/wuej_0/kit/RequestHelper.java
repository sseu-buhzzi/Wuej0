package com.buhzzi.wuej_0.kit;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class RequestHelper {
	public static class result {
		public int code;
		public String msg;
		public List<String[]> fields;
		public byte[] body;

		public result(int code, String msg, List<String[]> fields, byte[] body) {
			this.code = code;
			this.msg = msg;
			this.fields = fields;
			this.body = body;
		}
	}

	public interface callback {
		void succ(result rspRes);

		void fail(Exception e);
	}

	public interface JsonCallback {
		void response(int code, String msg, JSONObject data);

		default callback toCallback() {
			return new callback() {
				@Override
				public void succ(result rspRes) {
					final JSONObject bodyObj;
					try {
						bodyObj = new JSONObject(new String(rspRes.body));
					} catch (JSONException e) {
						Log.e("RequestHelper", "rspRes.body", e);
						response(-1, null, null);
						return;
					}
					int bodyCode;
					try {
						bodyCode = bodyObj.getInt("code");
					} catch (JSONException e) {
						Log.e("RequestHelper", "bodyObj.getInt(\"code\")", e);
						bodyCode = -1;
					}
					String bodyMsg;
					try {
						bodyMsg = bodyObj.getString("msg");
					} catch (JSONException e) {
						Log.e("RequestHelper", "bodyObj.getString(\"msg\")", e);
						bodyMsg = null;
					}
					JSONObject data;
					try {
						data = bodyObj.getJSONObject("data");
					} catch (JSONException e) {
						Log.e("RequestHelper", "bodyObj.getJSONObject(\"data\")", e);
						data = null;
					}
					response(bodyCode, bodyMsg, data);
				}

				@Override
				public void fail(Exception e) {
					Log.e("RequestHelper", "fail", e);
					response(-1, null, null);
				}
			};
		}
	}

	public static <ConnType extends HttpURLConnection> result requestSync(String method, String url, List<String[]> fields, byte[] body) throws Exception {
		final byte[] rspBody;
		final ConnType conn = (ConnType) new URL(url).openConnection();
		try {
			conn.setRequestMethod(method);
			conn.setDoOutput(body != null && body.length != 0);
			for (final String[] field : fields) {
				conn.addRequestProperty(field[0], field[1]);
			}
			conn.connect();
			if (conn.getDoOutput()) {
				try (final OutputStream os = conn.getOutputStream()) {
					os.write(body);
				}
			}
			try (final InputStream is = conn.getInputStream()) {
				rspBody = StreamHelper.readInputStream(is);
			}
		} finally {
			conn.disconnect();
		}
		conn.disconnect();
		return new result(
			conn.getResponseCode(),
			conn.getResponseMessage(),
			conn.getHeaderFields().entrySet().stream().flatMap(entry -> {
				final String key = entry.getKey();
				return entry.getValue().stream().map(value -> new String[]{key, value});
			}).collect(Collectors.toList()),
			rspBody
		);
	}

	public static <ConnType extends HttpURLConnection> void request(String method, String url, List<String[]> fields, byte[] body, callback cb) {
		new Thread(() -> {
			final ConnType conn;
			try {
				conn = (ConnType) new URL(url).openConnection();
			} catch (Exception e) {
				new Handler(Looper.getMainLooper()).post(() -> cb.fail(e));
				return;
			}
			final byte[] rspBody;
			try {
				conn.setRequestMethod(method);
				conn.setDoOutput(body != null && body.length != 0);
				for (final String[] field : fields) {
					conn.addRequestProperty(field[0], field[1]);
				}
				conn.connect();
				if (conn.getDoOutput()) {
					try (final OutputStream os = conn.getOutputStream()) {
						os.write(body);
					} catch (Exception e) {
						conn.disconnect();
						new Handler(Looper.getMainLooper()).post(() -> cb.fail(e));
						return;
					}
				}
				try (final InputStream is = conn.getInputStream()) {
					rspBody = StreamHelper.readInputStream(is);
				} catch (Exception e) {
					conn.disconnect();
					new Handler(Looper.getMainLooper()).post(() -> cb.fail(e));
					return;
				}
			} catch (Exception e) {
				conn.disconnect();
				new Handler(Looper.getMainLooper()).post(() -> cb.fail(e));
				return;
			}
			conn.disconnect();
			final int rspCode;
			final String rspMsg;
			try {
				rspCode = conn.getResponseCode();
				rspMsg = conn.getResponseMessage();
			} catch (Exception e) {
				new Handler(Looper.getMainLooper()).post(() -> cb.fail(e));
				return;
			}
			new Handler(Looper.getMainLooper()).post(() -> cb.succ(new result(
				rspCode,
				rspMsg,
				conn.getHeaderFields().entrySet().stream().flatMap(entry -> {
					final String key = entry.getKey();
					return entry.getValue().stream().map(value -> new String[]{key, value});
				}).collect(Collectors.toList()),
				rspBody
			)));
		}).start();
	}
}
