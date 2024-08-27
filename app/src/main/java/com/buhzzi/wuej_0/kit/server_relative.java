package com.buhzzi.wuej_0.kit;

import android.content.Context;
import android.content.res.Resources;

import com.buhzzi.wuej_0.R;

public class server_relative {
	public static Resources res;
	public static void init(Context ctx) {
		res = ctx.getResources();
	}
    public static java.lang.Object read_240126() throws java.lang.Exception {
        final android.content.res.AssetFileDescriptor _240126_fd = res.openRawResourceFd(R.raw._240130);
        final byte[] _240126_data = new byte[(int) _240126_fd.getLength()];
        final java.io.FileInputStream _240126_fis = _240126_fd.createInputStream();
        assert _240126_fis.read(_240126_data) == _240126_data.length;
        _240126_fis.close();
        return java.security.KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(_240126_data));
    }
    public static byte[] request_wrap(final long req_code, final byte[] reqbdy_data) {
        final byte[] req_data = new byte[reqbdy_data.length + 12];
        final java.nio.ByteBuffer req_buf = java.nio.ByteBuffer.allocate(12);
        req_buf.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        req_buf.putLong(req_code ^ 0x374447374d4245ffL);
        req_buf.putInt(req_data.length);
        java.lang.System.arraycopy(req_buf.array(), 0, req_data, 0, 12);
        java.lang.System.arraycopy(reqbdy_data, 0, req_data, 12, reqbdy_data.length);
        return req_data;
    }
    public static void fetch(final long req_code, final byte[] reqbdy_data, final java.util.function.Consumer<byte[]> rsp_callback) {
        new java.lang.Thread(() -> {
            try (
                final java.net.Socket server_sock = new java.net.Socket("buhzzi.com", 80);
                final java.io.OutputStream server_os = server_sock.getOutputStream();
                final java.io.InputStream server_is = server_sock.getInputStream()
            ) {
                server_os.write(request_wrap(req_code, reqbdy_data));
                rsp_callback.accept(stream_helper.read_input_stream(server_is));
            } catch (final java.lang.Exception e) {
                final byte[] e_data = e.toString().getBytes();
                final byte[] rsp_data = new byte[e_data.length + 4];
                rsp_data[0] = rsp_data[1] = rsp_data[2] = rsp_data[3] = -1;
                java.lang.System.arraycopy(e_data, 0, rsp_data, 4, e_data.length);
                rsp_callback.accept(rsp_data);
            }
        }).start();
    }
}
