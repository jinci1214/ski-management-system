package com.ski.ticketing.controller.admin;

import com.ski.ticketing.result.Result;
import com.ski.ticketing.utils.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/upload")
@Slf4j
public class UploadController {

    @Autowired
    private AliOSSUtil aliOSSUtil;

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error("请选择图片文件");
        if (file.getSize() > 5 * 1024 * 1024) return Result.error("图片不能超过5MB");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return Result.error("只能上传图片文件");
        String url = aliOSSUtil.upload(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }
}
