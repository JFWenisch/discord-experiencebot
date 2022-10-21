package tech.wenisch.discord.experiencebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

@SpringBootApplication
public class Main {


	public static void main(String[] args) {
		SpringApplication.run(Main.class,args);
		System.out.println("Initializing Database Connection...");
		DatabaseManager.getInstance().saveSession("server", "12345", 6789, 101112);
	}

}
