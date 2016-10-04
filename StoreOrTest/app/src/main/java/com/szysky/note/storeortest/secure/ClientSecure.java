package com.szysky.note.storeortest.secure;



import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.security.KeyRep;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author :  suzeyu
 * Time   :  2016-10-04  下午3:42
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 客户端加密演练
 */

public class ClientSecure {

    public static char[] userPassword = new char[]{'s','u','z','e','y','u'};
    public   static String textStr = "交易地点, 北京天安门南边,下午三点";

    public static void main(String arg[]){
        try {
            // 进行加密
            String encryResult = encryptClearText(userPassword, textStr);
            System.out.println("加密后的结果:"+encryResult);

            // 进行解密
            String rawData = decryptData(userPassword, encryResult);
            System.out.println("解密后得到的原始数据:"+rawData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成密钥
     */
    public static SecretKey generateKey(char[] password , byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        int outputKeyLength = 128;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, iterations, outputKeyLength);

        byte[] keyBytes = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     *  加密数据
     */
    public static String encryptClearText(char[] password, String plainText)
            throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        int saltLength = 8;
        byte[] salt = new byte[saltLength];
        secureRandom.nextBytes(salt);
        SecretKey secretKey = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] initVector = new byte[cipher.getBlockSize()];
        secureRandom.nextBytes(initVector);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] cipherData = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.encodeToString(cipherData,
                Base64.NO_WRAP | Base64.NO_PADDING)
                + "]" + Base64.encodeToString(initVector,
                Base64.NO_WRAP | Base64.NO_PADDING)
                + "]" + Base64.encodeToString(salt,
                Base64.NO_WRAP | Base64.NO_PADDING);
    }


    /**
     *  解密数据
     */
    public static String decryptData(char[] password, String encodedData)
            throws Exception {
        String[] parts = encodedData.split("]");
        byte[] cipherData = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] initVector = Base64.decode(parts[1], Base64.DEFAULT);
        byte[] salt = Base64.decode(parts[2], Base64.DEFAULT);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParams = new IvParameterSpec(initVector);
        SecretKey secretKey = generateKey(password, salt);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
        return new String(cipher.doFinal(cipherData), "UTF-8");
    }

    /**
     *  处理加密数据
     */
    public String encryptNoteDataCollection(Collection<NoteData> notes,
                                            char[] password) {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<NoteData>>(){}.getType();
        gson.toJson(notes, type, jsonWriter);
        String clearText = writer.toString();
        try {
            return encryptClearText(password, clearText);
        } catch (Exception e) {
            // Ignore for brevity
            return null;
        }
    }

    /**
     *  处理解密数据
     */
    public static Collection<NoteData> decryptAndDecode(char[] password,
                                                        String encryptedData) {
        try {
            String jsonData = decryptData(password, encryptedData);
            Gson gson =  new Gson();
            Type type = new TypeToken<Collection<NoteData>>(){}.getType();
            JsonReader jsonReader = new JsonReader(new StringReader(jsonData));
            return gson.fromJson(jsonReader, type);
        } catch (Exception e) {
            // Ignore for brevity...
            return null;
        }
    }

}
