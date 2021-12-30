package tech.wenisch.discord.experiencebot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class OnlineListener extends ListenerAdapter
{
	Map<String, Long> timelog= new HashMap<String,Long>(); 
	public OnlineListener(JDA jda)
	{

		// TODO Auto-generated constructor stub
	}
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {

		VoiceChannel vc = event.getChannelJoined();

		List <Member> m = vc.getMembers();
		timelog.put(event.getMember().getId(), System.currentTimeMillis());
		System.out.println(m.toString());
		System.out.println("JOIN "+event.getMember().getId());
	}
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {

		VoiceChannel vc = event.getChannelJoined();
		Long startTime = timelog.get(event.getMember().getId());
		Long endTime = System.currentTimeMillis();


		long diff =  endTime-startTime;
		if(diff > 30)
		{
			String timeOnlineMessage =TimeUtils.formatDuration(diff);

			System.out.println("LEFT after "+timeOnlineMessage+" ("+event.getMember().getId()+")");
			Bot.getDatabaseConnection().saveSession(event.getGuild().getId(), event.getMember().getId(),  startTime,endTime);
			event.getMember().getUser().openPrivateChannel().queue((channel) ->
			{
				channel.sendMessage(generateSessionNotification(startTime, endTime, event)).queue();
			});
		}
	}

	public static long generateEXPFromSession(long startTime, long endTime)
	{
		long diff =  endTime-startTime;
		return TimeUnit.MILLISECONDS.toSeconds(diff);
	}

	public static String generateSessionNotification(long startTime, long endTime,GuildVoiceLeaveEvent event)
	{
		String totalExp = Bot.getDatabaseConnection().getTotalExp(event.getMember().getId());
		long diff =  endTime-startTime;
		String timeOnlineMessage =TimeUtils.formatDuration(diff);
		StringBuilder sb = new StringBuilder();
		sb.append("Yo "+event.getMember().getEffectiveName()+", great Session right now on "+event.getGuild().getName()+"! ");
		sb.append("You have been online for "+timeOnlineMessage+" and earned "+TimeUnit.MILLISECONDS.toSeconds(diff) +"XP. ");
		sb.append("You now have " +totalExp+" XP in total.");
		return sb.toString();
	}
}
