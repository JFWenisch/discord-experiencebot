package tech.wenisch.discord.experiencebot.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.wenisch.discord.experiencebot.Bot;
import tech.wenisch.discord.experiencebot.ExperienceManager;
import tech.wenisch.discord.experiencebot.SentryManager;
import tech.wenisch.discord.experiencebot.exceptions.NoSessionStartTimeException;

public class OnlineListener extends ListenerAdapter {

	Map<String, Long> timelog = new HashMap<String, Long>();

	public OnlineListener(JDA jda) {

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		Bot.logger.info("[GuildVoiceJoinEvent] " + event.getMember().getEffectiveName() + " joined "
				+ event.getGuild().getName());
		timelog.put(event.getMember().getId(), System.currentTimeMillis());
		Bot.updateRegularRole(event);
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		Bot.logger.info("[GuildVoiceLeaveEvent] " + event.getMember().getEffectiveName() + " left "
				+ event.getGuild().getName());
		Long startTime = timelog.get(event.getMember().getId());
		Long endTime = System.currentTimeMillis();

		if (startTime != null) {
			long diff = endTime - startTime;
			if (diff > 30) {
				ExperienceManager.storeEXP(event, startTime, endTime, diff);
				Bot.assignRole(event);
				Bot.sendSessionNotification(event,startTime,endTime);

			}
		} else {
			String errorMessage = "Session for " + event.getMember().getEffectiveName() + " at "
					+ event.getGuild().getName() + " has no start time. Skipping EXP generation.";
			SentryManager.getInstance().handleError(new NoSessionStartTimeException(errorMessage));
		
		}
	}




}
