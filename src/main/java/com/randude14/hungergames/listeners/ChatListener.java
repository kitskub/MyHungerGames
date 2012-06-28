package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;


public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerChat(PlayerChatEvent event) {
		HungerGame chatterGame = GameManager.getSession(event.getPlayer());
		for (Player p : event.getRecipients()) {
			HungerGame receipientGame = GameManager.getSession(event.getPlayer());
			if (receipientGame != null) {
				if (Config.getIsolatePlayerChat(receipientGame.getSetup())) {
					if (chatterGame != null) {
						if (chatterGame.compareTo(receipientGame) == 0 
							&& event.getPlayer().getLocation().getWorld() == p.getLocation().getWorld()) {
							float distanceRequired = Config.getChatDistance(receipientGame.getSetup());
							if (distanceRequired != 0 && event.getPlayer().getLocation().distance(p.getLocation()) <= distanceRequired) {
								event.setCancelled(true);
							}
						}
						else {
							event.setCancelled(true);
						}
					}
					else {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
