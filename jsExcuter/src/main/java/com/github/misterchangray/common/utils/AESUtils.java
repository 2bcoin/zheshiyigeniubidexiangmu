package com.github.misterchangray.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 4/29/2018.
 */
public class AESUtils {
    //初始化向量，aes 16位
    private static final String IV = "a5856355ef87955f";

    public static void main(String[] args) throws Exception {

        String content = "梅须逊雪三分白，雪却输梅一段香。";
        String password = "0123456789abcde1f";   //此处使用AES-128-CBC加密模式，key需要为16位

        System.out.println("加密前：" + content);
        String encryptResult = AESUtils.encrypt(content, password);
        System.out.println("加密后：" + encryptResult);
        String decryptResult = AESUtils.decrypt(encryptResult,password);
        System.out.println("解密后：" + decryptResult);
    }


    //加密
    public static String encrypt(String content, String keyWord) throws Exception {
        if(null == content || null == keyWord) throw new RuntimeException("参数错误");
        if(16 != keyWord.length()) throw new RuntimeException("keyWord必须为16位字符");

        try {
            SecretKeySpec key = new SecretKeySpec(keyWord.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
            byte[] encryptedData = cipher.doFinal(content.getBytes("UTF-8"));
            return parseByte2HexStr(encryptedData);
        } catch (Exception e) {
            throw new Exception("加密失败");
        }
    }

    //解密
    public static String decrypt(String content, String keyWord) throws Exception {
        if(null == content || null == keyWord) throw new RuntimeException("参数错误");
        if(16 != keyWord.length()) throw new RuntimeException("keyWord必须为16位字符");

        byte[] contentBytes = parseHexStr2Byte(content);
        try {
            SecretKeySpec key = new SecretKeySpec(keyWord.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
            byte[] result = cipher.doFinal(contentBytes);
            return new String(result, "UTF-8");
        } catch (Exception e) {
            throw new Exception("解密失败");
        }
    }

    //二进制转变为16进制
    private static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    //将16进制转变为二进制
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }



}
