package com.buhzzi.wuej_0.kit;

public class RsaRelative {
	public static byte[][] generateKp(int len) {
		try {
			final java.security.KeyPairGenerator kpGen = java.security.KeyPairGenerator.getInstance("RSA");
			kpGen.initialize(len);
			final java.security.KeyPair kp = kpGen.generateKeyPair();
			return new byte[][]{kp.getPrivate().getEncoded(), kp.getPublic().getEncoded()};
		} catch (java.lang.Exception e) {
			return null;
		}
	}

	public static byte[] encrypt(final byte[] pubData, final byte[] msgData) {
		try {
			final java.security.PublicKey pub = java.security.KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(pubData));
			final int maxEncrBsize = (((java.security.interfaces.RSAKey) pub).getModulus().bitLength() >>> 3);
			final int maxDecrBsize = maxEncrBsize - 11;
			final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, pub);
			final byte[] encrMsgData = new byte[((msgData.length - 1) / maxDecrBsize + 1) * maxEncrBsize];
			int off = 0;
			int encrOff = 0;
			while (off + maxDecrBsize < msgData.length) {
				encrOff += cipher.doFinal(msgData, off, maxDecrBsize, encrMsgData, encrOff);
				off += maxDecrBsize;
			}
			cipher.doFinal(msgData, off, msgData.length - off, encrMsgData, encrOff);
			return encrMsgData;
		} catch (java.lang.Exception e) {
			return null;
		}
	}

	public static byte[] decrypt(final byte[] priData, final byte[] msgData) {
		try {
			final java.security.PrivateKey pri = java.security.KeyFactory.getInstance("RSA").generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(priData));
			final int maxEncrBsize = (((java.security.interfaces.RSAKey) pri).getModulus().bitLength() >>> 3);
			final int maxDecrBsize = maxEncrBsize - 11;
			final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(javax.crypto.Cipher.DECRYPT_MODE, pri);
			final byte[] trailData = cipher.doFinal(msgData, msgData.length - maxEncrBsize, maxEncrBsize);
			final byte[] decrMsgData = new byte[(msgData.length / maxEncrBsize - 1) * maxDecrBsize + trailData.length];
			int decrOff = 0;
			int off = 0;
			while (off + maxEncrBsize < msgData.length) {
				decrOff += cipher.doFinal(msgData, off, maxEncrBsize, decrMsgData, decrOff);
				off += maxEncrBsize;
			}
			java.lang.System.arraycopy(trailData, 0, decrMsgData, decrOff, trailData.length);
			return decrMsgData;
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
