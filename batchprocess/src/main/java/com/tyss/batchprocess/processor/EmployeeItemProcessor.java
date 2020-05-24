package com.tyss.batchprocess.processor;

import java.time.Instant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.tyss.batchprocess.dto.EmployeeBean;
import com.tyss.batchprocess.dto.SmsAndEmailResponse;

import lombok.extern.java.Log;
/**
 * 
 * This class processes the data which is in the form of Bean object  
 * and returns the processed data in the form of Bean object
 */
@Log
public class EmployeeItemProcessor implements ItemProcessor<EmployeeBean, EmployeeBean> {
	
	
	@Value("${from.mailid}")
	private String from;
	
	@Value("${cc.mailid}")
	private String ccs;
	
	@Value("${emailservice.url}")
	private String emailServiceUrl;

	@Override
	public EmployeeBean process(EmployeeBean employee) throws Exception {
		
		System.out.println("************************Processor***************************");
		System.out.println("employee mail-id"+employee.getMailId());
		log.info("MAILID "+employee.getMailId()+" DOJ " + employee.getDateOfJoin());
		log.info("MAILID "+employee.getMailId()+" DOB " + employee.getDateOfBirth());

		RestTemplate restTemplate = new RestTemplate();

		// converting java.util.Date to java.time.LocalDate
		Date dob = employee.getDateOfBirth();
		Instant instant = Instant.ofEpochMilli(dob.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		LocalDate convDob = localDateTime.toLocalDate();
		
		System.out.println("converted date "+convDob);
		System.out.println("localdate "+LocalDate.now());
		System.out.println(convDob.equals(LocalDate.now()));
		if (convDob.equals(LocalDate.now())) {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.set("from", from);
			headers.set("subject", "karnataka");
			headers.set("tos", "[" + employee.getMailId() + "]");
			//headers.set("ccs", "[" + "@gmail.com" + "]");
			headers.set("ccs", ccs);
			headers.set("content", "bheem");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			SmsAndEmailResponse resp = restTemplate.postForObject(emailServiceUrl, request,
					SmsAndEmailResponse.class);

		} else {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.set("from", from);
			headers.set("subject", "congrats");
			headers.set("tos", "[" + employee.getMailId() + "]");
			headers.set("ccs", ccs);
			headers.set("content", "panther");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			SmsAndEmailResponse resp = restTemplate.postForObject(emailServiceUrl, request,
					SmsAndEmailResponse.class);

		}
		return employee;
	}

}
