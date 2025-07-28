package com.example.demo.manage;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.asymmetric.Sign;
import com.example.demo.config.SignatureConfig;
import com.example.demo.tool.PemKeyReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@ConditionalOnBean(SignatureConfig.class)
@Component
public class SignatureManager {

    private final SignatureConfig signaturePrams;

    public SignatureManager(SignatureConfig signatureConfig){
        this.signaturePrams = signatureConfig;
        loadKeyPairByPath();
    }

    /**
     * 验证签名
     * @param clientId
     * @param rawData
     * @param signature
     * @return
     */
    public Boolean verifySignature(String clientId, String rawData, String signature){
        Sign sign = getSign(clientId);
        if(ObjectUtil.isEmpty(sign))throw new RuntimeException("获取签名失败");
        return sign.verify(rawData.getBytes(StandardCharsets.UTF_8), Base64.getUrlDecoder().decode(signature));
    }


    /**
     * 生成签名
     *
     * @param clientId
     * @param rawData
     * @return
     */
    public String getSignature(String clientId, String rawData){
        Sign sign = getSign(clientId);
        if(ObjectUtil.isEmpty(sign)) throw new RuntimeException("获取签名失败");
        return Base64.getEncoder().encodeToString(sign.sign(rawData));
    }

    public SignatureConfig.KeyPairProps getKeyPairByClientId(String clientId){
            if(ObjectUtil.isEmpty(clientId))throw new RuntimeException("无客户端id");
            return signaturePrams.getKeyPair().get(clientId);
    }

    /**
     * 获取hutool中的sign bean
     * @param cilentId
     * @return
     */
    private Sign getSign(String cilentId){
        SignatureConfig.KeyPairProps keyPair = getKeyPairByClientId(cilentId);
        if(ObjectUtil.isEmpty(keyPair))throw new RuntimeException("无效的调用");
        return new Sign(keyPair.getAlgorithm(), keyPair.getPrivateKey(),keyPair.getPublicKey());
    }

    private void loadKeyPairByPath(){
        signaturePrams.getKeyPair().
                forEach((key, keyPairProps) -> {
                    try {
                        keyPairProps.setPublicKey(PemKeyReader.extractPublicKey(keyPairProps.getPublicKeyPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        keyPairProps.setPrivateKey(PemKeyReader.extractPrivateKey(keyPairProps.getPrivateKeyPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(ObjectUtil.isEmpty(keyPairProps.getPrivateKey())||ObjectUtil.isEmpty(keyPairProps.getPublicKey()))
                        throw new RuntimeException("签名私钥或者公钥文件不存在");
                });
    }


}
