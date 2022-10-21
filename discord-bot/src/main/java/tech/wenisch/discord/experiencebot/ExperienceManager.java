package tech.wenisch.discord.experiencebot;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

public class ExperienceManager 
{
	public static void storeEXP(GuildVoiceLeaveEvent event, Long startTime, Long endTime, long diff)
	{
		ITransaction transaction = null;
		if (SentryManager.getInstance().isActivated()) {
			transaction = Sentry.startTransaction("StoreEXP",
					event.getGuild().getName() + "-" + event.getMember().getId());
		}
		try {
			String timeOnlineMessage = TimeUtils.formatDuration(diff);
			System.out.println(event.getMember().getEffectiveName()+" left "+event.getGuild().getName()+ "after" + timeOnlineMessage);
			Bot.getDatabaseConnection().saveSession(event.getGuild().getId(), event.getMember().getId(),
					startTime, endTime);
		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		} finally {
			if (SentryManager.getInstance().isActivated()) {
				transaction.finish();
			}
		}

	}
}
