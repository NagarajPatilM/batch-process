package com.tyss.batchprocess;

import org.springframework.batch.item.ItemProcessor;

import com.tyss.batchprocess.dto.Employee;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee>{

	@Override
	public Employee process(Employee employee) throws Exception {
		return employee;
	}

}
