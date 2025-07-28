package com.example.demo.tool;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PemKeyReader {

    /**
     * 从PEM文件中提取私钥字符串（Base64编码）
     * 支持PKCS#8格式（BEGIN PRIVATE KEY）和PKCS#1格式（BEGIN RSA PRIVATE KEY）
     */
    public static String extractPrivateKey(String filePath) throws IOException {
        String content = readFileContent(filePath);

        // 尝试提取PKCS#8格式私钥
        String privateKey = extractKeyContent(content,
                "-----BEGIN PRIVATE KEY-----",
                "-----END PRIVATE KEY-----");

        if (privateKey != null) {
            return privateKey;
        }

        // 尝试提取PKCS#1格式私钥
        privateKey = extractKeyContent(content,
                "-----BEGIN RSA PRIVATE KEY-----",
                "-----END RSA PRIVATE KEY-----");

        if (privateKey != null) {
            return privateKey;
        }

        throw new IllegalArgumentException("未找到有效的私钥内容");
    }

    /**
     * 从PEM文件中提取公钥字符串（Base64编码）
     */
    public static String extractPublicKey(String filePath) throws IOException {
        String content = readFileContent(filePath);

        // 提取公钥
        String publicKey = extractKeyContent(content,
                "-----BEGIN PUBLIC KEY-----",
                "-----END PUBLIC KEY-----");

        if (publicKey != null) {
            return publicKey;
        }

        throw new IllegalArgumentException("未找到有效的公钥内容");
    }

    /**
     * 读取文件内容
     */
    private static String readFileContent(String filePath) throws IOException {
            if(ObjectUtil.isEmpty(filePath)) throw new IllegalArgumentException("文件路劲参数不存在");
            return IoUtil.readUtf8(ResourceUtil.getStream(filePath));
    }

    /**
     * 从PEM格式内容中提取密钥的Base64编码部分
     */
    private static String extractKeyContent(String pemContent, String beginMarker, String endMarker) {
        Pattern pattern = Pattern.compile(
                Pattern.quote(beginMarker) +
                        "(.*?)" +
                        Pattern.quote(endMarker),
                Pattern.DOTALL);

        Matcher matcher = pattern.matcher(pemContent);
        if (matcher.find()) {
            return matcher.group(1)
                    .replaceAll("\\s+", "")
                    .trim();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            // 示例：提取私钥
            String privateKeyPath = "path/to/private_key.pem";
            String privateKey = extractPrivateKey(privateKeyPath);
            System.out.println("提取的私钥(Base64):\n" + privateKey);

            // 示例：提取公钥
            String publicKeyPath = "path/to/public_key.pem";
            String publicKey = extractPublicKey(publicKeyPath);
            System.out.println("\n提取的公钥(Base64):\n" + publicKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}