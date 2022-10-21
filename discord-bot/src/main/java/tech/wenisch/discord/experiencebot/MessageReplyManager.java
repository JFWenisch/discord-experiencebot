package tech.wenisch.discord.experiencebot;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class MessageReplyManager extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		ITransaction transaction = null;
		Message msg = event.getMessage();
		MessageChannel channel = event.getChannel();
		if (SentryManager.getInstance().isActivated()) {
			transaction = Sentry.startTransaction("MessageReceivedEvent", msg.getContentRaw());
		}
		try {
			if (!event.getAuthor().isBot()) {

				if (msg.getContentRaw().contains("help")) {
					channel.sendMessage(generateHelpMessage()).queue();
				} else if (msg.getContentRaw().contains("level")) {
					// channel.sendMessage(generateLevelResponse(event.getMember().getEffectiveName(),
					// event.getMember().getId())).queue();
				}
			}
		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		} finally {
			if (SentryManager.getInstance().isActivated()) {
				transaction.finish();
			}
		}
	}

	public static String generateHelpMessage() {

		StringBuilder sb = new StringBuilder();

		sb.append("This bot is under active development and might change over time. \n");
		sb.append("Available commands (reply within this private chat with the keyword): \n\n");
		sb.append("\n\n");
		sb.append("level - returns your level \n");
		sb.append("help - Displays this message with more information about the bot \n");
		sb.append("ping - Answers with pong as fast as possible, used for debugging purposes \n");
		sb.append("\n\n");
		sb.append(
				"Feedback and feature requests are highly appreciated. Pls use the https://discord.gg/r2rJh6s49x for all related communications \n \n");
		return sb.toString();
	}

	public static String generateLevelResponse(String username, String userID, String guildID) {
		String totalExp = DatabaseManager.getInstance().getTotalExp(userID, guildID);
		StringBuilder sb = new StringBuilder();
		sb.append("Yo " + username + ", ");
		sb.append(
				"You are now on lvl " + RoleManager.getLevel(userID, guildID) + " with " + totalExp + " XP in total. ");
		return sb.toString();
	}

}
