package com.slotify.backend.spring.config;

import io.awspring.cloud.s3.S3ObjectConverter;
import io.awspring.cloud.s3.S3OutputStreamProvider;
import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Slf4j
public class S3Config {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(
            @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey,
            @Value("${spring.cloud.aws.credentials.session-token:#{null}}") String sessionToken
    ) {

        return StaticCredentialsProvider.create(
                AwsSessionCredentials.create(accessKey, secretKey, sessionToken)
        );
    }
    @Bean
    public S3Client s3Client(
            AwsCredentialsProvider provider,
            @Value("${spring.cloud.aws.s3.region:us-east-1}") String region
    ) {

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(
            AwsCredentialsProvider provider,
            @Value("${spring.cloud.aws.s3.region:us-east-1}") String region
    ) {

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }

    @Bean
    public S3Template s3Template(
            S3Client s3Client,
            S3OutputStreamProvider outputStreamProvider,
            S3ObjectConverter objectConverter,
            S3Presigner s3Presigner
    ) {

        return new S3Template(s3Client, outputStreamProvider, objectConverter, s3Presigner);
    }
}
