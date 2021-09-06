package br.com.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TasksManagerApp {

	public static void main(String[] args) {
		SpringApplication.run(TasksManagerApp.class, args);
	}

}
