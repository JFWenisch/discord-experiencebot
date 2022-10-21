package tech.wenisch.discord.experiencebot;

import javax.security.auth.login.LoginException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@SpringBootApplication
public class Bot {

	

	public static void main(String[] args) {
		SpringApplication.run(Bot.class,args);
			System.out.println("Starting Discord Experiencebot " + Bot.class.getPackage().getImplementationVersion());
			
	
		JDA jda;
		try {
			jda = JDABuilder.createDefault(System.getenv("DC_TOKEN")).enableIntents(GatewayIntent.GUILD_MEMBERS)
					.build();
			jda.addEventListener(new PingPongListener());
			jda.addEventListener(new MessageReplyManager());
			jda.addEventListener(new OnlineListener(jda));
		} catch (LoginException e) {

			SentryManager.getInstance().handleError(e);
		}
	}

}
