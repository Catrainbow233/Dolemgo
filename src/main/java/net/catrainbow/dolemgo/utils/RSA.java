package net.catrainbow.dolemgo.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
public class RSA {
    private final static String ALGORITHM_RSA = "RSA";
    public static List<Key> getRSAKeyObject(int modulus) throws NoSuchAlgorithmException{

        List<Key> keyList = new ArrayList<>(2);
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyPairGen.initialize(modulus);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        keyList.add(keyPair.getPublic());
        keyList.add(keyPair.getPrivate());
        return keyList;
    }
    public static List<String> getRSAKeyString(int modulus) throws NoSuchAlgorithmException{

        List<String> keyList = new ArrayList<>(2);
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyPairGen.initialize(modulus);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        keyList.add(publicKey);
        keyList.add(privateKey);
        return keyList;
    }
    public static RSAPublicKey getPublicKey(String publicKey) throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }
    public static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int modulusSize = publicKey.getModulus().bitLength() / 8;
        int maxSingleSize = modulusSize - 11;
        byte[][] dataArray = splitArray(data.getBytes(), maxSingleSize);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] s : dataArray) {
            out.write(cipher.doFinal(s));
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // RSA加密算法的模长 n
        int modulusSize = privateKey.getModulus().bitLength() / 8;
        byte[] dataBytes = data.getBytes();
        // 之前加密的时候做了转码，此处需要使用Base64进行解码
        byte[] decodeData = Base64.getDecoder().decode(dataBytes);
        // 切分字节数组，每段不大于modulusSize
        byte[][] splitArrays = splitArray(decodeData, modulusSize);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for(byte[] arr : splitArrays){
            out.write(cipher.doFinal(arr));
        }
        return new String(out.toByteArray());
    }
    private static byte[][] splitArray(byte[] data,int len){

        int dataLen = data.length;
        if (dataLen <= len) {
            return new byte[][]{data};
        }
        byte[][] result = new byte[(dataLen-1)/len + 1][];
        int resultLen = result.length;
        for (int i = 0; i < resultLen; i++) {
            if (i == resultLen - 1) {
                int slen = dataLen - len * i;
                byte[] single = new byte[slen];
                System.arraycopy(data, len * i, single, 0, slen);
                result[i] = single;
                break;
            }
            byte[] single = new byte[len];
            System.arraycopy(data, len * i, single, 0, len);
            result[i] = single;
        }
        return result;
    }


}
