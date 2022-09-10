package net.catrainbow.dolemgo.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class AES {

    private static final int KEY_SIZE = 128;

    private static final String ALGORITHM = "AES";

    private static final String RNG_ALGORITHM = "SHA1PRNG";

    private static SecretKey generateKey(byte[] key) throws Exception {
        SecureRandom random = SecureRandom.getInstance(RNG_ALGORITHM);
        random.setSeed(key);
        KeyGenerator gen = KeyGenerator.getInstance(ALGORITHM);
        gen.init(KEY_SIZE, random);

        return gen.generateKey();
    }

    public static byte[] encrypt(byte[] plainBytes, byte[] key) throws Exception {

        SecretKey secKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] cipherBytes = cipher.doFinal(plainBytes);

        return cipherBytes;
    }

    public static byte[] decrypt(byte[] cipherBytes, byte[] key) throws Exception {
        SecretKey secKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        return plainBytes;
    }


}
