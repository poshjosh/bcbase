package com.bc.util;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


/**
 * @(#)SecurityTool.java   25-Dec-2014 08:27:36
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class SecurityTool {
    
    private static int counter;
    
    /**
     * The chars that will be used in the textual representation of the encoded bytes
     */
    private static final String DIGITS = "0123456789abcdef";

    /**
     * Cipher for encrypting
     */
    private final Cipher cipher;

    /**
     * Key for encrypting
     */
    private final SecretKeySpec secretKeySpec;

    public SecurityTool(String algorithm, String encryptionKey)
    throws NoSuchAlgorithmException, NoSuchPaddingException {
        
        cipher = Cipher.getInstance(algorithm);

        secretKeySpec = this.getSecretKeySpec(encryptionKey, algorithm);
    }

    public String generateUsername() {
        return generateUsername(2015);
    }

    public String generateUsername(int baseYear) {
        Calendar c = Calendar.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(c.get(Calendar.YEAR)-baseYear);
        builder.append(c.get(Calendar.MONTH));
        builder.append(c.get(Calendar.DAY_OF_MONTH));
        builder.append(c.get(Calendar.HOUR));
        builder.append(c.get(Calendar.MINUTE));
        long n = Long.parseLong(builder.toString());
        return "user_" + Long.toHexString(n) + (counter++);
    }
    
    public String getRandomUUID(final int outputSize) {
        String src = UUID.randomUUID().toString();
        src = src.replace("-", "");
        final int srcLen = src.length();
        
XLogger.getInstance().log(Level.FINER, "UUID: {0}, requried size: {1}", 
        this.getClass(), src, outputSize);        

        if(outputSize > srcLen) return src;
        
        int randomOffset = (int)(Math.random() * srcLen);
        
XLogger.getInstance().log(Level.FINER, "Random offset: {0}", this.getClass(), randomOffset);       

        int end = randomOffset + outputSize;
        int offsetAtBeginning = -1;
        if(end > srcLen) {
            offsetAtBeginning = end - srcLen;
            end = srcLen;
        }
        
XLogger.getInstance().log(Level.FINER, 
        "Random offset: {0}, offsetAtBegining: {1}, end: {2}", 
        this.getClass(), randomOffset, offsetAtBeginning, end);        

        String s = src.substring(randomOffset, end);
        if(offsetAtBeginning > 0) {
            s += src.substring(0, offsetAtBeginning);
        }
        
XLogger.getInstance().log(Level.FINER, "Output: {0}, size: {1}", 
        this.getClass(), s, outputSize);        

        return s;
    }
    
    private SecretKeySpec getSecretKeySpec(String encryptionKey, String algorithm) {

        //if the user has not set the password length correctly, just pad it out to 16 chars
        while (encryptionKey.length() < 16) {
            encryptionKey += "-";
        }

        //prepare a key for use with the cipher
        byte[] keyBytes = encryptionKey.getBytes();
        assert keyBytes.length >= 16;

        return new SecretKeySpec(keyBytes, 0, 16, algorithm);
    }
    
    /**
     * @param input The String to  encrypt
     * @return An encrypted String
     * @throws NullPointerException if the input String is null
     * @throws GeneralSecurityException 
     */
    public synchronized final String encrypt(String input) 
            throws GeneralSecurityException {
        
        if(input == null) {
            throw new NullPointerException("String to encrypt == null");
        }
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        //encryptString the string
        byte[] encrypted = cipher.doFinal(input.getBytes());
        String encryptedString = bytesToHex(encrypted);
        return encryptedString;
    }

    /**
     * @param input The String to  decrypt
     * @return An decrypted String
     * @throws NullPointerException if the input String is null
     * @throws GeneralSecurityException 
     */
    public synchronized final String decrypt(String input) 
            throws GeneralSecurityException {
        
        if(input == null) {
            throw new NullPointerException("String to decrypt == null");
        }

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        //decryptString the string
        byte[] decrypted = cipher.doFinal(hexToBytes(input));
        String decryptedString = new String(decrypted);
        return decryptedString;
    }

    public String encryptCookieValues(String val_1, String val_2) 
            throws GeneralSecurityException {
        String cookieString = String.format("%1s:::%2s", val_1, val_2);
        String encryptedCookieString = encrypt(cookieString);
        return encryptedCookieString;
    }

    public String[] decryptCookieValues(String encryptedCookie) 
            throws GeneralSecurityException {
        String[] emailAndScreenName = new String[2];
        String decryptedCookie = decrypt(encryptedCookie);
        StringTokenizer tokenizer = new StringTokenizer(decryptedCookie, ":::");
        emailAndScreenName[0] = tokenizer.nextToken();
        emailAndScreenName[1] = tokenizer.nextToken();
        return emailAndScreenName;
    }

    /**
     * Used to convert the encrypted bytes into chars to send over the web
     */
    private String bytesToHex(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            int v = b & 0xff;
            builder.append(DIGITS.charAt(v >> 4));
            builder.append(DIGITS.charAt(v & 0xf));
        }
        return builder.toString();
    }

    /**
     * Used to convert an encoded string into a byte array for de-encryping
     */
    private byte[] hexToBytes(String string) {
        byte[] data = new byte[string.length() / 2];
        for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
            char charA = string.charAt(dataIndex * 2);
            char charB = string.charAt(dataIndex * 2 + 1);
            int i = (byte) 0xff;
            i = i & ((byte) DIGITS.indexOf(charA));
            i = i << 4;
            i = i | ((byte) DIGITS.indexOf(charB));
            data[dataIndex] = (byte) i;
        }
        return data;
    }
}
