package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.games.HungerGame;
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
                Player player = (Player)event.getPlayer();
                HungerGame game = GameManager.getSession(player);
                if(game == null) return;
		if(!Config.getAutoAdd(game.getSetup())) return;
                game.addAndFillInventory(event.getInventory());
	}

}
