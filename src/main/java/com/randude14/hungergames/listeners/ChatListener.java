package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.Defaults.Perm;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.games.HungerGame;

import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;


public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerChat(PlayerChatEvent event) {
		HungerGame chatterGame = GameManager.INSTANCE.getRawSession(event.getPlayer());
		for (Player p : new HashSet<Player>(event.getRecipients())) {
			HungerGame receipientGame = GameManager.INSTANCE.getRawSession(event.getPlayer());
			if (receipientGame != null && Config.getIsolatePlayerChat(receipientGame.getSetup())) {
				if (chatterGame != null) {
					if (chatterGame.compareTo(receipientGame) == 0 
						&& event.getPlayer().getLocation().getWorld() == p.getLocation().getWorld()) {
						float distanceRequired = Config.getChatDistance(receipientGame.getSetup());
						if (distanceRequired != 0 && event.getPlayer().getLocation().distance(p.getLocation()) >= distanceRequired) {
							if (HungerGames.checkPermission(event.getPlayer(), Perm.ADMIN_CHAT)) {
								if (event.getMessage().startsWith("hg ")) {
									event.setMessage(event.getMessage().substring(3));
									return;
								}
								if (event.getMessage().startsWith("hg")) {
									event.setMessage(event.getMessage().substring(2));
									return;
								}
							}
							//Logging.debug("Cancelling chat because too far.");
							event.getRecipients().remove(p);
						}
					}
					else {
						//Logging.debug("Cancelling chat because games are not the same or different worlds.");
						event.getRecipients().remove(p);
					}
				}
				else {
					//Logging.debug("Cancelling chat because chatter was not in a game.");
					event.getRecipients().remove(p);
				}
			}
		}
	}
}
