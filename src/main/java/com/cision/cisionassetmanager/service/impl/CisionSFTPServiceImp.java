package com.cision.cisionassetmanager.service.impl;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.cision.cisionassetmanager.service.CisionSFTPService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.List;



@Service
@Slf4j
public class CisionSFTPServiceImp implements CisionSFTPService {

    @Value("${cloud.aws.s3.bucket.name}")
    String bucketName;

    @Autowired
    AmazonS3 s3Client;

    public void readAssets() {
        ObjectListing objectListing =  s3Client.listObjects(bucketName);
        List<S3ObjectSummary> s3ObjectSummaryList = objectListing.getObjectSummaries();
        if(!CollectionUtils.isEmpty(s3ObjectSummaryList)){
            s3ObjectSummaryList.forEach(CisionSFTPServiceImp::logFileStats);
        }
    }

    private static void logFileStats(S3ObjectSummary s3ObjectSummary) {
        try {
            if (s3ObjectSummary.getKey().toLowerCase().endsWith(".pdf") || s3ObjectSummary.getKey().toLowerCase().endsWith(".xml")) {
                long fileSizeInMegaBytes = ((s3ObjectSummary.getSize()) / 1024) / 1024;
                if (fileSizeInMegaBytes < 10)
                    log.info("File Details:: {}", s3ObjectSummary);
                else
                    throw new Exception("File size exceeds more then 10 MB");
            } else {
                throw new Exception("FileFormat not supported");
            }
        } catch (Exception e) {
            log.error("File Details: {}, errorMessage: {} ", s3ObjectSummary, e.getMessage());
        }
    }
}
