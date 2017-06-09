package com.yunjin.microlove.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description MD5工具类
 * @Author 一花一世界
 */
public class Md5Util {

    static final String HEXES = "0123456789abcdef";

    public static String md5Toword(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] raw = md.digest();
            if (raw == null) {
                return null;
            }
            final StringBuilder buf = new StringBuilder(2 * raw.length);
            for (final byte b : raw) {
                buf.append(HEXES.charAt((b & 0xF0) >> 4)).append(
                        HEXES.charAt((b & 0x0F)));
            }
            re_md5 = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }
}
