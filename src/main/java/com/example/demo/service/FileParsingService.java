package com.example.demo.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileParsingService {

    @Resource
    private Tika tika;

    @Value("${tika.maxFileSize}")
    private long maxFileSize;


    public String DocumentParse(MultipartFile file){
        if(file == null || file.isEmpty())throw new IllegalArgumentException("文件不存在");

        if(file.getSize()>maxFileSize)throw new IllegalArgumentException("文件过大");

        try(InputStream inputStream = file.getInputStream()){
            return tika.parseToString(inputStream);
        } catch (TikaException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
