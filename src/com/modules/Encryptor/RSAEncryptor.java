package com.modules.Encryptor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSAEncryptor {
    
    
    // ___________________________________________  A Beautiful Separate Line  ___________________________________________
    
    
    // openssl version -a
    
    /**
     * 
     * 1. Create the RSA Private Key
     * 
     * openssl genrsa -out private_key.pem 1024                                                                         -> Generate 'private_key.pem'
     * 
     * 
     * 2. Create a certificate signing request with the private key
     * (This step u will enter some Info. For further reference, you'd better write them down or make a screen shot)
     * 
     * openssl req -new -key private_key.pem -out rsaCertReq.csr                                                        -> Generate 'rsaCertReq.csr'
     * 
     * 
     * 3. Create a self-signed certificate with the private key and signing request                                     -> Generate 'rsaCert.crt'
     * 
     * openssl x509 -req -days 3650 -in rsaCertReq.csr -signkey private_key.pem -out rsaCert.crt        
     * 
     * 
     * 
     * 
     * 4. Convert the certificate to DER format: the certificate contains the public key
     * 
     * openssl x509 -outform der -in rsaCert.crt -out public_key.der                                                    -> Generate 'public_key.der' (for IOS to encrypt) 
     * 
     * 
     * 5. Export the private key and certificate to p12 file. 
     * (This step will ask u to enter password, it will be used in your IOS Code, do not forget it)
     * 
     * openssl pkcs12 -export -out private_key.p12 -inkey private_key.pem -in rsaCert.crt                               -> Generate 'private_key.p12' (for IOS to decrypt) 
     * 
     * 
     * 
     * 
     * 6.
     * openssl rsa -in private_key.pem -out rsa_public_key.pem -pubout                                                  -> Generate 'rsa_public_key.pem' (for JAVA to encrypt)
     * 
     * 7.
     * openssl pkcs8 -topk8 -in private_key.pem -out pkcs8_private_key.pem -nocrypt                                     -> Generate 'pkcs8_private_key.pem' (for JAVA to decrypt)
     * 
     * 
     */
    
    /**
     * @param publicKeyFilePath     The file from step 4.
     * @param privateKeyFilePath    The file from step 5. PKCS#8 format private key file .
     */
    public RSAEncryptor(String publicKeyFilePath, String privateKeyFilePath) throws Exception {
        String public_key = getKeyFromFile(publicKeyFilePath);
        String private_key = getKeyFromFile(privateKeyFilePath);
        loadPublicKey(public_key);  
        loadPrivateKey(private_key);  
    }
    public RSAEncryptor() {
        // load the PublicKey and PrivateKey manually
    }
    
    
    public String getKeyFromFile(String filePath) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        
        String line = null;
        List<String> list = new ArrayList<String>();
        while ((line = bufferedReader.readLine()) != null){
            list.add(line);
        }
        
        // remove the firt line and last line
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < list.size() - 1; i++) {
            stringBuilder.append(list.get(i)).append("\r");
        }
        
        String key = stringBuilder.toString();
        return key;
    }
    
    public String decryptWithBase64(String base64String) throws Exception {
        //  http://commons.apache.org/proper/commons-codec/ : org.apache.commons.codec.binary.Base64
        // sun.misc.BASE64Decoder
        byte[] binaryData = decrypt(getPrivateKey(), new BASE64Decoder().decodeBuffer(base64String) /*org.apache.commons.codec.binary.Base64.decodeBase64(base46String.getBytes())*/);
        String string = new String(binaryData);
        return string;
    }
    
    public String encryptWithBase64(String string) throws Exception {
        //  http://commons.apache.org/proper/commons-codec/ : org.apache.commons.codec.binary.Base64
        // sun.misc.BASE64Encoder
        byte[] binaryData = encrypt(getPublicKey(), string.getBytes());
        String base64String = new BASE64Encoder().encodeBuffer(binaryData) /* org.apache.commons.codec.binary.Base64.encodeBase64(binaryData) */;
        return base64String;
    }
  
    
    
    // convenient properties
    public static RSAEncryptor sharedInstance = null;
    public static void setSharedInstance (RSAEncryptor rsaEncryptor) {
        sharedInstance = rsaEncryptor;
    }
    
    
    // ___________________________________________  A Beautiful Separate Line  ___________________________________________
    
    
    
    
    
    
    
    
    

    /** 
     * 私钥 
     */  
    private RSAPrivateKey privateKey;  
  
    /** 
     * 公钥 
     */  
    private RSAPublicKey publicKey;  
      
    /** 
     * 获取私钥 
     * @return 当前的私钥对象 
     */  
    public RSAPrivateKey getPrivateKey() {  
        return privateKey;  
    }  
  
    /** 
     * 获取公钥 
     * @return 当前的公钥对象 
     */  
    public RSAPublicKey getPublicKey() {  
        return publicKey;  
    }  
  
    /** 
     * 随机生成密钥对 
     */  
    public void genKeyPair(){  
        KeyPairGenerator keyPairGen= null;  
        try {  
            keyPairGen= KeyPairGenerator.getInstance("RSA");  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        keyPairGen.initialize(1024, new SecureRandom());  
        KeyPair keyPair= keyPairGen.generateKeyPair();  
        this.privateKey= (RSAPrivateKey) keyPair.getPrivate();  
        this.publicKey= (RSAPublicKey) keyPair.getPublic();  
    }  
  
    /** 
     * 从文件中输入流中加载公钥 
     * @param in 公钥输入流 
     * @throws Exception 加载公钥时产生的异常 
     */  
    public void loadPublicKey(InputStream in) throws Exception{  
        try {  
            BufferedReader br= new BufferedReader(new InputStreamReader(in));  
            String readLine= null;  
            StringBuilder sb= new StringBuilder();  
            while((readLine= br.readLine())!=null){  
                if(readLine.charAt(0)=='-'){  
                    continue;  
                }else{  
                    sb.append(readLine);  
                    sb.append('\r');  
                }  
            }  
            loadPublicKey(sb.toString());  
        } catch (IOException e) {  
            throw new Exception("公钥数据流读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥输入流为空");  
        }  
    }  
  
    /** 
     * 从字符串中加载公钥 
     * @param publicKeyStr 公钥数据字符串 
     * @throws Exception 加载公钥时产生的异常 
     */  
    public void loadPublicKey(String publicKeyStr) throws Exception{  
        try {  
            BASE64Decoder base64Decoder= new BASE64Decoder();  
            byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);  
            this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("公钥非法");  
        } catch (IOException e) {  
            throw new Exception("公钥数据内容读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("公钥数据为空");  
        }  
    }  
  
    /** 
     * 从文件中加载私钥 
     * @param keyFileName 私钥文件名 
     * @return 是否成功 
     * @throws Exception  
     */  
    public void loadPrivateKey(InputStream in) throws Exception{  
        try {  
            BufferedReader br= new BufferedReader(new InputStreamReader(in));  
            String readLine= null;  
            StringBuilder sb= new StringBuilder();  
            while((readLine= br.readLine())!=null){  
                if(readLine.charAt(0)=='-'){  
                    continue;  
                }else{  
                    sb.append(readLine);  
                    sb.append('\r');  
                }  
            }  
            loadPrivateKey(sb.toString());  
        } catch (IOException e) {  
            throw new Exception("私钥数据读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥输入流为空");  
        }  
    }  
  
    public void loadPrivateKey(String privateKeyStr) throws Exception{  
        try {  
            BASE64Decoder base64Decoder= new BASE64Decoder();  
            byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);  
            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
            this.privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e) {  
        	e.printStackTrace();
            throw new Exception("私钥非法");  
        } catch (IOException e) {  
            throw new Exception("私钥数据内容读取错误");  
        } catch (NullPointerException e) {  
            throw new Exception("私钥数据为空");  
        }  
    }  
  
    /** 
     * 加密过程 
     * @param publicKey 公钥 
     * @param plainTextData 明文数据 
     * @return 
     * @throws Exception 加密过程中的异常信息 
     */  
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception{  
        if(publicKey== null){  
            throw new Exception("加密公钥为空, 请设置");  
        }  
        Cipher cipher= null;  
        try {  
            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            byte[] output= cipher.doFinal(plainTextData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此加密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        }catch (InvalidKeyException e) {  
            throw new Exception("加密公钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("明文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("明文数据已损坏");  
        }  
    }  
  
    /** 
     * 解密过程 
     * @param privateKey 私钥 
     * @param cipherData 密文数据 
     * @return 明文 
     * @throws Exception 解密过程中的异常信息 
     */  
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception{  
        if (privateKey== null){  
            throw new Exception("解密私钥为空, 请设置");  
        }  
        Cipher cipher= null;  
        try {  
            cipher= Cipher.getInstance("RSA");//, new BouncyCastleProvider());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
            byte[] output= cipher.doFinal(cipherData);  
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        }catch (InvalidKeyException e) {  
            throw new Exception("解密私钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("密文数据已损坏");  
        }         
    }  
  
    
    
    /** 
     * 字节数据转字符串专用集合 
     */  
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}; 
    
    /** 
     * 字节数据转十六进制字符串 
     * @param data 输入数据 
     * @return 十六进制内容 
     */  
    public static String byteArrayToString(byte[] data){  
        StringBuilder stringBuilder= new StringBuilder();  
        for (int i=0; i<data.length; i++){  
            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移  
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);  
            //取出字节的低四位 作为索引得到相应的十六进制标识符  
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);  
            if (i<data.length-1){  
                stringBuilder.append(' ');  
            }  
        }  
        return stringBuilder.toString();  
    }  
    
    
    
    
    
    
    
    
    
    
    
    
    
 // ___________________________________________ Begin Test  ___________________________________________
    
    
    public static final String DEFAULT_PUBLIC_KEY=   

            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbDRCvwEaVyL0uNHUzZIO1cH7K" + "\r" +
            "fwaQ/gAJ5VQ1478SqK6st7CiP9jTQMYyDyvUxQgG6KDE2mwPJwHzsYkzzKjH2OJz" + "\r" +
            "HUXUMSaSVdynTavJtmzdNKe7SPMCYhYqB3BmUgfOY6ZFh37gBwfDVcQ54DIbHCD0" + "\r" +
            "SUx+AmgPjdJgYZf9zwIDAQAB" + "\r";

      
    public static final String DEFAULT_PRIVATE_KEY=  
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANsNEK/ARpXIvS40" + "\r" +
            "dTNkg7Vwfsp/BpD+AAnlVDXjvxKorqy3sKI/2NNAxjIPK9TFCAbooMTabA8nAfOx" + "\r" +
            "iTPMqMfY4nMdRdQxJpJV3KdNq8m2bN00p7tI8wJiFioHcGZSB85jpkWHfuAHB8NV" + "\r" +
            "xDngMhscIPRJTH4CaA+N0mBhl/3PAgMBAAECgYEAjzH5SIrvGZeCZCQSwafhmciS" + "\r" +
            "ehmT11DUAaQS6q+ZBr+SgIIMS+Rk/6SGa88THiI3XxzPjCAlJ7qeQgo64MvXQKcA" + "\r" +
            "soTH1IgCgF+5WyjOvODNgS59dcQbamYLZcRptNv+79O5mxBsRwyS6HeHqZ24X/v2" + "\r" +
            "6YfcIEGC1BaJy2dKsPECQQD/VLOGYvoy8PCxhHVPBHxhWc6vcrj2y0PnYcNvP8n0" + "\r" +
            "M8Wj5mHdnKl2VF7lZt5dwtpZemXD8gmYT4YDiPDduthdAkEA26AGL7lWmAwQXZjL" + "\r" +
            "6MWlsy92zcfuiGe88N1GIAPPr3dL3HufAW2HHwAQIn3twZhXhldQOm9tiGu0teiG" + "\r" +
            "tI0cGwJAV5bk5wr5LZR93UfFPlAZowO95W4DiZX9O1jMRFOrofxIposXs4BUmeUj" + "\r" +
            "kKqTSbLYWK2mT2uuYvOU042co1O/eQJAbd/iGHAdnVWzvk+Z++sdmcZuJkcW09Eq" + "\r" +
            "WkopMg0WEw+YuUZzZxB3oA+1AryDfO4NI518+q8SWkSgFL2u3pcV7wJBAOQhH0Gn" + "\r" +
            "HfkHkuqK3v4gHZQVSZ94p/YIWF4prw7OsP345JOSEvvJANJ/pRH9FEIOWTfki1bH" + "\r" +
            "Edo54moo91zpQ0Y=" + "\r"; 
    
    
    public static void main(String[] args){  
        RSAEncryptor rsaEncrypt= new RSAEncryptor();  
        //rsaEncrypt.genKeyPair();  
  
        //加载公钥  
        try {  
            rsaEncrypt.loadPublicKey(RSAEncryptor.DEFAULT_PUBLIC_KEY);  
            System.out.println("加载公钥成功");  
            System.out.println("公钥长度："+rsaEncrypt.getPublicKey().toString().length());
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
            System.err.println("加载公钥失败");  
        }  
  
        //加载私钥  
        try {  
            rsaEncrypt.loadPrivateKey(RSAEncryptor.DEFAULT_PRIVATE_KEY);  
            System.out.println("加载私钥成功");  
            System.out.println("私钥长度："+rsaEncrypt.getPrivateKey().toString().length());
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
            System.err.println("加载私钥失败");  
        }  
  
        //测试字符串  
        String encryptStr= "JAVA";  
        try {  
            //加密  
            byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), encryptStr.getBytes());  
            //解密  
            byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(), cipher);  
           
            System.out.println("Encrypt string: " + encryptStr);
            System.out.println("Decrypt string: " + new String(plainText));  
        } catch (Exception e) {  
            System.err.println(e.getMessage());  
        }  
    }
   
 // ___________________________________________ End Test  ___________________________________________
   
}
