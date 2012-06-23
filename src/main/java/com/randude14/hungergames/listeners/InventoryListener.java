package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.games.HungerGame;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void inventoryOpen(InventoryOpenEvent event) {
		if(event.getInventory().getType() != InventoryType.CHEST) return;
                Player player = (Player) event.getPlayer();
                HungerGame game = GameManager.getPlayingSession(player);
                if(game == null) return;
		if(!Config.getAutoAdd(game.getSetup())) return;
		Logging.log(Level.FINEST, "Inventory opened and checking for fill. Player: {0} Holder: {1}", player.getName(), event.getInventory().getHolder().toString());
                game.addAndFillInventory(event.getInventory());
	}

}
