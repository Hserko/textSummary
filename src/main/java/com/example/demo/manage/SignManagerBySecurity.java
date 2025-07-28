package com.example.demo.manage;


import cn.hutool.core.util.ObjectUtil;
import com.example.demo.config.SignatureConfig;
import com.example.demo.play.SHA256withRSAExample;
import com.example.demo.tool.PemKeyReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@ConditionalOnClass(SignatureConfig.class)
@Slf4j
@Component
public class SignManagerBySecurity {



    private final SignatureConfig signatureParams;


    public SignManagerBySecurity(SignatureConfig signatureConfig){
        this.signatureParams = signatureConfig;
        loadKey();
    }

    public boolean verifySignature(String clientId, String rawData, String signature) throws Exception {
        SignatureConfig.KeyPairProps keyPair = signatureParams.getKeyPair().get(clientId);
        Signature sign = Signature.getInstance(keyPair.getAlgorithm());
        sign.initVerify(publicKeyFromBase64(keyPair.getPublicKey()));
        sign.update(rawData.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = Base64.getUrlDecoder().decode(signature);
        return sign.verify(signatureBytes);
    }

    public String getSignature(String clientId, String rawData) throws Exception {
        SignatureConfig.KeyPairProps keyPair = signatureParams.getKeyPair().get(clientId);
        Signature sign = Signature.getInstance(keyPair.getAlgorithm());
        sign.initSign(privateKeyFromBase64(keyPair.getPrivateKey()));
        sign.update(rawData.getBytes(StandardCharsets.UTF_8));
        byte[] signBytes = sign.sign();
        return Base64.getEncoder().encodeToString(signBytes);
    }

    private void loadKey(){
        signatureParams.getKeyPair().forEach(
                (key, keyPairProps) -> {
                    try {
                        keyPairProps.setPublicKeyPath(PemKeyReader.extractPublicKey(keyPairProps.getPublicKeyPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        keyPairProps.setPrivateKey(PemKeyReader.extractPrivateKey(keyPairProps.getPrivateKeyPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(ObjectUtil.isEmpty(keyPairProps.getPublicKey())||ObjectUtil.isEmpty(keyPairProps.getPrivateKey()))
                        throw new RuntimeException("载入密钥文件失败");
                }
        );

    }

    /**
     * 从Base64编码字符串恢复私钥
     */
    public static PrivateKey privateKeyFromBase64(String privateKeyBase64) throws Exception {
        return SHA256withRSAExample.privateKeyFromBase64(privateKeyBase64);
    }

    /**
     * 从Base64编码字符串恢复公钥
     */
    public static PublicKey publicKeyFromBase64(String publicKeyBase64) throws Exception {
        return SHA256withRSAExample.publicKeyFromBase64(publicKeyBase64);
    }

}
