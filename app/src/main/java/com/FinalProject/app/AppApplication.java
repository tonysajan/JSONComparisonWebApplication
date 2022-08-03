package com.FinalProject.app;

import Controller.FileUploadController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;

@SpringBootApplication
@ComponentScan({"com.FinalProject.app","Controller"})
public class AppApplication{


	//main function of the project
	public static void main(String[] args) {


		//Creating Upload directory
		new File(FileUploadController.uploadDirectory).mkdir();

		//Spring app execution point
		SpringApplication.run(AppApplication.class, args);


	}

}
