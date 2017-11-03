package com.cfp.kxf.enc;

import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.cfp.kxf.zip.ZipUtils;


/**
 * Created by kuangxf on 2017/7/31.
 */

public class EncUtil {

    private static final String CHARSET_NAME = "UTF-8";
    private static final String DEFUALT_KEY = "12345678123456781234567812345678";

//    public static void main(String[] args) throws IOException {
//        // 字符串超过一定的长度
//        String str = "ABCdef123中文~!@#$%^&*()_+{};/1111111111111111111111111AAAAAAAAAAAJDLFJDLFJDLFJLDFFFFJEIIIIIIIIIIFJJJJJJJJJJJJALLLLLLLLLLLLLLLLLLLLLL" +
//                "LLppppppppppppppppppppppppppppppppppppppppp===========================------------------------------iiiiiiiiiiiiiiiiiiiiiii";
//        System.out.println("\n原始的字符串为------->" + str);
//        float len0=str.length();
//        System.out.println("原始的字符串长度为------->"+len0);
//
//        String ys = encryptAsStringAfterZip(null,str);
//        System.out.println("\n压缩后的字符串为----->" + ys);
//        float len1=ys.length();
//        System.out.println("压缩后的字符串长度为----->" + len1);
//
//        String jy = desEncryptAsStringBeforeUnZip(null, ys);
//        System.out.println("\n解压缩后的字符串为--->" + jy);
//        System.out.println("解压缩后的字符串长度为--->"+jy.length());
//        
//        System.out.println("\n压缩比例为"+len1/len0);
//        
//        //判断
//        if(str.equals(jy)){
//            System.out.println("先压缩再解压以后字符串和原来的是一模一样的");
//        }else {
//        	System.err.println("先压缩再解压以后字符串和原来的不一样的");
//		}
//    }
    
    public static byte[] desEncrypt(String strKey, String msg) {
        if (msg == null)
            msg = "";
        if (strKey == null) {
            strKey = DEFUALT_KEY;
        }
        byte[] keyBytes = new byte[8];
        int saltLen = strKey.length();
        byte[] saltBytes = strKey.getBytes();
        for (int i = 0; i < 8; i++) {
            keyBytes[i] = saltBytes[i % saltLen];
        }

        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(
                    keySpec);
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] text = msg.getBytes(CHARSET_NAME);
            byte[] ciphertext = desCipher.doFinal(text);

            return ciphertext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String desDecrypt(String strKey, byte[] msg) {
        if (msg == null)
            return null;
        if (strKey == null) {
            strKey = DEFUALT_KEY;
        }
        byte[] keyBytes = new byte[8];
        int saltLen = strKey.length();
        byte[] saltBytes = strKey.getBytes();
        for (int i = 0; i < 8; i++) {
            keyBytes[i] = saltBytes[i % saltLen];
        }

        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(
                    keySpec);
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] deciphertext = desCipher.doFinal(msg);

            return new String(deciphertext, CHARSET_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dumpBytes(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            /*
			 * if (i%32 == 0 && i!=0) { sb.append("\n"); }
			 */
            String s = Integer.toHexString(bytes[i]);
            if (s.length() < 2) {
                s = "0" + s;
            }
            if (s.length() > 2) {
                s = s.substring(s.length() - 2);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static byte[] parseBytes(String str) {
        try {
            int len = str.length() / 2;
            if (len <= 2) {
                return new byte[]{Byte.parseByte(str)};
            }
            byte[] arr = new byte[len];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (byte) Integer.parseInt(
                        str.substring(i * 2, i * 2 + 2), 16);
            }
            return arr;
        } catch (Exception e) {
            return new byte[0];
        }
    }

    /**
     * 加密
     *
     * @param encrypt_value 被加密的字符串
     * @param encrypt_key   加密的密钥
     * @return
     */
    public static String encryptAsString(String encrypt_key, String encrypt_value) {
        return dumpBytes(desEncrypt(encrypt_key, encrypt_value));
    }

    /**
     * 解密
     *
     * @param encrypt_value 要解密的字符串
     * @param encrypt_key   密钥
     * @return
     */
    public static String desEncryptAsString(String encrypt_key, String encrypt_value) {
        return desDecrypt(encrypt_key, parseBytes(encrypt_value));
    }
    
    /**
     * 先压缩再加密
     *
     * @param encrypt_value 被加密的字符串
     * @param encrypt_key   加密的密钥
     * @return
     */
    public static String encryptAsStringAfterZip(String encrypt_key, String encrypt_value) {
    	String zip = null;
    	try {
			zip = ZipUtils.compressStr(encrypt_value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return dumpBytes(desEncrypt(encrypt_key, zip));
    }

    /**
     * 先解密再解压缩
     *
     * @param encrypt_value 要解密的字符串
     * @param encrypt_key   密钥
     * @return
     */
    public static String desEncryptAsStringBeforeUnZip(String encrypt_key, String encrypt_value) {
        String str = desDecrypt(encrypt_key, parseBytes(encrypt_value));
        try {
			return ZipUtils.unCompressStr(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
}
