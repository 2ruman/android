package com.truman.example.cryptography;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class CryptoManager {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep

    private static final int DEFAULT_PBKDF_INTERATION = 1000;

    private static CryptoManager sInstance;
    private CryptoManager() { }
    public static synchronized CryptoManager getInstance() {
        if (sInstance == null) {
            sInstance = new CryptoManager();
        }
        return sInstance;
    }

    public static byte[] PBKDF2(byte[] passwordBytes, byte[] salt) throws RuntimeException {
        char[] password = bytesToChars(passwordBytes);
        byte[] ret;
        try {
            SecretKey secretKey = PBKDF2_HMAC_SHA256(password, salt);
            ret = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NullPointerException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed in PBKDF2", e);
        } finally {
            zeroize(password);
        }
        return ret;
    }

    public static SecretKey PBKDF2_HMAC_SHA256(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(password, salt, DEFAULT_PBKDF_INTERATION, 256);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }

    public static SecretKey PBKDF2_HMAC_SHA512(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA512");
        KeySpec keySpec = new PBEKeySpec(password, salt, DEFAULT_PBKDF_INTERATION, 512);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }

    public static char[] bytesToChars(byte[] bytes) {
        if (bytes == null) return null;

        char[] chars = new char[bytes.length];
        for (int i = 0 ; i < bytes.length ; i++) {
            chars[i] = (char)bytes[i];
        }
        return chars;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "null";
        if (bytes.length == 0) return "";

        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null) throw new IllegalArgumentException("Invalid hex string");

        int retLen = hex.length() / 2;
        byte[] ret = new byte[retLen];
        int retIdx = 0, hexIdx = 0;
        for ( ; retIdx < retLen ; retIdx+=1, hexIdx+=2) {
            ret[retIdx] = (byte) (charToDigit(hex.charAt(hexIdx)) << 4
                                    | charToDigit(hex.charAt(hexIdx + 1)));
        }
        return ret;
    }

    private static int charToDigit(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        } else if ('a' <= c && c <= 'f') {
            return 10 + (c - 'a');
        } else if ('A' <= c && c <= 'F') {
            return 10 + (c - 'A');
        }
        throw new IllegalArgumentException("Invalid hex char");
    }

    public static void zeroize(byte[] bytes) {
        if (bytes != null)
            Arrays.fill(bytes, (byte) 0);
        return;
    }

    public static void zeroize(char[] chars) {
        if (chars != null)
            Arrays.fill(chars, (char) 0);
        return;
    }
}
