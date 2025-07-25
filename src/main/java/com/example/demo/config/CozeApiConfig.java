package com.example.demo.config;

import com.coze.openapi.client.auth.OAuthToken;
import com.coze.openapi.service.auth.JWTOAuth;
import com.coze.openapi.service.auth.JWTOAuthClient;
import com.coze.openapi.service.config.Consts;
import com.coze.openapi.service.service.CozeAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@Slf4j
//@EnableScheduling
public class CozeApiConfig {

    @Value("${coze.jwt.oauth.client.id}")
    private String COZE_JWT_OAUTH_CLIENT_ID; // 创建JWT类型OAuth应用时平台生成的唯一标识符

    @Value("${coze.jwt.oauth.private.key}")
    private String COZE_JWT_OAUTH_PRIVATE_KEY; // 用于签署JWT，可以在 OAuth 应用页面找到这个应用，在操作列单击编辑图标，进入配置页面下载私钥文件

    @Value("${coze.jwt.oauth.private.filePath}")
    private String COZE_JWT_OAUTH_PRIVATE_KEY_FILE_PATH; // 私钥文件在本地的存储路径，开发者自行指定

    @Value("${coze.jwt.oauth.public.key_id}")
    private String COZE_JWT_OAUTH_PUBLIC_KEY_ID; // 从环境变量获取 OAuth 应用的公钥ID，可以在 OAuth 应用页面找到这个应用，在操作列单击编辑图标，进入配置页面查看公钥指纹。


    private final ResourceLoader resourceLoader;

    private JWTOAuthClient oauth;

    public CozeApiConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public CozeAPI OauthCozeApi(){
       initOAuthClient();

       refreshToken();

       return new CozeAPI.Builder()
                .readTimeout(50_000)
                .auth(new JWTOAuth(oauth))
                .baseURL(Consts.COZE_CN_BASE_URL)
                .build();
    }

    private void initOAuthClient() {
        String jwtOauthClientID = COZE_JWT_OAUTH_CLIENT_ID;
        String jwtOauthPrivateKey = COZE_JWT_OAUTH_PRIVATE_KEY;
        String jwtOauthPrivateKeyFilePath = COZE_JWT_OAUTH_PRIVATE_KEY_FILE_PATH;
        String jwtOauthPublicKeyID = COZE_JWT_OAUTH_PUBLIC_KEY_ID;

        if (!jwtOauthPrivateKeyFilePath.isEmpty()) {
            try {
                Resource resource = resourceLoader.getResource(jwtOauthPrivateKeyFilePath);
                if (!resource.exists()) {
                    throw new IllegalArgumentException("私有密钥文件不存在: " + jwtOauthPrivateKeyFilePath);
                }
                try (InputStream is = resource.getInputStream();
                     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                     BufferedReader reader = new BufferedReader(isr)) {

                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    jwtOauthPrivateKey = content.toString();
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("读取私有密钥文件失败", e);
            }
        }

        try {
            oauth = new JWTOAuthClient.JWTOAuthBuilder()
                    .clientID(jwtOauthClientID)
                    .privateKey(jwtOauthPrivateKey)
                    .publicKey(jwtOauthPublicKeyID)
                    .baseURL(Consts.COZE_CN_BASE_URL)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("初始化JWTOAuthClient失败", e);
        }
    }


    // 每13分钟刷新一次令牌（比15分钟提前，确保令牌始终有效）
//    @Scheduled(fixedRate = 13 * 60 * 1000)
    public void refreshToken() {
        if (oauth== null) {
            initOAuthClient();
        }

        try {
            OAuthToken token = oauth.getAccessToken();
            log.info("定时刷新了CozeAPI访问令牌");
        } catch (Exception e) {
            log.error("刷新CozeAPI访问令牌失败", e);
            // 可以添加重试逻辑或告警机制
        }
    }
}
