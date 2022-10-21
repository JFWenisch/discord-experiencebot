package tech.wenisch.discord.experiencebot.actions;

import java.util.List;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import tech.wenisch.discord.experiencebot.SentryManager;
import tech.wenisch.discord.experiencebot.persistence.DatabaseManager;

public class UpdateRegularsThread extends Thread {
	GuildVoiceJoinEvent event;

	public UpdateRegularsThread(GuildVoiceJoinEvent event) {
		this.event = event;
	}

	public void run() {
		ITransaction transaction = null;
		if (SentryManager.getInstance().isActivated()) {
			transaction = Sentry.startTransaction("UpdateRegulars", event.getGuild().getName().toUpperCase());
		}
		try {
			String roleName = event.getGuild().getName().toUpperCase() + " REGULARS";
			List<String> assignableMembers = DatabaseManager.getInstance().getRegulars(event.getGuild().getId());
			List<Role> roles = event.getGuild().getRolesByName(roleName, true);
			Role destRole = null;
			if (roles.size() < 1) {
				destRole = event.getGuild().createRole().setName(roleName).complete();
				System.out.println("Successfully created role " + destRole);
			} else {
				destRole = roles.get(0);
			}
			final Role createdRole = destRole;
			// Add assignable Members
			for (int i = 0; i < assignableMembers.size(); i++) {
				event.getGuild().addRoleToMember(assignableMembers.get(i), destRole).queue();
				System.out.println("Added member " + assignableMembers.get(i) + " to regular role");
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
					System.out.println("Removed member " + members.get(i) + " from regular role");
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
