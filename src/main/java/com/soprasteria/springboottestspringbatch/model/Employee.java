package com.soprasteria.springboottestspringbatch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	
	@Id
	private Integer id;
	
	private String nome;
	
	private Integer eta;
	
	private String indirizzo;
	
	private Integer idTask;
	
	private String nomeTask;

}
