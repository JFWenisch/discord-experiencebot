package tech.wenisch.discord.experiencebot;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;

public class UpdateRegularsThread extends Thread
{
	GuildVoiceJoinEvent event;
	UpdateRegularsThread(GuildVoiceJoinEvent event)
	{
		this.event=event;
	}
	public void run() 
	{
		String roleName = event.getGuild().getName().toUpperCase()+" REGULARS";
		List<String> assignableMembers= Bot.getDatabaseConnection().getRegulars(event.getGuild().getId());
		List<Role> roles = event.getGuild().getRolesByName(roleName, true);
		Role destRole =null;
		if(roles.size()<1)
		{
			destRole = event.getGuild().createRole().setName(roleName).complete();
			System.out.println("Successfully created role "+destRole);
		}
		else
		{
			destRole = roles.get(0);
		}
		final Role createdRole = destRole;
		// Add assignable Members
		for(int i=0; i < assignableMembers.size(); i++)
		{
			event.getGuild().addRoleToMember(assignableMembers.get(i), destRole).queue();
			System.out.println("Added member " +assignableMembers.get(i) +" to regular role");
		}

		List<Member> members = event.getGuild().findMembers(member -> {
			if (member.getRoles().contains(createdRole)){
				return true;
			}
			return false;
		}).get();
		// Remove not qualified members
		for(int i=0; i < members.size(); i++)
		{
			if(!assignableMembers.contains(members.get(i).getId()))
			{
				event.getGuild().removeRoleFromMember(members.get(i), createdRole).queue();
				System.out.println("Removed member " +members.get(i) +" from regular role");
			}
		}    
	}
}
