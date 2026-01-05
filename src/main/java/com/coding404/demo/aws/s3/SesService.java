package com.coding404.demo.aws.s3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.awspring.cloud.sqs.annotation.SqsListener;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

//@Component
public class SesService {
    /*
	@Value("${aws_access_key_id}")
	private String aws_access_key_id;

	@Value("${aws_secret_access_key}")
	private String aws_secret_access_key;

	
	//ses로 메일보내기
	//공식문서
	//https://docs.aws.amazon.com/ko_kr/ses/latest/dg/example_sesv2_SendEmail_section.html
	//git예제
	//https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ses/src/main/java/com/example/sesv2/SendEmail.java
	public void send(String sender, //보내는사람
  			 	  	String recipient, //받는사람
	  				String subject, //제목
	  				String bodyHTML) { //내용
		
		//자격증명을 생성하는 객체입니다.
		AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
		
        SesV2Client client = SesV2Client.builder()
                .region(Region.AP_NORTHEAST_2 )
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
		
		//임포트 - sesv2패키지
		
		 Destination destination = Destination.builder()
	                .toAddresses(recipient)
	                .build();

	            Content content = Content.builder()
	                .data(bodyHTML)
	                .build();

	            Content sub = Content.builder()
	                .data(subject)
	                .build();

	            Body body = Body.builder()
	                .html(content)
	                .build();

	            Message msg = Message.builder()
	                .subject(sub)
	                .body(body)
	                .build();

	            EmailContent emailContent = EmailContent.builder()
	                .simple(msg)
	                 .build();

	            SendEmailRequest emailRequest = SendEmailRequest.builder()
	                .destination(destination)
	                .content(emailContent)
	                .fromEmailAddress(sender)
	                .build();

	            try {
	                System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
	                client.sendEmail(emailRequest);
	                System.out.println("email was sent");

	            } catch (SesV2Exception e) {
	                System.err.println(e.awsErrorDetails().errorMessage());
	                //System.exit(1);
	            }
		
		
	}
	
	
	//sns로 메시지게시
	//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-simple-notification-service.html#sns-publish-message-topic
	//https://docs.aws.amazon.com/sns/latest/dg/example_sns_PublishFifoTopic_section.html
	public void sendSns() {
		
		//메시지Queue의 URL을 얻는 방법도 있습니다.
        //String fifoTopicName = "DemoTopic.fifo"; //fifo토픽주제
        
        String topicArn = "arn:aws:sns:ap-northeast-2:944886541504:DemoTopic.fifo"; //구독주제
        
        //자격증명을 생성하는 객체입니다.
		AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
        
        SnsClient snsClient = SnsClient.builder()
            .region(Region.AP_NORTHEAST_2) //서울
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
        
        try {

            // Compose and publish a message that updates the wholesale price.
            String subject = "홍길동님의 아이템 주문내역입니다.";
            String payload = "{\"product\": 214, \"price\": 79.99}";
            String groupId = "PID-214";
            String dedupId = UUID.randomUUID().toString();
            String attributeName = "business";
            String attributeValue = "wholesale";
            
            //sns패키지 임포트
            MessageAttributeValue msgAttValue = MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(attributeValue)
                    .build();
            
            Map<String, MessageAttributeValue> attributes = new HashMap<>();
            attributes.put(attributeName, msgAttValue);
            PublishRequest pubRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .subject(subject)
                .message(payload)
                .messageGroupId(groupId)
                .messageDeduplicationId(dedupId)
                .messageAttributes(attributes)
                .build();
            
            snsClient.publish(pubRequest);
            System.out.println("Message was published to "+topicArn);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}
	
	
	//sqs에서 메시지폴링
	//git(리시브 메시지 부분)
	//https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/sqs/src/main/java/com/example/sqs/SQSExample.java
	public List<software.amazon.awssdk.services.sqs.model.Message> pollSqs() {

        String queueUrl = "https://sqs.ap-northeast-2.amazonaws.com/944886541504/DemoQueue.fifo"; //SQS대기열 URL주소
        
        //자격증명을 생성하는 객체입니다.
		AwsBasicCredentials credentials = AwsBasicCredentials.create(aws_access_key_id, aws_secret_access_key);
		
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
		
        
		System.out.println("\nReceive messages");
        try {
            // snippet-start:[sqs.java2.sqs_example.retrieve_messages]
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl) //SQS대기열 URL주소
                .maxNumberOfMessages(5) //가져올 메시지 개수
                .build();
            
            //import는 sqs메시지 타입입니다.
            List<software.amazon.awssdk.services.sqs.model.Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            
            return messages;

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            //System.exit(1);
        }
        
        return null;
	}
	
	
	
	//spring-cloud-aws를 활용한 SQS메시지 폴링
    @SqsListener("${aws_sqs_url}")
    public void listen(String message) {
    	
    	System.out.println("========sqsListener실행됨======");
    	System.out.println("========메시지 수신후, 중복수신을 방지하기 위해 메시지는 자동삭제됩니다.======");
    	System.out.println("========@리스너 어노테이션은 다양한 인수사용이 가능합니다. 공식문서를 확인하세요======");
        System.out.println(message);
    }
	
	*/
	
	
	
	
	
	
	
}

