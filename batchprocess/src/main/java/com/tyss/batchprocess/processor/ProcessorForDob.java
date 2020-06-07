package com.tyss.batchprocess.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.tyss.batchprocess.dto.EmployeeBean;
import com.tyss.batchprocess.util.EmailUtil;

import lombok.extern.java.Log;

/**
 * The class {@code ProcessorForDob} acts as a processor. This class
 * processes the data which is in the form of Bean object and returns the
 * processed data in the form of Bean object
 */
@Log
public class ProcessorForDob implements ItemProcessor<EmployeeBean, EmployeeBean> {

	@Value("${from.mailid}")
	private String from;

	@Value("${cc.mailid}")
	private String ccs;

	@Value("${emailservice.url}")
	private String emailServiceUrl;

	/**
	 * @param employee The bean object which needs to be processes
	 * 
	 * @return The processed bean
	 */

	@Override
	public EmployeeBean process(EmployeeBean employee) throws Exception {

		System.out.println("************************Processor For DOB***************************");
		log.info("MAILID " + employee.getMailId() + " DOB " + employee.getDateOfBirth() + " DOJ "+ employee.getDateOfJoin());

		// converting java.util.Date(DOB & DOJ) to String
		/*
		 * Date dob = employee.getDateOfBirth(); String sdob =
		 * dob.toString().substring(5);
		 * 
		 * Date doj = employee.getDateOfJoin(); String sdoj =
		 * doj.toString().substring(5);
		 * 
		 * // fetching today's date and converting to the String String todaysDate =
		 * LocalDate.now().toString().substring(5);
		 */

		EmailUtil emailUtil = new EmailUtil();

		emailUtil.sendBirthdayMail(from, employee.getMailId(), ccs, emailServiceUrl);
		/*
		 * if (sdob.equals(sdoj)) { emailUtil.sendBirthdayMail(from,
		 * employee.getMailId(), ccs, emailServiceUrl);
		 * emailUtil.sendAnniversaryMail(from, employee.getMailId(), ccs,
		 * emailServiceUrl); } else
		 */ /*
			 * if (sdob.equals(todaysDate)) { emailUtil.sendBirthdayMail(from,
			 * employee.getMailId(), ccs, emailServiceUrl); } else {
			 * emailUtil.sendAnniversaryMail(from, employee.getMailId(), ccs,
			 * emailServiceUrl); }
			 */
		return employee;
	}
}
