package tech.wenisch.discord.experiencebot;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;

public class RoleManager {

	public static int getLevel(String userID)
	{
		double totalExp = Double.parseDouble(Bot.getDatabaseConnection().getTotalExp(userID));
		return (int) (Math.log(totalExp)/Math.log(3));
	}

	public static void updateRegularRole(GuildVoiceJoinEvent event)
	{
		new UpdateRegularsThread(event).start();
	}

}
