package com.frankcooper.common;

import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@EnableEncryptableProperties
//@PropertySource(value = {"classpath:security.properties"},ignoreResourceNotFound = false)
//@PropertySource(name = "EncryptedProperties", value = {"classpath:application.yml", "classpath:application-dev.yml", "classpath:application-test.yml", "classpath:application-prod.yml"})
@Configuration
public class JasyptConfig {

    //    @Bean("encryptorBean")
    public static SimpleStringPBEConfig jasyptStringEncryptor() {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("");
        config.setAlgorithm("PBEWithMD5AndDES");
//        config.setKeyObtentionIterations("1000");
        config.setPoolSize("4");
//        config.setProviderName("SunJCE");
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
//        config.setStringOutputType("base64");
        return config;
    }

    @Bean("encryptorBean")
    public SimpleStringPBEConfig getSimpleStringPBEConfig() {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("");
        config.setPoolSize("1");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        return config;
    }

}
