package com.coding404.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coding404.demo.aws.s3.S3Service;
import com.coding404.demo.aws.s3.SesService;

import software.amazon.awssdk.services.sqs.model.Message;

@RestController
public class CloudRestController {

//	@Autowired
//	private SesService sesService;
	
	@Autowired
	private S3Service s3Service;

	//////////////////////////cloudeupload//////////////////////////////
	@PostMapping("/cloudUpload")
	public String cloudUpload(@RequestParam("file_data")  MultipartFile file) {

		try {
			//IE, Edge는 전체경로가 들어오므로 \\기준으로 파일명만 추출
			String originName = file.getOriginalFilename();
			// 서버에서 저장 할 파일 이름
			// 파일이름은 버킷에서 중복을 피하려면 UUID 랜덤값으로 올려줍니다.
			String objectKey =  originName.substring( originName.lastIndexOf("\\") + 1);
			// 서버에 올릴 바이트 데이터
			byte[] objectData = file.getBytes();
			
			s3Service.putS3Object(objectKey, objectData);
			
		} catch (Exception e) {
			System.out.println("업로드중 에러발생:" + e.getMessage());
		}
		
		return "(버킷업로드)클라이언트로 결과처리는 여러분이 하세요";
	}
	
	@GetMapping("/list_bucket_objects")
	public String list_bucket_objects() {
		
		s3Service.listBucketObjects();
		
		return "(버킷객체목록조회)클라이언트로 결과처리는 여러분이 하세요";
	}
	
	
	@DeleteMapping("/delete_bucket_objects")
	public String delete_bucket_objects(@RequestParam("bucket_obj_name") String bucket_obj_name) {

		s3Service.deleteBucketObjects(bucket_obj_name);
		
		return "(버킷객체목록삭제)클라이언트로 처리결과는 여러분이 하세요";
	}
	
	
	//////////////////////////여기서부터는 lambda사용입니다/////////////////////////
	@GetMapping("/lambda_call")
	public String lambda_call() {

		s3Service.invokeFunction();
		
		return "(람다함수호출)클라이언트로 처리결과는 여러분이 하세요";
	}
	
	@GetMapping("/lambda_call_step1")
	public String lambda_call_step1() {
		
		//클라이언트에서 받은 파일명으로 대체 될 수 있습니다.
		String file_name = "20211130주택도시보증공사_전국_민간아파트_분양가격동향.csv";
		
		return s3Service.lambdaCallStep1(file_name);
	}
	
	@GetMapping("/lambda_call_step2")
	public String lambda_call_step1(@RequestParam("file_name") String file_name) {

		return s3Service.lambdaCallStep2(file_name);
	}

    //////////////////////////ses////////////////////////////////
    /*
    @GetMapping("/sendEmail")
    public String sendEmail() {

        //너희가 클라이언트에서 받은값 전달해라
        String sender = "yopy0817@gmail.com";
        String recipient = "yopy0817@naver.com";
        String subject = "Amazon SES test (AWS SDK for Java)";
        String HTMLBODY = "<h1>Amazon SES test (AWS SDK for Java)</h1>"
                + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
                + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
                + "AWS SDK for Java</a>";


        sesService.send(sender, recipient, subject, HTMLBODY);

        return "(이메일전송완료)";
    }


    //////////////////////////sns////////////////////////////////
    @GetMapping("/sendSns")
    public String sendSns() {

        //insert를 처리..(생략)
        //sns알림서비스로 연결
        sesService.sendSns();

        //sns는 sqs로 메시지를 전송했음을 의미합니다.
        //sqs에서는 폴링으로 메시지를 받아봅니다.

        return "(SNS전송완료)";
    }

    //////////////////////////sqs////////////////////////////////
    @GetMapping("/pollSqs")
    public String pollSqs() {

        List<Message> list = sesService.pollSqs();

        for(Message m : list ) {
            System.out.println(m);
        }
        return "(SQS풀링완료)";
    }
    */



}
