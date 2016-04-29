package com.reign.ast.sdk.alipay;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * rsa
 * @author zhouwenjia
 *
 */
public class Rsa {
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes = Base64.decode(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	public static String encrypt(String content, String key) {
		try {
			PublicKey pubkey = getPublicKey(key);

			Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(1, pubkey);

			byte[] plaintext = content.getBytes("UTF-8");
			byte[] output = cipher.doFinal(plaintext);

			return new String(Base64.encode(output));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * sign
	 * @param content
	 * @param privateKey
	 * @return
	 */
	public static String sign (String content, String privateKey){  
        String charset = "utf-8";  
        try{  
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec (Base64.decode (privateKey));  
            KeyFactory keyf = KeyFactory.getInstance ("RSA");  
            PrivateKey priKey = keyf.generatePrivate (priPKCS8);  
            java.security.Signature signature = java.security.Signature.getInstance (SIGN_ALGORITHMS);  
            signature.initSign (priKey);  
            signature.update (content.getBytes (charset));  
            byte[] signed = signature.sign ();  
            return Base64.encode (signed);  
        } catch (Exception e){  
            e.printStackTrace ();  
        }  
        return null;  
    }

	/**
	 * doCheck
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @return
	 */
	public static boolean doCheck (String content, String sign, String publicKey) {  
        try{  
            KeyFactory keyFactory = KeyFactory.getInstance ("RSA");  
            byte[] encodedKey = Base64.decode (publicKey);  
            PublicKey pubKey = keyFactory.generatePublic (new X509EncodedKeySpec (encodedKey));  
            java.security.Signature signature = Signature.getInstance (SIGN_ALGORITHMS);  
            signature.initVerify (pubKey);  
            signature.update (content.getBytes ("utf-8"));  
            boolean bverify = signature.verify (Base64.decode (sign));  
            return bverify;  
        } catch (Exception e){  
            e.printStackTrace ();  
        }  
        return false;  
    }  
	
}
