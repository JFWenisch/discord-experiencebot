package tech.wenisch.discord.experiencebot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
		RoleManager.updateRegularRole(event);
	}
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {

		//	VoiceChannel vc = event.getChannelJoined();
		Long startTime = timelog.get(event.getMember().getId());
		Long endTime = System.currentTimeMillis();


		long diff =  endTime-startTime;
		if(diff > 30)
		{
			ITransaction transaction = null;
			if (SentryManager.getInstance().isActivated()) {
				transaction = Sentry.startTransaction("StoreEXP",event.getGuild().getName()+"-"+event.getMember().getId());
			}
			try {
				String timeOnlineMessage =TimeUtils.formatDuration(diff);

				System.out.println("LEFT after "+timeOnlineMessage+" ("+event.getMember().getId()+")");
				Bot.getDatabaseConnection().saveSession(event.getGuild().getId(), event.getMember().getId(),  startTime,endTime);
			} catch (Exception e) {
				SentryManager.getInstance().handleError(e);
			} finally {
				if (SentryManager.getInstance().isActivated()) {
					transaction.finish();
				}
			}
			if (SentryManager.getInstance().isActivated()) {
				transaction = Sentry.startTransaction("StoreEXP",event.getGuild().getName()+"-"+event.getMember().getId());
			}
			try {
				assignRole(event);
			} catch (Exception e) {
				SentryManager.getInstance().handleError(e);
			} finally {
				if (SentryManager.getInstance().isActivated()) {
					transaction.finish();
				}
			}
			event.getMember().getUser().openPrivateChannel().queue((channel) ->
			{
				channel.sendMessage(generateSessionNotification(startTime, endTime, event)).queue();
			});
		}
	}

	private void assignRole(GuildVoiceLeaveEvent event) 
	{
		int level = RoleManager.getLevel(event.getMember().getId(),event.getGuild().getId());
		String roleName=event.getGuild().getName().toUpperCase()+" LVL "+level;
		System.out.println(event.getMember().getId() +" is on lvl "+ level+". Trying to assign role");
		List<Role> roles = event.getGuild().getRolesByName(roleName, true);
		if(roles.size()>0)
		{
			Role destRole = roles.get(0);
			event.getGuild().addRoleToMember(event.getMember(), destRole).queue();
			System.out.println("Successfully assigned member "+event.getMember()+" to role "+destRole);

		}
		else
		{
			Role destRole = event.getGuild().createRole().setName(roleName).complete();
			System.out.println("Successfully created role "+destRole);
			event.getGuild().addRoleToMember(event.getMember(), destRole).queue();
			System.out.println("Successfully assigned member "+event.getMember()+" to role "+destRole);
		}



	}
	public static long generateEXPFromSession(long startTime, long endTime)
	{
		long diff =  endTime-startTime;
		return TimeUnit.MILLISECONDS.toSeconds(diff);
	}

	public static String generateSessionNotification(long startTime, long endTime,GuildVoiceLeaveEvent event)
	{
		String totalExp = Bot.getDatabaseConnection().getTotalExp(event.getMember().getId(),event.getGuild().getId());
		long diff =  endTime-startTime;
		String timeOnlineMessage =TimeUtils.formatDuration(diff);
		StringBuilder sb = new StringBuilder();
		sb.append("Yo "+event.getMember().getEffectiveName()+", great Session right now on "+event.getGuild().getName()+"! ");
		sb.append("You have been online for "+timeOnlineMessage+" and earned "+TimeUnit.MILLISECONDS.toSeconds(diff) +"XP. ");
		sb.append("You are now on lvl "+RoleManager.getLevel(event.getMember().getId(),event.getGuild().getId())+" with " +totalExp+" XP in total. ");
		sb.append("(This bot is under active development and might change over time. For more information regarding the bot and available commands just reply with help)");
		return sb.toString();
	}
}
