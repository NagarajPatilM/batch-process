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

		RestTemplate restTemplate = new RestTemplate();

		Multimap<String, Date> multimapForDob = ArrayListMultimap.create();
		Multimap<String, Date> multimapForDoj = ArrayListMultimap.create();

		// converting java.util.Date to java.time.LocalDate
		Date dob = employee.getDateOfBirth();
		Instant instant = Instant.ofEpochMilli(dob.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		LocalDate convDob = localDateTime.toLocalDate();

		if (convDob == LocalDate.now()) {
			log.info("" + employee.getDateOfBirth());
			multimapForDob.put(employee.getMailId(), employee.getDateOfBirth());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.set("from", from);
			headers.set("subject", "Congratulations");
			headers.set("tos", "[" + employee.getMailId() + "]");
			//headers.set("ccs", "[" + "@gmail.com" + "]");
			headers.set("ccs", ccs);
			headers.set("content", "good night");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			SmsAndEmailResponse resp = restTemplate.postForObject("http://localhost:8082/send-email", request,
					SmsAndEmailResponse.class);

		} else {
			log.info("" + employee.getDateOfJoin());
			multimapForDoj.put(employee.getMailId(), employee.getDateOfJoin());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.set("from", from);
			headers.set("subject", "congratulations");
			System.out.println(employee.getMailId());
			headers.set("tos", "[" + employee.getMailId() + "]");
			headers.set("ccs", "[" + "@gmail.com" + "]");
			headers.set("content", "Good night");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			SmsAndEmailResponse resp = restTemplate.postForObject(emailServiceUrl, request,
					SmsAndEmailResponse.class);

		}

		log.info("mm " + multimapForDob);
		log.info("mm " + multimapForDoj);

		return employee;
	}

}
