package com.buhzzi.wuej_0.kit;

import android.content.Context;
import android.content.res.Resources;

import com.buhzzi.wuej_0.R;

public class ServerRelative {
	public static byte[] magic = {-2, 69, 66, 77, 55, 71, 68, 55};
	public static byte[] jsonMagic = "json".getBytes();
	public static Resources res;

	public static void init(Context ctx) {
		res = ctx.getResources();
	}

	public static java.lang.Object readT240126() throws java.lang.Exception {
		final android.content.res.AssetFileDescriptor T240126Fd = res.openRawResourceFd(R.raw._240130);
		final byte[] T240126Data = new byte[(int) T240126Fd.getLength()];
		final java.io.FileInputStream T240126Fis = T240126Fd.createInputStream();
		assert T240126Fis.read(T240126Data) == T240126Data.length;
		T240126Fis.close();
		return java.security.KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(T240126Data));
	}

	public static byte[] getTshr16() {
		final byte[] tshr16Arr = new byte[8];
		int ndx = 7;
		int tshr16 = (int) java.lang.System.currentTimeMillis() >>> 16;
		int digi;
		while (tshr16 != 0) {
			digi = tshr16 % 24;
			tshr16 /= 24;
			tshr16Arr[ndx--] = (byte) (digi + (digi < 10 ? 48 : 55));
		}
		while (ndx >= 0) {
			tshr16Arr[ndx--] = 95;
		}
		return tshr16Arr;
	}

	public static byte[] requestWrap(final long reqCode, final byte[] reqbdyData) {
		final byte[] reqData = new byte[reqbdyData.length + 12];
		final java.nio.ByteBuffer req_buf = java.nio.ByteBuffer.allocate(12);
		req_buf.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		req_buf.putLong(reqCode ^ 0x374447374d4245ffL);
		req_buf.putInt(reqData.length);
		java.lang.System.arraycopy(req_buf.array(), 0, reqData, 0, 12);
		java.lang.System.arraycopy(reqbdyData, 0, reqData, 12, reqbdyData.length);
		return reqData;
	}

	public static void fetch(final long reqCode, final byte[] reqbdyData, final java.util.function.Consumer<byte[]> rspCallback) {
		new java.lang.Thread(() -> {
			try (
				final java.net.Socket serverSock = new java.net.Socket("buhzzi.com", 80);
				final java.io.OutputStream serverOs = serverSock.getOutputStream();
				final java.io.InputStream serverIs = serverSock.getInputStream()
			) {
				serverOs.write(requestWrap(reqCode, reqbdyData));
				rspCallback.accept(StreamHelper.readInputStream(serverIs));
			} catch (final java.lang.Exception e) {
				final byte[] e_data = e.toString().getBytes();
				final byte[] rspData = new byte[e_data.length + 4];
				rspData[0] = rspData[1] = rspData[2] = rspData[3] = -1;
				java.lang.System.arraycopy(e_data, 0, rspData, 4, e_data.length);
				rspCallback.accept(rspData);
			}
		}).start();
	}
}
