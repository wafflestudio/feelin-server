package com.wafflestudio.msns.global.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@Configuration
class AWSConfig {
    @Bean
    fun assetSNSClient(
        @Value("\${cloud.aws.credentials.accessKey}") accessKey: String,
        @Value("\${cloud.aws.credentials.secretKey}") secretKey: String

    ): AmazonSNS {
        return AmazonSNSClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withRegion(Regions.AP_NORTHEAST_1)
            .build()
    }
}
