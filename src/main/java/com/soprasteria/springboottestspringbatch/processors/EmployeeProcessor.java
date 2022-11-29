package com.soprasteria.springboottestspringbatch.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.soprasteria.springboottestspringbatch.model.Employee;
import com.soprasteria.springboottestspringbatch.model.Task;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public Employee process(Employee item) throws Exception {
		// TODO Auto-generated method stub
		String nomeTask = mongoTemplate.findOne(new Query(Criteria.where("id").is(item.getIdTask())), Task.class).getNome();
		item.setNomeTask(nomeTask);
		return item;
	}

}
