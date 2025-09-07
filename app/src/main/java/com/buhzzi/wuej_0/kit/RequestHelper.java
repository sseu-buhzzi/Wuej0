package com.buhzzi.wuej_0.kit;

import android.os.Handler;
import android.os.Looper;

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

	public interface json_callback {
		void response(int code, String msg, JSONObject data);

		default callback toCallback() {
			return new callback() {
				@Override
				public void succ(result rspRes) {
					final JSONObject bodyObj;
					try {
						bodyObj = new JSONObject(new String(rspRes.body));
					} catch (JSONException e) {
						e.printStackTrace();
						response(-1, null, null);
						return;
					}
					int body_code;
					try {
						body_code = bodyObj.getInt("code");
					} catch (JSONException e) {
						e.printStackTrace();
						body_code = -1;
					}
					String body_msg;
					try {
						body_msg = bodyObj.getString("msg");
					} catch (JSONException e) {
						e.printStackTrace();
						body_msg = null;
					}
					JSONObject data;
					try {
						data = bodyObj.getJSONObject("data");
					} catch (JSONException e) {
						e.printStackTrace();
						data = null;
					}
					response(body_code, body_msg, data);
				}

				@Override
				public void fail(Exception e) {
					e.printStackTrace();
					response(-1, null, null);
				}
			};
		}
	}

	public static <ConnType extends HttpURLConnection> result requestSync(String method, String url, List<String[]> fields, byte[] body) throws Exception {
		final byte[] rsp_body;
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
				rsp_body = StreamHelper.readInputStream(is);
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
			rsp_body
		);
	}

	public static <conn_type extends HttpURLConnection> void request(String method, String url, List<String[]> fields, byte[] body, callback cb) {
		new Thread(() -> {
			final conn_type conn;
			try {
				conn = (conn_type) new URL(url).openConnection();
			} catch (Exception e) {
				new Handler(Looper.getMainLooper()).post(() -> cb.fail(e));
				return;
			}
			final byte[] rsp_body;
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
					rsp_body = StreamHelper.readInputStream(is);
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
			final int rsp_code;
			final String rsp_msg;
			try {
				rsp_code = conn.getResponseCode();
				rsp_msg = conn.getResponseMessage();
			} catch (Exception e) {
				new Handler(Looper.getMainLooper()).post(() -> cb.fail(e));
				return;
			}
			new Handler(Looper.getMainLooper()).post(() -> cb.succ(new result(
				rsp_code,
				rsp_msg,
				conn.getHeaderFields().entrySet().stream().flatMap(entry -> {
					final String key = entry.getKey();
					return entry.getValue().stream().map(value -> new String[]{key, value});
				}).collect(Collectors.toList()),
				rsp_body
			)));
		}).start();
	}
}
