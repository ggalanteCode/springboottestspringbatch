package com.soprasteria.springboottestspringbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SpringboottestspringbatchApplication implements CommandLineRunner {
	
	@Autowired
	private Job job;
	
	private JobLauncher jobLauncher;

	public static void main(String[] args) {
		SpringApplication.run(SpringboottestspringbatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		JobParameters parameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis()).toJobParameters();
		jobLauncher.run(job, parameters);
	}

}
