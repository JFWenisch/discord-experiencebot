package tech.wenisch.discord.experiencebot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import tech.wenisch.discord.experiencebot.actions.AssignRoleThread;
import tech.wenisch.discord.experiencebot.actions.SessionNotificationMessageThread;
import tech.wenisch.discord.experiencebot.actions.UpdateRegularsThread;
import tech.wenisch.discord.experiencebot.listeners.MessageReplyListener;
import tech.wenisch.discord.experiencebot.listeners.OnlineListener;
import tech.wenisch.discord.experiencebot.listeners.PingPongListener;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

@SpringBootApplication
public class Bot {
	public static final Logger logger = LoggerFactory.getLogger(Bot.class);
	private final static ExecutorService executor = Executors.newFixedThreadPool(1);

	// hold the Spring context so static code can obtain beans when needed
	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		applicationContext = SpringApplication.run(Bot.class, args);
		logger.info("Starting Discord Experiencebot " + Bot.class.getPackage().getImplementationVersion());

		JDA jda;
		try {
			logger.info("Initializing Discord Connection");
			jda = JDABuilder.createDefault(System.getenv("DC_TOKEN")).enableIntents(GatewayIntent.GUILD_MEMBERS)
					.build();
			jda.addEventListener(new PingPongListener());
			jda.addEventListener(new MessageReplyListener());
			jda.addEventListener(new OnlineListener(jda));
			logger.info("Successfully initialized");
		} catch (LoginException e) {

			SentryManager.getInstance().handleError(e);
		}
	}

	public static void updateRegularRole(GuildVoiceJoinEvent event) {
		try {
			DatabaseManager db = applicationContext.getBean(DatabaseManager.class);
			executor.submit(new UpdateRegularsThread(event, db));
		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		}
	}

	public static void assignRole(GuildVoiceLeaveEvent event) {
		try {
			executor.submit(new AssignRoleThread(event));
		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		}
	}

	public static void sendSessionNotification(GuildVoiceLeaveEvent event, Long startTime, Long endTime) {
		try {
			executor.submit(new SessionNotificationMessageThread(event, startTime, endTime));
		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		}

	}

	// helper to fetch beans from the Spring context from static code
	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

}