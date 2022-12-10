package com.soprasteria.springboottestspringbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
public class BatchConfiguration extends DefaultBatchConfigurer {
	
	@Autowired
	private EmployeeProcessor employeeProcessor;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
	
	//FlatFileItemReader VA BENE ANCHE PER LEGGERE GLI EMPLOYEE DA FILE .txt
	@Bean
	public FlatFileItemReader<Employee> txtFileEmployeeReader() {
		FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
		reader.setResource(new FileSystemResource("src\\main\\resources\\text.txt"));
		reader.setLineMapper(new DefaultLineMapper<Employee>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"id", "nome", "eta", "indirizzo", "idTask"});
				setDelimiter("|");
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
				setTargetType(Employee.class);
			}});
		}});
		return reader;
	}
	
	@Bean
	public FlatFileItemReader<Task> txtFileTaskReader() {
		FlatFileItemReader<Task> reader = new FlatFileItemReader<>();
		reader.setResource(new FileSystemResource("src\\main\\resources\\tasks.txt"));
		reader.setLineMapper(new DefaultLineMapper<Task>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"id", "nome"});
				setDelimiter("|");
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Task>() {{
				setTargetType(Task.class);
			}});
		}});
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
	public MongoItemWriter<Task> mongoDbTaskWriter() {
		MongoItemWriter<Task> writer = new MongoItemWriter<Task>();
		writer.setTemplate(mongoTemplate);
		writer.setCollection("tasks");
		return writer;
	}

	public Step step() {
		return stepBuilderFactory.get("step1").<Task, Task>chunk(2)
				.reader(txtFileTaskReader())
				.writer(mongoDbTaskWriter())
				.build();
	}
	
	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<Employee, Employee>chunk(2)
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
				.next(step2())
				.build();
	}

}
