package tech.wenisch.discord.experiencebot.actions;

import java.util.List;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import tech.wenisch.discord.experiencebot.Bot;
import tech.wenisch.discord.experiencebot.SentryManager;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class UpdateRegularsThread implements Runnable {
	GuildVoiceJoinEvent event;
	private final DatabaseManager databaseManager;

	public UpdateRegularsThread(GuildVoiceJoinEvent event, DatabaseManager databaseManager) {
		this.event = event;
		this.databaseManager = databaseManager;
		
	}

	public void run() {
		Thread.currentThread().setName("UpdateRegulars");
		Bot.logger.info("Updating regulars for "+event.getGuild().getName());
		ITransaction transaction = null;
		if (SentryManager.getInstance().isActivated()) {
			transaction = Sentry.startTransaction("UpdateRegulars", event.getGuild().getName().toUpperCase());
		}
		try {
			String roleName = event.getGuild().getName().toUpperCase() + " REGULARS";
			List<String> assignableMembers = databaseManager.getRegulars(event.getGuild().getId());
			System.out.println("There are a total of " + assignableMembers.size() +" regular members on "+event.getGuild().getName());
			List<Role> roles = event.getGuild().getRolesByName(roleName, true);
			Role destRole = null;
			if (roles.size() < 1) {
				Bot.logger.info("Regular Role on "+event.getGuild().getName()+" could not be found. Creating Role");
				destRole = event.getGuild().createRole().setName(roleName).complete();
				Bot.logger.info("Successfully created role " + destRole);
			} else {
				destRole = roles.get(0);
			}
			final Role createdRole = destRole;
			// Add assignable Members
			for (int i = 0; i < assignableMembers.size(); i++) {
				event.getGuild().addRoleToMember(assignableMembers.get(i), destRole).queue();
				Bot.logger.info("Added member " + assignableMembers.get(i) + " to regular role");
			}

			List<Member> members = event.getGuild().findMembers(member -> {
				if (member.getRoles().contains(createdRole)) {
					return true;
				}
				return false;
			}).get();
			// Remove not qualified members
			for (int i = 0; i < members.size(); i++) {
				if (!assignableMembers.contains(members.get(i).getId())) {
					event.getGuild().removeRoleFromMember(members.get(i), createdRole).queue();
					Bot.logger.info("Removed member " + members.get(i) + " from regular role");
				}
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