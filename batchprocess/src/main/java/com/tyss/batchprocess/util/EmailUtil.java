package com.tyss.batchprocess.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.tyss.batchprocess.dto.SmsAndEmailResponse;

public class EmailUtil {

	public void sendBirthdayMail(String from, String to, String ccs, String emailServiceUrl) {

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("from", from);
		headers.set("subject", "congratulations on your birthday");
		// headers.set("tos", "[" + employee.getMailId() + "]");
		headers.set("tos", "[" + to + "]");
		headers.set("ccs", ccs);
		headers.set("content", "chotu");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		SmsAndEmailResponse resp = restTemplate.postForObject(emailServiceUrl, request, SmsAndEmailResponse.class);
	}

	public void sendAnniversaryMail(String from, String to, String ccs, String emailServiceUrl) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("from", from);
		headers.set("subject", "congratulations on your anniversary");
		headers.set("tos", "[" + to + "]");
		headers.set("ccs", ccs);
		headers.set("content", "chotu");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		SmsAndEmailResponse resp = restTemplate.postForObject(emailServiceUrl, request, SmsAndEmailResponse.class);
	}
}
