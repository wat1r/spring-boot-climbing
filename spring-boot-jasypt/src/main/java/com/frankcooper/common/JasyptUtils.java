package com.frankcooper.common;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.PBEConfig;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptUtils {
    /**
     * {@link StringEncryptor} 加解密。
     * 同一个密钥（salt）对同一个内容执行加密，生成的密文都是不一样的，但是根据根据这些密文解密成明文都是可以.
     * 1、Jasypt 默认使用 {@link StringEncryptor} 来解密全局配置文件中的属性，所以提供密文时，也需要提供 {@link StringEncryptor} 加密的密文
     * 2、{@link StringEncryptor} 接口有很多的实现类，比如常用的 {@link PooledPBEStringEncryptor}
     * 3、setConfig(final PBEConfig config)：为对象设置 {@link PBEConfig} 配置对象
     * 4、encrypt(final String password)：加密内容
     * 5、decrypt(final String encryptedMessage)：解密内容
     *
     * @param salt      ：密钥。加/解密必须使用同一个密钥
     * @param password  ：加/解密的内容
     * @param isEncrypt ：true 表示加密、false 表示解密
     * @return
     */
    public static String stringEncryptor(String salt, String password, boolean isEncrypt) {
        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        pooledPBEStringEncryptor.setConfig(getSimpleStringPBEConfig(salt));
//        pooledPBEStringEncryptor.setConfig(jasyptStringEncryptor(salt));
        String result = isEncrypt ? pooledPBEStringEncryptor.encrypt(password) : pooledPBEStringEncryptor.decrypt(password);
        return result;
    }

    /**
     * 设置 {@link PBEConfig} 配置对象，SimpleStringPBEConfig 是它的实现类
     * 1、所有的配置项建议与全局配置文件中的配置项保持一致，特别是 password、algorithm 等等选项，如果不一致，则应用启动时解密失败而报错.
     * 2、setPassword(final String password)：设置加密密钥，必须与全局配置文件中配置的保存一致，否则应用启动时会解密失败而报错.
     * 3、setPoolSize(final String poolSize)：设置要创建的加密程序池的大小.
     * 4、setAlgorithm(final String algorithm): 设置加密算法的值， 此算法必须由 JCE 提供程序支持
     * 5、setKeyObtentionIterations: 设置应用于获取加密密钥的哈希迭代次数。
     * 6、setProviderName(final String providerName)：设置要请求加密算法的安全提供程序的名称
     * 7、setSaltGeneratorClassName：设置 Sal 发生器
     * 8、setIvGeneratorClassName：设置 IV 发生器
     * 9、setStringOutputType：设置字符串输出的编码形式。可用的编码类型有 base64、hexadecimal
     *
     * @param salt
     * @return
     */
    private static SimpleStringPBEConfig getSimpleStringPBEConfig(String salt) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(salt);
        config.setPoolSize("1");
//        config.setAlgorithm("PBEWithMD5AndDES");
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        return config;
    }

    public static SimpleStringPBEConfig jasyptStringEncryptor(String secretKey) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(secretKey);
        config.setAlgorithm("PBEWithMD5AndDES");
//        config.setKeyObtentionIterations("1000");
        config.setPoolSize("4");
//        config.setProviderName("SunJCE");
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
//        config.setStringOutputType("base64");
        return config;
    }


    public static void main(String[] args) throws Exception {
        String salt = "salt";
        String password = "12345";

        //一个同样的密码和秘钥，每次执行加密，密文都是不一样的。但是解密是没问题的。
        String jasyptEncrypt = stringEncryptor(salt, password, true);
        System.out.println(jasyptEncrypt);

        String jasyptDecrypt = stringEncryptor(salt, jasyptEncrypt, false);
        System.out.println(jasyptDecrypt);
    }
}