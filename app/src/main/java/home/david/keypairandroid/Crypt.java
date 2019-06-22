package home.david.keypairandroid;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by david on 12/18/16.
 */
public class Crypt {

    private String password;
    private Cipher cipher;
    private char c;

    public Crypt() {
        password = null;
        c = 0;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public void setPassword(String password) throws Exception {
        if (password.matches(".+:(b|buf|buffer)\\(.\\)$")) {
            String buffer=password.substring(password.length()-2, password.length()-1);
            c = buffer.charAt(0);
        }
        if (c == 0) {
            c = '*';
        }
        this.password = password.replaceAll("(.+):(b|buf|buffer)\\(.\\)","$1");
    }

    public void setBuffer(char c) {
        this.c = c;
    }

    public byte[] encrypt(String clear) throws Exception {
        byte[] result = null;
        if (password != null || c != 0) {
            byte[] encrypted_text = null;
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(getKey());
            byte[] keyBytes = new byte[16];
            System.arraycopy(digest.digest(), 0, keyBytes, 0, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            encrypted_text = cipher.doFinal(clear.getBytes());
            byte[] encryptedWithIV = new byte[16 + encrypted_text.length];
            System.arraycopy(iv, 0, encryptedWithIV, 0, 16);
            System.arraycopy(encrypted_text, 0, encryptedWithIV, 16, encrypted_text.length);
            result = encryptedWithIV;
        } else {
            throw new NoPasswordException("password was not set");
        }
        return result;
    }

    public String decrypt(byte[] encrypted) throws Exception {
        String decrypted_text = null;
        if (password != null && c != 0) {
            byte[] iv = new byte[16];
            System.arraycopy(encrypted, 0, iv, 0, 16);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            int es = encrypted.length - 16;
            byte[] encryptedNoSalt = new byte[es];
            System.arraycopy(encrypted, 16, encryptedNoSalt, 0, es);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(getKey());
            byte[] keyBytes = new byte[16];
            System.arraycopy(md.digest(), 0, keyBytes, 0, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] temp = cipher.doFinal(encryptedNoSalt);
            decrypted_text = new String(temp);
        } else {
            throw new NoPasswordException("password was not set");
        }
        return decrypted_text;
    }

    private byte[] getKey() throws Exception {
        String pw = null;
        if (password.length() < 16) {
            char[] b=new char[16-password.length()];
            for (int i=0; i<b.length; i++) {
                b[i]=c;
            }
            pw = password+new String(b);
        } else if (password.length() > 16) {
            pw = password.substring(0, 16);
        } else {
            pw = password;
        }

        return pw.getBytes(StandardCharsets.UTF_8);
    }

    public static String getHash(String clearText) {
        String result = null;
        try {
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] encodedHashBytes = digest.digest(clearText.getBytes(StandardCharsets.UTF_8));
            result = getHexFromBytes(encodedHashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getHexFromBytes(byte[] bytes) {
        String result = null;
        StringBuilder bufferedHash=new StringBuilder();
        for (int i = 0, bytesLength = bytes.length; i < bytesLength; i++) {
            byte b = bytes[i];
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) bufferedHash.append('0');
            bufferedHash.append(hex);
        }
        result = bufferedHash.toString();
        return result;
    }

    public static byte[] getBytesFromHex(String hex) {
        byte[] result = null;
        int len = hex.length();
        result = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return result;
    }

    public String getEncryptedHexString(String clearText) throws Exception {
        String result = null;
        //result = getHexFromBytes(encrypt(clearText));
        result = encodeHexString(encrypt(clearText));
        return result;
    }

    public String getDecryptedFromHexString(String hex) throws Exception {
        String result = null;
        //result = decrypt(getBytesFromHex(hex));
        result = decrypt(decodeHexString(hex));
        return result;
    }

    public String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }
}
