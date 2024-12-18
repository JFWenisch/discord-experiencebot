package tech.wenisch.discord.experiencebot.actions;

import java.util.List;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import tech.wenisch.discord.experiencebot.Bot;
import tech.wenisch.discord.experiencebot.RoleManager;
import tech.wenisch.discord.experiencebot.SentryManager;

public class AssignRoleThread extends Thread {
	GuildVoiceLeaveEvent event ;

	public AssignRoleThread(GuildVoiceLeaveEvent event) {
		this.event = event;
	}

	public void run() {
		Thread.currentThread().setName("AssignRole");
		ITransaction transaction = null;
		if (SentryManager.getInstance().isActivated()) {
			transaction = Sentry.startTransaction("AssignRole", event.getGuild().getName().toUpperCase());
		}
		try {
			int level = RoleManager.getLevel(event.getMember().getId(),event.getGuild().getId());
			if (level < 0)
			{
				Bot.logger.info("Level for " +event.getMember().getEffectiveName()+" cannot be identified (level="+level+"). Resetting level to 0.");
				level = 0;
			}
			String roleName=event.getGuild().getName().toUpperCase()+" LVL "+level;
			Bot.logger.info(event.getMember().getEffectiveName() +" is on lvl "+ level+" at "+event.getGuild().getName()+". Trying to assign role");
			List<Role> roles = event.getGuild().getRolesByName(roleName, true);
			if(roles.size()>0)
			{
				Role destRole = roles.get(0);
				event.getGuild().addRoleToMember(event.getMember(), destRole).queue();
				Bot.logger.info("Successfully assigned member "+event.getMember().getEffectiveName()+" to role "+destRole+" at "+event.getGuild().getName()+".");

			}
			else
			{
				Role destRole = event.getGuild().createRole().setName(roleName).complete();
				Bot.logger.info("Successfully created role "+destRole+" at "+event.getGuild().getName()+".");
				event.getGuild().addRoleToMember(event.getMember(), destRole).queue();
				Bot.logger.info("Successfully assigned member "+event.getMember().getEffectiveName()+" to role "+destRole+" at "+event.getGuild().getName()+".");
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
