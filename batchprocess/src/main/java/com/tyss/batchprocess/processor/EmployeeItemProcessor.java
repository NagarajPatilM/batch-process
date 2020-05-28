package com.tyss.batchprocess.processor;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.tyss.batchprocess.dto.EmployeeBean;
import com.tyss.batchprocess.util.EmailUtil;

import lombok.extern.java.Log;

/**
 * 
 * This class processes the data which is in the form of Bean object and returns
 * the processed data in the form of Bean object
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
		log.info("MAILID " + employee.getMailId() + " DOJ " + employee.getDateOfJoin());
		log.info("MAILID " + employee.getMailId() + " DOB " + employee.getDateOfBirth());

		// converting java.util.Date(DOB & DOJ) to String
		Date dob = employee.getDateOfBirth();
		String sdob = dob.toString().substring(5);

		Date doj = employee.getDateOfJoin();
		String sdoj = doj.toString().substring(5);
		
        //fetching today's date and converting to the String
		String todaysDate = LocalDate.now().toString().substring(5);

		EmailUtil emailUtil = new EmailUtil();

		if (sdob.equals(sdoj)) {
			emailUtil.sendBirthdayMail(from, employee.getMailId(), ccs, emailServiceUrl);
			emailUtil.sendAnniversaryMail(from, employee.getMailId(), ccs, emailServiceUrl);
		} else if (sdob.equals(todaysDate)) {
			emailUtil.sendBirthdayMail(from, employee.getMailId(), ccs, emailServiceUrl);
		} else {
			emailUtil.sendAnniversaryMail(from, employee.getMailId(), ccs, emailServiceUrl);
		}
		return employee;
	}
}
