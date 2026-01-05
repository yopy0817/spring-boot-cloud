package com.coding404.demo.aws.s3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;


@Component //스프링 빈으로 생성
public class S3Service {
    //자격증명 객체를 주입받으려면
//	@Autowired
//	private LambdaClient awsLambda;

	private String bucketName = "demo-coding404-bucket";

	//버킷목록을 조회합니다.
	public void getBucketList() {

        //staticCredential자격증명을 사용하는경우 예제
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(어세스키, 시크릿키);
//        S3Client s3 = S3Client.builder()
//                .region(Region.AP_NORTHEAST_2)//서울 리전
//                .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .build();

		//.aws폴더 밑에 외부파일로 자격증명처리
		S3Client s3 = S3Client.builder()
		.region(Region.AP_NORTHEAST_2)//서울 리전
		.credentialsProvider(ProfileCredentialsProvider.create())
		.build();

		System.out.println("S3객체성공(스프링빈일 경우 싱글톤):" + s3);

		ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
		ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
		listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));

		//화면에서 접근에 성공하는지 확인합니다.
	}

	//버킷에 업로드를 처리합니다.
	public int putS3Object(String objectKey, /*String objectPath*/ byte[] objectData) {

        S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_2)//서울 리전
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();


		int result = 404;

		try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .metadata(metadata)
                .build();

            //로컬의 파일 데이터를 업로드시 사용합니다.
            //s3.putObject(putOb, RequestBody.fromFile(new File(objectPath)));

            //멀티파트객체의 byte데이터를 업로드시 사용합니다.
            PutObjectResponse response  = s3.putObject(putOb, RequestBody.fromBytes(objectData) );
            //결과 받기
            result = response.sdkHttpResponse().statusCode();

            System.out.println("Successfully placed " + objectKey +" into bucket "+bucketName);
            System.out.println("업로드결과:" + result );

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

		return result;
	}

	//버킷의 객체 목록을 확인합니다.
	public void listBucketObjects(/*필요에 따라 컨트롤러에서 매개변수를 받음*/) {

        S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_2)//서울 리전
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();

            //조회한 목록을 출력합니다.
            for (S3Object myValue : objects) {
                System.out.print("\n The name of the key is " + myValue.key());
                System.out.print("\n The object is " + myValue.size() + " bite");
                System.out.print("\n The owner is " + myValue.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


	public void deleteBucketObjects(String bucket_obj_name /*필요에 따라 컨트롤러에서 매개변수를 받음*/) {

        S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_2)//서울 리전
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        ArrayList<ObjectIdentifier> keys = new ArrayList<>();
		//삭제할 객체명을 지정하는 객체
		ObjectIdentifier objectId = ObjectIdentifier.builder()
                .key(bucket_obj_name) //버킷에서 삭제할 객체명을 넣습니다.
                .build();

		keys.add(objectId);

		//Delete객체에 컬렉션을 추가
		//objects함수의 ObjectIdentifier타입만 들어가면 단일삭제 입니다
		Delete del = Delete.builder()
				.objects(keys) //컬렉션을 넣습니다.
				.build();

		try {
			DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
					.bucket(bucketName)
					.delete(del)
					.build();

			//S3에 키가 없더라도 삭제는 200으로 나옵니다.
			DeleteObjectsResponse delete =s3.deleteObjects(multiObjectDeleteRequest);
			System.out.println(delete.sdkHttpResponse().statusCode());

			System.out.println("Multiple objects are deleted!");

		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}


	//람다 호출하기
	public void invokeFunction() {
		//staticCredential을 사용하는 경우
//		AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
//		LambdaClient awsLambda = LambdaClient.builder()
//				.region(Region.AP_NORTHEAST_2)//서울 리전
//				.credentialsProvider(StaticCredentialsProvider.create(credentials))
//				.build();

        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2)//서울 리전
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        //실행시킬 람다 함수명
		String functionName = "spring-lambda-test";

		InvokeResponse res = null ;
		try {
			//Need a SdkBytes instance for the payload
			String json = "{\"Hello \":\"자바에서 보낸 매개변수\"}";
			SdkBytes payload = SdkBytes.fromUtf8String(json) ;

			//Setup an InvokeRequest
			InvokeRequest request = InvokeRequest.builder()
					.functionName(functionName) //실행시킬 람다함수의 이름이 들어갑니다.
					.payload(payload)
					.build();

			res = awsLambda.invoke(request);
			String value = res.payload().asUtf8String() ;

			//value값이 한글이라면 JSONObject객체 or GSon을 사용해서 복원해야 합니다.
			System.out.println(value);

		} catch(LambdaException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	//람다 호출하기(데이터 전처리)
	public String lambdaCallStep1(String file_name) {

        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2)//서울 리전
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();


		//실행시킬 람다 함수명
		String functionName = "spring-python-data-step1";

		//리턴값
		String value = null;

		InvokeResponse res = null ;
		try {
			//Need a SdkBytes instance for the payload
			String json = "{\"file_name\":\"" + file_name + "\"}";
			SdkBytes payload = SdkBytes.fromUtf8String(json) ;

			//Setup an InvokeRequest
			InvokeRequest request = InvokeRequest.builder()
					.functionName(functionName) //실행시킬 람다함수의 이름이 들어갑니다.
					.payload(payload)
					.build();

			res = awsLambda.invoke(request);
			//람다의 리턴값
			value = res.payload().asUtf8String() ;

			System.out.println(value);

		} catch(LambdaException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		return value;

	}



	//람다 호출하기(데이터 시각화)
	public String lambdaCallStep2(String file_name) {

        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2)//서울 리전
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

		//실행시킬 람다 함수명
		String functionName = "spring-python-data-step2";

		//리턴값
		String value = null;

		InvokeResponse res = null ;
		try {
			//Need a SdkBytes instance for the payload
			String json = "{\"file_name\":\"" + file_name + "\"}";
			SdkBytes payload = SdkBytes.fromUtf8String(json) ;

			//Setup an InvokeRequest
			InvokeRequest request = InvokeRequest.builder()
					.functionName(functionName) //실행시킬 람다함수의 이름이 들어갑니다.
					.payload(payload)
					.build();

			res = awsLambda.invoke(request);
			//람다의 리턴값
			value = res.payload().asUtf8String() ;

			System.out.println(value);

		} catch(LambdaException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		return value;

	}



}
