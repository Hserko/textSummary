package com.example.demo.config;


import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;




@Configuration
public class DocumentParserConfig {

    private final ResourceLoader resourceLoader;

    @Value("${tika.xml}")
    private String configPath;

    public DocumentParserConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public Tika tikaInstance() {
        try{
            Resource resource = resourceLoader.getResource(configPath);
            if(resource.exists()){
                return new Tika(new TikaConfig(resource.getInputStream()));
            }
            else{
                return new Tika(TikaConfig.getDefaultConfig());
            }

        }catch (Exception e){
            System.err.println("初始化 Tika 时发生异常，将使用默认配置: " + e.getMessage());
            return new Tika(TikaConfig.getDefaultConfig());
        }
    }
}
