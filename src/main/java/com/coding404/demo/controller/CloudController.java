package com.coding404.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.coding404.demo.aws.s3.S3Service;

@Controller
public class CloudController {
	

	@Autowired
	private S3Service s3Service;
	
	@GetMapping("/main")
	public String main() {
		
		return "main";
	}
	
	@GetMapping("/S3Request")
	public String S3Request() {
		
		s3Service.getBucketList();
		
		return "redirect:/main";
	}
	
	@GetMapping("/ses")
	public String ses() {
		
		return "ses";
	}
	
	
	
	
}
