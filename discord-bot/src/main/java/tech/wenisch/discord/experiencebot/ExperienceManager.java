package tech.wenisch.discord.experiencebot;

public class ExperienceManager {
	
	public static int getLevel(String userID)
	{
		double totalExp = Double.parseDouble(Bot.getDatabaseConnection().getTotalExp(userID));
		return (int) (Math.log(totalExp)/Math.log(3));
	}

}
