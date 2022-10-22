package tech.wenisch.discord.experiencebot.listeners;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.wenisch.discord.experiencebot.SentryManager;

public class PingPongListener extends ListenerAdapter {
	
//	@Override
//	public void onMessageReceived(MessageReceivedEvent event) {
//		ITransaction transaction = null;
//		Message msg = event.getMessage();
//		if (SentryManager.getInstance().isActivated()) {
//			transaction = Sentry.startTransaction("MessageReceivedEvent", msg.getContentRaw());
//		}
//		try {
//			if (msg.getContentRaw().equals("ping")) {
//
//				MessageChannel channel = event.getChannel();
//				long time = System.currentTimeMillis();
//				channel.sendMessage("pong").queue(response -> {
//					response.editMessageFormat("pong: %d ms", System.currentTimeMillis() - time).queue();
//				});
//			}
//		} catch (Exception e) {
//			SentryManager.getInstance().handleError(e);
//		} finally {
//			if (SentryManager.getInstance().isActivated()) {
//				transaction.finish();
//			}
//		}
//	}
}
