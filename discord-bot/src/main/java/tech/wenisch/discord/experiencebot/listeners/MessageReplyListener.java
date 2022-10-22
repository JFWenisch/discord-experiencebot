package tech.wenisch.discord.experiencebot.listeners;

import java.util.List;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.wenisch.discord.experiencebot.Bot;
import tech.wenisch.discord.experiencebot.RoleManager;
import tech.wenisch.discord.experiencebot.SentryManager;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class MessageReplyListener extends ListenerAdapter {
	public static final String commandPrefix = "/dexbo";

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message msg = event.getMessage();
		MessageChannel channel = event.getChannel();
		if (!event.getAuthor().isBot()) {
			if (msg.getContentRaw().startsWith(commandPrefix)) {
				String command = msg.getContentRaw().substring(commandPrefix.length() + 1).toLowerCase();
				ITransaction transaction = null;
				Bot.logger.info("[onMessageReceived] " + event.getMember().getEffectiveName() + " send " + command
						+ " command at " + event.getGuild().getName());
				if (SentryManager.getInstance().isActivated()) {
					transaction = Sentry.startTransaction("MessageReceivedEvent", command);
				}
				try {

					if (command.equals("help")) {
						channel.sendMessage(generateHelpMessage()).queue();
					} else if (command.equals("level")) {
						channel.sendMessage(generateLevelResponse(event.getMember().getEffectiveName(),
								event.getMember().getId(), event.getGuild().getId())).queue();
					} else if (command.equals("regulars")) {
						channel.sendMessage(generateRegularsResponse(event.getGuild().getId())).queue();
					}

//					else if (command.equals("top")) {
//						channel.sendMessage(generateTopUsersResponse(event.getGuild().getId())).queue();
//					}
					else if (command.equals("ping")) {

						long time = System.currentTimeMillis();
						channel.sendMessage("pong").queue(response -> {
							response.editMessageFormat("pong: %d ms", System.currentTimeMillis() - time).queue();
						});
					}

				} catch (Exception e) {
					SentryManager.getInstance().handleError(e);
				} finally {
					if (SentryManager.getInstance().isActivated()) {
						transaction.finish();
					}
				}
			}
		}
	}

	public static String generateHelpMessage() {

		StringBuilder sb = new StringBuilder();

		sb.append("This bot is under active development and might change over time. \n");
		sb.append("Available commands: \n\n");
		sb.append("\n\n");
		sb.append("level - returns your level on this server \n");
//		sb.append("top - returns the top members of this server based on the EXP \n");
		sb.append("regulars - returns the regular members of this server \n");
		sb.append("help - Displays this message with more information about the bot \n");
		sb.append("ping - Answers with pong as fast as possible, used for debugging purposes \n");
		sb.append("\n\n");
		sb.append(
				"Feedback and feature requests are highly appreciated. Pls use the https://discord.gg/4DUf76BbBY for all related communications \n \n");
		return sb.toString();
	}

	public static String generateLevelResponse(String username, String userID, String guildID) {
		String totalExp = DatabaseManager.getInstance().getTotalExp(userID, guildID);
		StringBuilder sb = new StringBuilder();
		sb.append("Yo " + username + ", ");
		sb.append("You are now on lvl " + RoleManager.getLevel(userID, guildID) + " with " + totalExp
				+ " EXP in total. ");
		return sb.toString();
	}

	public static String generateTopUsersResponse(String guildID) {
		List<String> users = DatabaseManager.getInstance().getTopUsers(guildID);
		if (users.size() < 1)
			return "There are currently no Users with EXP on this server";
		StringBuilder sb = new StringBuilder();
		for (String username : users) {
			sb.append(username + "\n");
		}

		return sb.toString();
	}

	public static String generateRegularsResponse(String guildID) {
		List<String> regulars = DatabaseManager.getInstance().getRegulars(guildID);
		if (regulars.size() < 1)
			return "There are currently no regulars on this server";
		StringBuilder sb = new StringBuilder();
		for (String username : regulars) {
			sb.append(username + "\n");
		}

		return sb.toString();
	}

}
