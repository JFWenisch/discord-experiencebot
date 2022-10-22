package tech.wenisch.discord.experiencebot;

import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class RoleManager {
	public static int getLevel(String userID, String guildID)
	{
		double totalExp = Double.parseDouble(DatabaseManager.getInstance().getTotalExp(userID,guildID));
		return (int) (Math.log(totalExp)/Math.log(3));
	}



}
