package com.buhzzi.wuej_0.kit;

public class RsaRelative {
	public static byte[][] generateKp(int len) {
		try {
			final java.security.KeyPairGenerator kp_gen = java.security.KeyPairGenerator.getInstance("RSA");
			kp_gen.initialize(len);
			final java.security.KeyPair kp = kp_gen.generateKeyPair();
			return new byte[][]{kp.getPrivate().getEncoded(), kp.getPublic().getEncoded()};
		} catch (java.lang.Exception e) {
			return null;
		}
	}

	public static byte[] encrypt(final byte[] pubData, final byte[] msgData) {
		try {
			final java.security.PublicKey pub = java.security.KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(pubData));
			final int max_encr_bsize = (((java.security.interfaces.RSAKey) pub).getModulus().bitLength() >>> 3);
			final int max_decr_bsize = max_encr_bsize - 11;
			final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, pub);
			final byte[] encr_msg_data = new byte[((msgData.length - 1) / max_decr_bsize + 1) * max_encr_bsize];
			int off = 0;
			int encr_off = 0;
			while (off + max_decr_bsize < msgData.length) {
				encr_off += cipher.doFinal(msgData, off, max_decr_bsize, encr_msg_data, encr_off);
				off += max_decr_bsize;
			}
			cipher.doFinal(msgData, off, msgData.length - off, encr_msg_data, encr_off);
			return encr_msg_data;
		} catch (java.lang.Exception e) {
			return null;
		}
	}

	public static byte[] decrypt(final byte[] priData, final byte[] msgData) {
		try {
			final java.security.PrivateKey pri = java.security.KeyFactory.getInstance("RSA").generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(priData));
			final int max_encr_bsize = (((java.security.interfaces.RSAKey) pri).getModulus().bitLength() >>> 3);
			final int max_decr_bsize = max_encr_bsize - 11;
			final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(javax.crypto.Cipher.DECRYPT_MODE, pri);
			final byte[] trail_data = cipher.doFinal(msgData, msgData.length - max_encr_bsize, max_encr_bsize);
			final byte[] decr_msg_data = new byte[(msgData.length / max_encr_bsize - 1) * max_decr_bsize + trail_data.length];
			int decr_off = 0;
			int off = 0;
			while (off + max_encr_bsize < msgData.length) {
				decr_off += cipher.doFinal(msgData, off, max_encr_bsize, decr_msg_data, decr_off);
				off += max_encr_bsize;
			}
			java.lang.System.arraycopy(trail_data, 0, decr_msg_data, decr_off, trail_data.length);
			return decr_msg_data;
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] sign(final byte[] priData, final byte[] msgData) {
		try {
			final java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
			signature.initSign(java.security.KeyFactory.getInstance("RSA").generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(priData)));
			signature.update(msgData);
			return signature.sign();
		} catch (java.lang.Exception e) {
			return null;
		}
	}

	public static int verify(final byte[] pubData, final byte[] signedMsgData, final byte[] msgData) {
		try {
			final java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
			signature.initVerify(java.security.KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(pubData)));
			signature.update(msgData);
			return signature.verify(signedMsgData) ? 0 : 1;
		} catch (java.lang.Exception e) {
			return -1;
		}
	}
}
