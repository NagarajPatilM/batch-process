package com.tyss.batchprocess.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.tyss.batchprocess.dto.EmployeeBean;
import com.tyss.batchprocess.util.EmailUtil;

import lombok.extern.java.Log;

/**
 * The class {@code ProcessorForDoj} acts as a processor. This class
 * processes the data which is in the form of Bean object and returns the
 * processed data in the form of Bean object
 */   

@Log
public class ProcessorForDoj implements ItemProcessor<EmployeeBean, EmployeeBean> {

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
		
		System.out.println("************************Processor For Doj***************************");
		log.info("MAILID " + employee.getMailId() +" DOB " + employee.getDateOfBirth()+ " DOJ " + employee.getDateOfJoin());
		EmailUtil emailUtil = new EmailUtil();
		emailUtil.sendAnniversaryMail(from, employee.getMailId(), ccs, emailServiceUrl);
		return employee;
	}
}