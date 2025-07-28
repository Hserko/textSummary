package com.example.demo.config;


import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Data
@ConditionalOnProperty(value = "secure.signature.enable", havingValue = "true") //根据条件注入bean
@Component
@ConfigurationProperties(prefix = "secure.signature")
public class SignatureConfig {
    private Boolean enable;

    HashMap<String, KeyPairProps> keyPair = new HashMap<>();

    @Data
    public static class KeyPairProps {
        private String algorithm;
        private String publicKey;
        private String publicKeyPath;
        private String privateKey;
        private String privateKeyPath;
    }
}
