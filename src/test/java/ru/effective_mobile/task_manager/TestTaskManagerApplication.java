package ru.effective_mobile.task_manager;

import org.springframework.boot.SpringApplication;

public class TestTaskManagerApplication {

	public static void main(String[] args) {
		SpringApplication.from(TaskManagerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
