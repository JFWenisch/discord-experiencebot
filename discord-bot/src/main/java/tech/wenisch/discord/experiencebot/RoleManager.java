package tech.wenisch.discord.experiencebot;

import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class RoleManager {
	public static int getLevel(String userID, String guildID)
	{
		double totalExp = Double.parseDouble(Bot.getBean(DatabaseManager.class).getTotalExp(userID,guildID));
		return (int) (Math.log(totalExp)/Math.log(3));
	}



}