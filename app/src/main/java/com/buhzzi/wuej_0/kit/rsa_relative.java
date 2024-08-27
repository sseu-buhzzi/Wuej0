package com.buhzzi.wuej_0.kit;

public class rsa_relative {
    public static void test() throws Exception {
        final java.security.KeyPair kp = generate_kp(4096);
        final java.security.PrivateKey pri = kp.getPrivate();
        final java.security.PublicKey pub = kp.getPublic();

        final byte[] msg_data = "AAAABBBBCCCCDDDDjvqiwpo jti3qop jgiro wgjirpeq jifrqrpro jriewapo jgiprr jwiagpo j309c vur3920 ruji438p tu8q092p cri903u qt04q3 u9tgq3jtrq3 jgp 3wj8ptgg 34j8qpt0 tg43jq0 gjip0u3 g9p0q3 ugp3uq gu 30iqpg u30qp gupiq3 gip34u4ipt utgq0p3 ug03qu j pwa fmj48qp aEEEEFFFFGGGGHHHH".getBytes();
        final byte[] encrypted_msg_data = encrypt(pub, msg_data);
        final byte[] decrypted_msg_data = decrypt(pri, encrypted_msg_data);
        System.out.println(new String(decrypted_msg_data));
    }
    public static java.security.KeyPair generate_kp(final int len) throws Exception {
        final java.security.KeyPairGenerator kp_gen = java.security.KeyPairGenerator.getInstance("RSA");
        kp_gen.initialize(len);
        return kp_gen.generateKeyPair();
    }
    public static java.security.PrivateKey generate_pri(final byte[] pri_data) throws Exception {
        return java.security.KeyFactory.getInstance("RSA").generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(pri_data));
    }
    public static java.security.PublicKey generate_pub(final byte[] pub_data) throws Exception {
        return java.security.KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(pub_data));
    }
    public static byte[] encrypt(final java.security.PublicKey pub, final byte[] msg_data) throws Exception {
        final int encr_bsize = (((java.security.interfaces.RSAKey) pub).getModulus().bitLength() >>> 3);
        final int decr_bsize = encr_bsize - 11;
        final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, pub);
        final byte[] encr_msg_data = new byte[((msg_data.length - 1) / decr_bsize + 1) * encr_bsize];
        int off = 0;
        int encr_off = 0;
        while (off + decr_bsize < msg_data.length) {
            encr_off += cipher.doFinal(msg_data, off, decr_bsize, encr_msg_data, encr_off);
            off += decr_bsize;
        }
        cipher.doFinal(msg_data, off, msg_data.length - off, encr_msg_data, encr_off);
        return encr_msg_data;
    }
    public static byte[] decrypt(final java.security.PrivateKey pri, final byte[] msg_data) throws Exception {
        final int encr_bsize = (((java.security.interfaces.RSAKey) pri).getModulus().bitLength() >>> 3);
        final int decr_bsize = encr_bsize - 11;
        final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, pri);
        final byte[] trail_data = cipher.doFinal(msg_data, msg_data.length - encr_bsize, encr_bsize);
        final byte[] decr_msg_data = new byte[(msg_data.length / encr_bsize - 1) * decr_bsize + trail_data.length];
        int decr_off = 0;
        int off = 0;
        while (off + encr_bsize < msg_data.length) {
            decr_off += cipher.doFinal(msg_data, off, encr_bsize, decr_msg_data, decr_off);
            off += encr_bsize;
        }
        System.arraycopy(trail_data, 0, decr_msg_data, decr_off, trail_data.length);
        return decr_msg_data;
    }
    public static byte[] sign(final java.security.PrivateKey pri, final byte[] msg_data) throws Exception {
        final java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
        signature.initSign(pri);
        signature.update(msg_data);
        return signature.sign();
    }
    public static boolean verify(final java.security.PublicKey pub, final byte[] signed_msg_data, final byte[] msg_data) throws Exception {
        final java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
        signature.initVerify(pub);
        signature.update(msg_data);
        return signature.verify(signed_msg_data);
    }
}
