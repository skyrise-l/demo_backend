package com.zju.QueryArtisan.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Random;

@Slf4j
public class PasswordUtil {
    public static String getRandSalt(){
        Random random = new Random();
        char[] chars = new char[10];

        for (int i = 0; i < 10; i++){
            chars[i] = (char)('a' + random.nextInt(26));
        }

        return new String(chars);
    }

    public static String passwordHash(String password, String salt) {
        String value = password + salt;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes("UTF-8"));
            return Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error("Password hashing error", e);
            return null;
        }
    }
}
