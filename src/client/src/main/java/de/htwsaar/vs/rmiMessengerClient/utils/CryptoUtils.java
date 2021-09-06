package de.htwsaar.vs.rmiMessengerClient.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Crypto Utils
 * @author https://howtodoinjava.com/java/java-security/aes-256-encryption-decryption/
 * @author Julian Klotz
 * @author Marcel Hesselbach
 * @version 1.0
 * @since 1.0
 */
public class CryptoUtils {
    private static final String SECRET_KEY = "SecretEschKey";
    private static final String SALT = "was_really_salty_during_development";
    private static final byte[] IV = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

    /**
     * encrypt Method
     * encrypts given String
     * @param strToEncrypt String to Encrypt
     * @return encrypted String
     */
    public static String encrypt(String strToEncrypt) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(IV);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
        }
        return null;
    }
    /**
     * decrypt Method
     * decrypts given String
     * @param strToDecrypt String to Decrypt
     * @return decrypted String
     */
    public static String decrypt(String strToDecrypt) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(IV);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e);
        }
        return null;
    }
}
