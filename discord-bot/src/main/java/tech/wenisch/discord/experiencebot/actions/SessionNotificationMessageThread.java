package tech.wenisch.discord.experiencebot.actions;

import java.util.concurrent.TimeUnit;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import tech.wenisch.discord.experiencebot.Bot;
import tech.wenisch.discord.experiencebot.RoleManager;
import tech.wenisch.discord.experiencebot.SentryManager;
import tech.wenisch.discord.experiencebot.TimeUtils;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class SessionNotificationMessageThread extends Thread {
	GuildVoiceLeaveEvent event;
	long startTime, endTime;

	public SessionNotificationMessageThread(GuildVoiceLeaveEvent event, long startTime, long endTime) {
		this.event = event;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void run() {
		Thread.currentThread().setName("SessionNotification");
		ITransaction transaction = null;
		if (SentryManager.getInstance().isActivated()) {
			transaction = Sentry.startTransaction("SessionNotification", event.getMember().getEffectiveName());
		}
		try {
//			String message = generateSessionNotification(event, startTime, endTime);
//
//			event.getMember().getUser().openPrivateChannel().flatMap(channel -> channel.sendMessage(message)).queue(
//					null,
//					new ErrorHandler().handle(ErrorResponse.CANNOT_SEND_TO_USER,
//							(ex) -> Bot.logger.warn("Cannot send private message to "
//									+ event.getMember().getEffectiveName() + " (" + ex.getMessage() + ")")));
			System.out.println("Session Notification for " + event.getMember().getEffectiveName() + " on guild "
					+ event.getGuild().getName() + " would be sent now.");

		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		} finally {
			if (SentryManager.getInstance().isActivated()) {
				transaction.finish();
			}
		}
	}

	public static String generateSessionNotification(GuildVoiceLeaveEvent event, long startTime, long endTime) {
		String totalExp = Bot.getBean(DatabaseManager.class).getTotalExp(event.getMember().getId(),
				event.getGuild().getId());
		long diff = endTime - startTime;
		String timeOnlineMessage = TimeUtils.formatDuration(diff);
		StringBuilder sb = new StringBuilder();
		sb.append("Yo " + event.getMember().getEffectiveName() + ", great Session right now on "
				+ event.getGuild().getName() + "! ");
		sb.append("You have been online for " + timeOnlineMessage + " and earned "
				+ TimeUnit.MILLISECONDS.toSeconds(diff) + "XP. ");
		sb.append("You are now on lvl " + RoleManager.getLevel(event.getMember().getId(), event.getGuild().getId())
				+ " with " + totalExp + " XP in total. ");
		sb.append(
				"(This bot is under active development and might change over time. For more information regarding the bot and available commands just reply with help)");
		return sb.toString();
	}
}