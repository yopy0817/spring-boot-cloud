package com.coding404.demo.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;



@Configuration
public class AWSConfig {

	private String aws_access_key_id = "어세스키";
	private String aws_secret_access_key = "시크릿키";
    /*
	@Bean
	public S3Client s3() {
		
		//자격증명을 생성하는 객체입니다.
		AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);

		//S3를 핸들링 하기위한 S3Client를 생성합니다.
		S3Client s3 = S3Client.builder()
				.region(Region.AP_NORTHEAST_2)//서울 리전
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.build();
		
		return s3;
	}
	
	@Bean
	public LambdaClient awsLambda() {
		//자격증명을 생성하는 객체입니다.
		AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);

		//S3를 핸들링 하기위한 S3Client를 생성합니다.
		LambdaClient awsLambda = LambdaClient.builder()
				.region(Region.AP_NORTHEAST_2)//서울 리전
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.build();
		
		return awsLambda;
	}
	*/
	
}
