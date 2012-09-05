package com.randude14.hungergames.core.bukkit;

import com.randude14.hungergames.core.Vector;
import com.randude14.hungergames.core.blocks.Chest;

import java.util.Map;
import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BukkitChest extends Chest {

	public BukkitChest(BukkitWorld world, Vector pos) {
		super(Material.CHEST.getId(), world, pos);
	}

	/**
	* Get a container block's contents.
	*
	* @return
	*/
	@Override
	public com.randude14.hungergames.core.ItemStack[] getContents() {
		Block block = ((BukkitWorld) world).getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		if (block == null) {
		return new com.randude14.hungergames.core.ItemStack[0];
		}
		BlockState state = block.getState();
		if (!(state instanceof org.bukkit.inventory.InventoryHolder)) {
		return new com.randude14.hungergames.core.ItemStack[0];
		}

		org.bukkit.inventory.InventoryHolder container = (org.bukkit.inventory.InventoryHolder) state;
		Inventory inven = container.getInventory();
		if (container instanceof org.bukkit.block.Chest) {
		inven = BukkitUtil.getBlockInventory((org.bukkit.block.Chest) container);
		}
		int size = inven.getSize();
		com.randude14.hungergames.core.ItemStack[] contents = new com.randude14.hungergames.core.ItemStack[size];

		for (int i = 0; i < size; ++i) {
		ItemStack bukkitStack = inven.getItem(i);
		if (bukkitStack != null && bukkitStack.getTypeId() > 0) {
			contents[i] = new com.randude14.hungergames.core.ItemStack(
				bukkitStack.getTypeId(),
				bukkitStack.getAmount(),
				bukkitStack.getDurability());
			try {
			for (Map.Entry<Enchantment, Integer> entry : bukkitStack.getEnchantments().entrySet()) {
				contents[i].getEnchantments().put(entry.getKey().getId(), entry.getValue());
			}
			} catch (Throwable ignore) {}
		}
		}

		return contents;
	}

	/**
	* Set a container block's contents.
	*
	* @param contents
	* @return
	*/
	@Override
	public boolean setContents(com.randude14.hungergames.core.ItemStack[] contents) {
		Block block = ((BukkitWorld) world).getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		if (block == null) {
		return false;
		}
		BlockState state = block.getState();
		if (!(state instanceof org.bukkit.inventory.InventoryHolder)) {
		return false;
		}

		org.bukkit.inventory.InventoryHolder chest = (org.bukkit.inventory.InventoryHolder) state;
		Inventory inven = chest.getInventory();
		if (chest instanceof org.bukkit.block.Chest) {
		inven = BukkitUtil.getBlockInventory((org.bukkit.block.Chest) chest);
		}
		int size = inven.getSize();

		for (int i = 0; i < size; ++i) {
		if (i >= contents.length) {
			break;
		}

		if (contents[i] != null) {
			ItemStack toAdd = new ItemStack(contents[i].getType(), contents[i].getAmount(), contents[i].getData());
			try {
			for (Map.Entry<Integer, Integer> entry : contents[i].getEnchantments().entrySet()) {
				toAdd.addEnchantment(Enchantment.getById(entry.getKey()), entry.getValue());
			}
			} catch (Throwable ignore) {}
			inven.setItem(i, toAdd);
		} else {
			inven.setItem(i, null);
		}
		}

		return true;
	}

	public void setItem(int index, com.randude14.hungergames.core.ItemStack stack) {
		com.randude14.hungergames.core.ItemStack[] contents = getContents();
		contents[index] = stack;
		setContents(contents);
	}

	public com.randude14.hungergames.core.ItemStack[] addItem(com.randude14.hungergames.core.ItemStack... stack) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
