package com.soprasteria.springboottestspringbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.soprasteria.springboottestspringbatch.model.Employee;
import com.soprasteria.springboottestspringbatch.model.Task;
import com.soprasteria.springboottestspringbatch.processors.EmployeeProcessor;

@Configuration
@EnableBatchProcessing
public class EmployeeBatchConfiguration {
	
	@Autowired
	private EmployeeProcessor employeeProcessor;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
    public JobBuilderFactory jobBuilderFactory;
	
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
	
	//FlatFileItemReader VA BENE ANCHE PER LEGGERE GLI EMPLOYEE DA FILE .txt
	@Bean
	public FlatFileItemReader<Employee> txtFileEmployeeReader() {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
		reader.setResource(new FileSystemResource("src\\main\\resources\\text.txt"));
		reader.setLineMapper(new DefaultLineMapper<Employee>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"id", "nome", "eta", "indirizzo"});
				setDelimiter("|");
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
				setTargetType(Employee.class);
			}});
		}});
		return reader;
	}
	
	//LEGGIAMO DA MONGODB I TASK
	@Bean
	public MongoItemReader<Task> mongoDbTaskReader() {
		MongoItemReader<Task> reader = new MongoItemReader<Task>();
		reader.setTemplate(mongoTemplate);
		reader.setCollection("tasks");
		reader.setTargetType(Task.class);
		return reader;
	}
	
	//SCRIVIAMO GLI EMPLOYEE ARRICCHITI SU MONGODB
	@Bean
	public MongoItemWriter<Employee> mongoDbEmployeeWriter() {
		MongoItemWriter<Employee> writer = new MongoItemWriter<Employee>();
		writer.setTemplate(mongoTemplate);
		writer.setCollection("employees");
		return writer;
	}
	
	@Bean
	public Step step() {
		return stepBuilderFactory.get("step1").<Employee, Employee>chunk(2)
				.reader(txtFileEmployeeReader())
				.processor(employeeProcessor)
				.writer(mongoDbEmployeeWriter())
				.build();
	}
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.start(step())
				.build();
	}

}
