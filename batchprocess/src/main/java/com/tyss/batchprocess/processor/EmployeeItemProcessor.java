package com.tyss.batchprocess.processor;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.tyss.batchprocess.dto.Employee;
import com.tyss.batchprocess.dto.SmsAndEmailResponse;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {

	@Override
	public Employee process(Employee employee) throws Exception {
		System.out.println("employee in EIP----------------->");

		List<Employee> eLists = Arrays.asList(employee);
		Multimap<String, Date> multimap = ArrayListMultimap.create();

		System.out.println(eLists.size());
		for (Employee emp : eLists) {
			multimap.put(emp.getMailId(), emp.getDateOfBirth());
			multimap.put(emp.getMailId(), emp.getDateOfJoin());
		}

		System.out.println(multimap);

		demo();

		RestTemplate restTemplate = new RestTemplate();
		//SmsAndEmailResponse resp = restTemplate.getForObject("http://localhost:8082/send-email", SmsAndEmailResponse.class);
		String obj = restTemplate.getForObject("http://localhost:8082/a", String.class);
		System.out.println("rest template -------------------------------- "+obj);

		return employee;
	}

	public static void demo() {
		System.out.println("demo method");
	}

}
