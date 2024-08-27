package com.buhzzi.wuej_0.kit;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class request_helper {
	public static class result {
		public int code;
		public String msg;
		public Map<String, List<String>> fields;
		public byte[] body;
		public result(int code, String msg, Map<String, List<String>> fields, byte[] body) {
			this.code = code;
			this.msg = msg;
			this.fields = fields;
			this.body = body;
		}
	}
	public interface callback {
		void succ(result rsp_res);
		void fail(Exception e);
	}
	public interface json_callback {
		void response(int code, String msg, JSONObject data);
		default callback to_callback() {
			return new callback() {
				@Override public void succ(result rsp_res) {
					final JSONObject body_obj;
					try {
						body_obj = new JSONObject(new String(rsp_res.body));
					} catch (JSONException e) {
						e.printStackTrace();
						response(-1, null, null);
						return;
					}
					int body_code;
					try {
						body_code = body_obj.getInt("code");
					} catch (JSONException e) {
						e.printStackTrace();
						body_code = -1;
					}
					String body_msg;
					try {
						body_msg = body_obj.getString("msg");
					} catch (JSONException e) {
						e.printStackTrace();
						body_msg = null;
					}
					JSONObject data;
					try {
						data = body_obj.getJSONObject("data");
					} catch (JSONException e) {
						e.printStackTrace();
						data = null;
					}
					response(body_code, body_msg, data);
				}
				@Override public void fail(Exception e) {
					e.printStackTrace();
					response(-1, null, null);
				}
			};
		}
	}
	public static <conn_type extends HttpURLConnection> result request_sync(String method, String url, Map<String, String> fields, byte[] body) throws Exception {
		final byte[] rsp_body;
		final conn_type conn = (conn_type) new URL(url).openConnection();
		try {
			conn.setRequestMethod(method);
			conn.setDoOutput(body != null && body.length != 0);
			for (final String field : fields.keySet()) {
				conn.addRequestProperty(field, fields.get(field));
			}
			conn.connect();
			if (conn.getDoOutput()) {
				try (final OutputStream os = conn.getOutputStream()) {
					os.write(body);
				}
			}
			try (final InputStream is = conn.getInputStream()) {
				rsp_body = stream_helper.read_input_stream(is);
			}
		} finally {
			conn.disconnect();
		}
		conn.disconnect();
		return new result(
			conn.getResponseCode(),
			conn.getResponseMessage(),
			conn.getHeaderFields(),
			rsp_body
		);
	}
	public static <conn_type extends HttpURLConnection> void request(String method, String url, Map<String, String> fields, byte[] body, callback cb) {
		final Handler main_handler = new Handler();
		new Thread(() -> {
			final conn_type conn;
			try {
				conn = (conn_type) new URL(url).openConnection();
			} catch (Exception e) {
				main_handler.post(() -> cb.fail(e));
				return;
			}
			final byte[] rsp_body;
			try {
				conn.setRequestMethod(method);
				conn.setDoOutput(body != null && body.length != 0);
				for (final String field : fields.keySet()) {
					conn.addRequestProperty(field, fields.get(field));
				}
				conn.connect();
				if (conn.getDoOutput()) {
					try (final OutputStream os = conn.getOutputStream()) {
						os.write(body);
					} catch (Exception e) {
						conn.disconnect();
						main_handler.post(() -> cb.fail(e));
						return;
					}
				}
				try (final InputStream is = conn.getInputStream()) {
					rsp_body = stream_helper.read_input_stream(is);
				} catch (Exception e) {
					conn.disconnect();
					main_handler.post(() -> cb.fail(e));
					return;
				}
			} catch (Exception e) {
				conn.disconnect();
				main_handler.post(() -> cb.fail(e));
				return;
			}
			conn.disconnect();
			final int rsp_code;
			final String rsp_msg;
			try {
				rsp_code = conn.getResponseCode();
				rsp_msg = conn.getResponseMessage();
			} catch (Exception e) {
				main_handler.post(() -> cb.fail(e));
				return;
			}
			main_handler.post(() -> cb.succ(new result(rsp_code, rsp_msg, conn.getHeaderFields(), rsp_body)));
		}).start();
	}
}
