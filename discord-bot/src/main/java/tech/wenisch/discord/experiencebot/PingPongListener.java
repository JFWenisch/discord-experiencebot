package tech.wenisch.discord.experiencebot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PingPongListener extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		Message msg = event.getMessage();
		if (msg.getContentRaw().equals("ping"))
		{
			MessageChannel channel = event.getChannel();
			long time = System.currentTimeMillis();
			channel.sendMessage("pong")
			.queue(response -> 
			{
				response.editMessageFormat("pong: %d ms", System.currentTimeMillis() - time).queue();
			}
					);
		}
	}
}
