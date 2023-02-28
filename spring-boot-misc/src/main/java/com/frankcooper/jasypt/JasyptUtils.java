package com.frankcooper.jasypt;

//import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
//import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptUtils {
//    private static final String PBEWITHMD5ANDDES = "PBEWithMD5AndDES";
//    private static final String PBEWITHHMACSHA512ANDAES_256 = "PBEWITHHMACSHA512ANDAES_256";
//
//    public static SimpleStringPBEConfig encryptJasypt(String salt) {
//        //加解密配置
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        config.setPassword(salt);
//        config.setAlgorithm(PBEWITHMD5ANDDES);
//        config.setKeyObtentionIterations("1000");
//        config.setPoolSize("1");
//        config.setProviderName("SunJCE");
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setStringOutputType("base64");
//        return config;
//    }
//
//    public static String encryptPassword(String salt, String password) {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = encryptJasypt(salt);
//        encryptor.setConfig(config);
//        return encryptor.encrypt(password);
//    }
//
//    public static String decryptPassword(String salt, String password) {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = encryptJasypt(salt);
//        encryptor.setConfig(config);
//        return encryptor.decrypt(password);
//    }
//
//    public static void main(String[] args) {
//        String s = encryptPassword("salt", "12345");
//        System.out.println("root加密后：" + s);
//
//        String password = decryptPassword("salt", s);
//        System.out.println("root解密后：" + password);
//    }

}
