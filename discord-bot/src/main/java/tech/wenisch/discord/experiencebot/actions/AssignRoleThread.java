package tech.wenisch.discord.experiencebot.actions;

import java.util.List;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import tech.wenisch.discord.experiencebot.RoleManager;
import tech.wenisch.discord.experiencebot.SentryManager;

public class AssignRoleThread extends Thread {
	GuildVoiceLeaveEvent event ;

	public AssignRoleThread(GuildVoiceLeaveEvent event) {
		this.event = event;
	}

	public void run() {
		ITransaction transaction = null;
		if (SentryManager.getInstance().isActivated()) {
			transaction = Sentry.startTransaction("AssignRole", event.getGuild().getName().toUpperCase());
		}
		try {
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

		} catch (Exception e) {
			SentryManager.getInstance().handleError(e);
		} finally {
			if (SentryManager.getInstance().isActivated()) {
				transaction.finish();
			}
		}
	}
}
