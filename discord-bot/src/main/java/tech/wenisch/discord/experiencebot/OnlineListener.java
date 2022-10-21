package tech.wenisch.discord.experiencebot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.wenisch.discord.experiencebot.exceptions.NoSessionStartTimeException;

public class OnlineListener extends ListenerAdapter {
	Map<String, Long> timelog = new HashMap<String, Long>();

	public OnlineListener(JDA jda) {

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {

		VoiceChannel vc = event.getChannelJoined();

		List<Member> m = vc.getMembers();
		timelog.put(event.getMember().getId(), System.currentTimeMillis());
		System.out.println(m.toString());
		System.out.println("JOIN " + event.getMember().getId());
		RoleManager.updateRegularRole(event);
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {

		// VoiceChannel vc = event.getChannelJoined();
		Long startTime = timelog.get(event.getMember().getId());
		Long endTime = System.currentTimeMillis();

		if (startTime != null) {
			long diff = endTime - startTime;
			if (diff > 30) {
				ExperienceManager.storeEXP(event, startTime, endTime, diff);
				RoleManager.assignRole(event);

				event.getMember().getUser().openPrivateChannel().queue((channel) -> {
					channel.sendMessage(generateSessionNotification(startTime, endTime, event)).queue();
				});
			}
		} else {
			String errorMessage = "Session for" + event.getMember().getEffectiveName() + " on "
					+ event.getGuild().getName() + " has no start time. Skipping EXP generation.";
			SentryManager.getInstance().handleError(new NoSessionStartTimeException(errorMessage));
			System.out.println(errorMessage);
		}
	}

	public static long generateEXPFromSession(long startTime, long endTime) {
		long diff = endTime - startTime;
		return TimeUnit.MILLISECONDS.toSeconds(diff);
	}

	public static String generateSessionNotification(long startTime, long endTime, GuildVoiceLeaveEvent event) {
		String totalExp = Bot.getDatabaseConnection().getTotalExp(event.getMember().getId(), event.getGuild().getId());
		long diff = endTime - startTime;
		String timeOnlineMessage = TimeUtils.formatDuration(diff);
		StringBuilder sb = new StringBuilder();
		sb.append("Yo " + event.getMember().getEffectiveName() + ", great Session right now on "
				+ event.getGuild().getName() + "! ");
		sb.append("You have been online for " + timeOnlineMessage + " and earned "
				+ TimeUnit.MILLISECONDS.toSeconds(diff) + "XP. ");
		sb.append("You are now on lvl " + RoleManager.getLevel(event.getMember().getId(), event.getGuild().getId())
				+ " with " + totalExp + " XP in total. ");
		sb.append(
				"(This bot is under active development and might change over time. For more information regarding the bot and available commands just reply with help)");
		return sb.toString();
	}
}
