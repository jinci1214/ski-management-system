package com.ski.ticketing.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.ski.ticketing.config.AliOSSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class AliOSSUtil {

    @Autowired
    private AliOSSConfig aliOSSConfig;

    public String upload(MultipartFile file) throws IOException {
        checkConfiguration();
        // 1. 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        // 2. 提取文件扩展名（如 .jpg, .png）
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 3. 生成 UUID + 扩展名 作为新文件名
        String fileName = UUID.randomUUID().toString() + extension;
        // 4. 上传到 OSS
        OSS ossClient = createClient();
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    aliOSSConfig.getBucketName(), fileName, inputStream);
            ossClient.putObject(putObjectRequest);

            return "https://" + aliOSSConfig.getBucketName() + "." + aliOSSConfig.getEndpoint() + "/" + fileName;
        } catch (OSSException e) {
            throw new RuntimeException("上传文件到OSS失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    public void delete(String fileName) {
        checkConfiguration();
        OSS ossClient = createClient();
        try {
            ossClient.deleteObject(aliOSSConfig.getBucketName(), fileName);
        } catch (OSSException e) {
            throw new RuntimeException("删除OSS文件失败", e);
        } finally {
            ossClient.shutdown();
        }
    }

    private OSS createClient() {
        return new OSSClientBuilder().build(
                aliOSSConfig.getEndpoint(),
                aliOSSConfig.getAccessKeyId(),
                aliOSSConfig.getAccessKeySecret());
    }

    private void checkConfiguration() {
        if (aliOSSConfig.getAccessKeyId() == null || aliOSSConfig.getAccessKeyId().isBlank()
                || aliOSSConfig.getAccessKeySecret() == null || aliOSSConfig.getAccessKeySecret().isBlank()) {
            throw new IllegalStateException("未配置阿里云 OSS 密钥，当前不能使用图片上传功能");
        }
    }
}
