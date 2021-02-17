package com.smec.cc.accountsmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccountsManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsManagementApplication.class, args);
	}

}
