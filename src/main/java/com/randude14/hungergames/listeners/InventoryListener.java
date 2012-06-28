package com.randude14.hungergames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void inventoryOpen(InventoryOpenEvent event) {
	}

}
