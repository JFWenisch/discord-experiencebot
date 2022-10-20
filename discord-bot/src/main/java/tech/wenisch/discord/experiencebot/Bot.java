package tech.wenisch.discord.experiencebot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class Bot {

	private static DatabaseManager database;

	public static void main(String[] args)
	{
		try {
			System.out.println("Starting Discord Experiencebot v"+Bot.class.getPackage().getImplementationVersion());
			System.out.println("Going to sleep for 30s");
			Thread.sleep(30000);
		} catch (InterruptedException e1) {
			SentryManager.getInstance().handleError(e1);
			
		}
		System.out.println("Initializing Database Connection...");
		database = new DatabaseManager();
		JDA jda;
		try 
		{
			jda = JDABuilder.createDefault(System.getenv("DC_TOKEN"))
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.build();
			jda.addEventListener(new PingPongListener());
			jda.addEventListener(new MessageReplyManager());
			jda.addEventListener(new OnlineListener(jda));
		}
		catch (LoginException e) 
		{

			SentryManager.getInstance().handleError(e);
		}
	}
	public static DatabaseManager getDatabaseConnection()
	{
		return database;
	}
}
