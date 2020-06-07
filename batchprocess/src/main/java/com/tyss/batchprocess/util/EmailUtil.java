package com.tyss.batchprocess.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.tyss.batchprocess.dto.SmsAndEmailResponse;

/**
 * The class {@code EmailUtil} contains utility methods that helps to send
 * emails
 */

public class EmailUtil {

	/**
	 * Makes an API call to the {@code emailServiceUrl} with the given content and
	 * subject regarding birthdays
	 * 
	 * @param from            Mail-id from which the mail has to be sent
	 * 
	 * @param to              Mail-id to which the mail has to be sent
	 * 
	 * @param ccs             Mail-id to which the mail has to be sent
	 * 
	 * @param emailServiceUrl url to which the request has to be sent
	 */
		public void sendBirthdayMail(String from, String to, String ccs, String emailServiceUrl) {

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			

			headers.set("from", from);
			headers.set("subject", "congo");
			// headers.set("tos", "[" + employee.getMailId() + "]");
			headers.set("tos", "[" + to + "]");
			headers.set("ccs", ccs);
		//	headers.set("content", "chotu");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("content", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			SmsAndEmailResponse resp = restTemplate.postForObject(emailServiceUrl, request, SmsAndEmailResponse.class);
		}

		/**
		 * Makes an API call to the {@code emailServiceUrl} with the given content and
		 * subject regarding birthdays
		 * 
		 * @param from            Mail-id from which the mail has to be sent
		 * 
		 * @param to              Mail-id to which the mail has to be sent
		 * 
		 * @param ccs             Mail-id to which the mail has to be sent
		 * 
		 * @param emailServiceUrl url to which the request has to be sent
		 */

		public void sendAnniversaryMail(String from, String to, String ccs, String emailServiceUrl) {
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			//headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setContentType(MediaType.TEXT_PLAIN);
			headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
			//headers.setContentType(MediaType.APPLICATION_JSON);
			//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			
			headers.set("from", from);
			headers.set("subject", "cong pa");
			headers.set("tos", "[" + to + "]");
			headers.set("ccs", ccs);
			//headers.set("content", "chotu");
			//MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			Map<String, String> map = new HashMap<>();
			map.put("content", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			HttpEntity<Map<String, String>> request = new HttpEntity<>(map,headers);
			SmsAndEmailResponse resp = restTemplate.postForObject(emailServiceUrl, request, SmsAndEmailResponse.class);
		}
	}
