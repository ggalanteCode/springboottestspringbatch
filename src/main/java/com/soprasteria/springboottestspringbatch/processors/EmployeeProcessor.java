package com.soprasteria.springboottestspringbatch.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.soprasteria.springboottestspringbatch.model.Employee;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

	@Override
	public Employee process(Employee item) throws Exception {
		// TODO Auto-generated method stub
		return item;
	}

}
