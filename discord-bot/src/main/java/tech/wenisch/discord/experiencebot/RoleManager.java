package tech.wenisch.discord.experiencebot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import tech.wenisch.discord.experiencebot.actions.AssignRoleThread;
import tech.wenisch.discord.experiencebot.actions.UpdateRegularsThread;

public class RoleManager {
	private final static ExecutorService executor = Executors.newFixedThreadPool(1);
	public static int getLevel(String userID, String guildID)
	{
		double totalExp = Double.parseDouble(Bot.getDatabaseConnection().getTotalExp(userID,guildID));
		return (int) (Math.log(totalExp)/Math.log(3));
	}

	public static void updateRegularRole(GuildVoiceJoinEvent event)
	{
		try {
			executor.submit(new UpdateRegularsThread(event));
		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		}
	}
	public static void assignRole(GuildVoiceLeaveEvent event)
	{
		try {
			executor.submit(new AssignRoleThread(event));
		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		}
	}

}
