package tech.wenisch.discord.experiencebot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReplyManager extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		Message msg = event.getMessage();
		MessageChannel channel = event.getChannel();
		if(!event.getAuthor().isBot())
		{


			if (msg.getContentRaw().contains("help"))
			{
				channel.sendMessage(generateHelpMessage()).queue();
			}
			else if (msg.getContentRaw().contains("level"))
			{
				channel.sendMessage(generateLevelResponse(event.getMember().getEffectiveName(), event.getMember().getId())).queue();
			}
		}
	}

	public static String generateHelpMessage()
	{

		StringBuilder sb = new StringBuilder();

		sb.append("This bot is under active development and might change over time. \n");
		sb.append("Available commands (reply within this private chat with the keyword): \n\n");
		sb.append("\n\n");
		sb.append("level - returns your level \n");
		sb.append("help - Displays this message with more information about the bot \n");
		sb.append("ping - Answers with pong as fast as possible, used for debugging purposes \n");
		sb.append("\n\n");
		sb.append("Feedback and feature requests are highly appreciated. Pls use the https://discord.gg/jmwz7Ga3 for all related communications \n \n");
		return sb.toString();
	}
	public static String generateLevelResponse(String username, String userID)
	{
		String totalExp = Bot.getDatabaseConnection().getTotalExp(userID);
		StringBuilder sb = new StringBuilder();
		sb.append("Yo "+username+", ");
		sb.append("You are now on lvl "+RoleManager.getLevel(userID)+" with " +totalExp+" XP in total. ");
		return sb.toString();
	}

}
