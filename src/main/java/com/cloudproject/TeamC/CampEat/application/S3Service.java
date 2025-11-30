package com.cloudproject.TeamC.CampEat.application;

import com.cloudproject.TeamC.global.common.code.ErrorCode;
import com.cloudproject.TeamC.global.exception.BusinessException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + extension;

        try (InputStream inputStream = file.getInputStream()) {
            // S3에 업로드
            s3Template.upload(bucket, s3FileName, inputStream, ObjectMetadata.builder().contentType(file.getContentType()).build());

            // 업로드된 파일의 URL 반환
            return String.format("https://%s.s3.amazonaws.com/%s", bucket, s3FileName);

        } catch (IOException e) {
            log.error("S3 Upload Error: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
