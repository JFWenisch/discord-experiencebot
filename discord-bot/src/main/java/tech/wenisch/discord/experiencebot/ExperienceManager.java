package tech.wenisch.discord.experiencebot;

import java.util.concurrent.TimeUnit;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

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
			DatabaseManager.getInstance().saveSession(event.getGuild().getId(), event.getMember().getId(),
					startTime, endTime);
			Bot.logger.info(event.getMember().getEffectiveName()+" received "+ timeOnlineMessage.substring(0,timeOnlineMessage.indexOf(" "))  +"EXP for his session on "+event.getGuild().getName() );
			
		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		} finally {
			if (SentryManager.getInstance().isActivated()) {
				transaction.finish();
			}
		}

	}
	public static long generateEXPFromSession(long startTime, long endTime) {
		long diff = endTime - startTime;
		return TimeUnit.MILLISECONDS.toSeconds(diff);
	}	
}
